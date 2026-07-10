package com.javaee.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.javaee.blog.dto.request.BlogCreateRequest;
import com.javaee.blog.dto.vo.BlogListVO;
import com.javaee.blog.dto.vo.BlogVO;
import com.javaee.blog.dto.vo.TagVO;
import com.javaee.blog.entity.Blog;
import com.javaee.blog.entity.BlogTags;
import com.javaee.blog.entity.Tags;
import com.javaee.blog.mapper.BlogMapper;
import com.javaee.blog.mapper.BlogTagsMapper;
import com.javaee.blog.mapper.TagsMapper;
import com.javaee.blog.service.BlogService;
import com.javaee.blog.util.SlugUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BlogServiceImpl implements BlogService {

    private final BlogMapper blogMapper;
    private final TagsMapper tagsMapper;
    private final BlogTagsMapper blogTagsMapper;

    @Override
    public List<BlogListVO> list(String keyword, String tagSlug) {
        return queryList(keyword, tagSlug, null);
    }

    @Override
    public List<BlogListVO> list(String keyword, String tagSlug, int limit) {
        return queryList(keyword, tagSlug, limit);
    }

    private List<BlogListVO> queryList(String keyword, String tagSlug, Integer limit) {
        LambdaQueryWrapper<Blog> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Blog::getPublishedAt);
        if (keyword != null && !keyword.isBlank()) {
            wrapper.and(w -> w.like(Blog::getTitle, keyword).or().like(Blog::getContent, keyword));
        }
        if (tagSlug != null && !tagSlug.isBlank()) {
            Tags tag = tagsMapper.selectOne(new LambdaQueryWrapper<Tags>().eq(Tags::getSlug, tagSlug));
            if (tag == null) return Collections.emptyList();
            List<BlogTags> links = blogTagsMapper.selectList(
                    new LambdaQueryWrapper<BlogTags>().eq(BlogTags::getTagId, tag.getId()));
            List<Long> blogIds = links.stream().map(BlogTags::getBlogId).collect(Collectors.toList());
            if (blogIds.isEmpty()) return Collections.emptyList();
            wrapper.in(Blog::getId, blogIds);
        }
        if (limit != null) wrapper.last("LIMIT " + limit);
        List<Blog> blogs = blogMapper.selectList(wrapper);
        // 批量查所有 tag，避免 N+1
        Map<Long, List<TagVO>> tagMap = batchGetTags(blogs.stream().map(Blog::getId).collect(Collectors.toList()));
        return blogs.stream().map(b -> toListVO(b, tagMap.getOrDefault(b.getId(), Collections.emptyList()))).collect(Collectors.toList());
    }

    /** 批量查询多个 blog 的 tag，1 次查关联 + 1 次查 tag 实现 */
    private Map<Long, List<TagVO>> batchGetTags(List<Long> blogIds) {
        if (blogIds.isEmpty()) return Collections.emptyMap();
        List<BlogTags> links = blogTagsMapper.selectList(
                new LambdaQueryWrapper<BlogTags>().in(BlogTags::getBlogId, blogIds));
        if (links.isEmpty()) return Collections.emptyMap();
        List<Long> tagIds = links.stream().map(BlogTags::getTagId).distinct().collect(Collectors.toList());
        List<Tags> tags = tagsMapper.selectBatchIds(tagIds);
        Map<Long, TagVO> tagVoMap = tags.stream().collect(Collectors.toMap(Tags::getId, t -> {
            TagVO vo = new TagVO(); vo.setId(t.getId()); vo.setName(t.getName()); vo.setSlug(t.getSlug());
            return vo;
        }));
        Map<Long, List<TagVO>> result = new HashMap<>();
        for (BlogTags link : links) {
            TagVO vo = tagVoMap.get(link.getTagId());
            if (vo != null) result.computeIfAbsent(link.getBlogId(), k -> new ArrayList<>()).add(vo);
        }
        return result;
    }

    @Override
    public BlogVO getBySlug(String slug) {
        Blog blog = blogMapper.selectOne(new LambdaQueryWrapper<Blog>().eq(Blog::getSlug, slug));
        if (blog == null) return null;
        BlogVO vo = toVO(blog);

        // 上一篇（publishedAt 更早的最大一条）
        Blog prev = blogMapper.selectOne(new LambdaQueryWrapper<Blog>()
                .lt(Blog::getPublishedAt, blog.getPublishedAt())
                .orderByDesc(Blog::getPublishedAt).last("LIMIT 1"));
        if (prev != null) vo.setPrev(new BlogVO.PostNav(prev.getTitle(), prev.getSlug()));

        // 下一篇（publishedAt 更晚的最小一条）
        Blog next = blogMapper.selectOne(new LambdaQueryWrapper<Blog>()
                .gt(Blog::getPublishedAt, blog.getPublishedAt())
                .orderByAsc(Blog::getPublishedAt).last("LIMIT 1"));
        if (next != null) vo.setNext(new BlogVO.PostNav(next.getTitle(), next.getSlug()));

        return vo;
    }

    @Override
    @Transactional
    public void create(BlogCreateRequest request) {
        Blog blog = new Blog();
        blog.setTitle(request.getTitle());
        blog.setSlug(SlugUtil.toUniqueSlug(request.getTitle()));
        blog.setSummary(request.getSummary());
        blog.setContent(request.getContent());
        blog.setPublishedAt(LocalDateTime.now());
        blogMapper.insert(blog);

        handleTags(blog.getId(), request.getTagIds(), request.getNewTags());
    }

    @Override
    @Transactional
    public void update(Long id, BlogCreateRequest request) {
        Blog blog = blogMapper.selectById(id);
        if (blog == null) throw new java.util.NoSuchElementException("文章不存在");
        blog.setTitle(request.getTitle());
        blog.setSummary(request.getSummary());
        blog.setContent(request.getContent());
        blogMapper.updateById(blog);

        // 清除旧关联，重新绑定
        blogTagsMapper.delete(new LambdaQueryWrapper<BlogTags>().eq(BlogTags::getBlogId, id));
        handleTags(id, request.getTagIds(), request.getNewTags());
    }

    @Override
    public BlogVO getById(Long id) {
        Blog blog = blogMapper.selectById(id);
        if (blog == null) return null;
        return toVO(blog);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        blogTagsMapper.delete(new LambdaQueryWrapper<BlogTags>().eq(BlogTags::getBlogId, id));
        blogMapper.deleteById(id);
    }

    // ==================== 私有方法 ====================

    private BlogListVO toListVO(Blog blog, List<TagVO> tags) {
        BlogListVO vo = new BlogListVO();
        vo.setId(blog.getId());
        vo.setTitle(blog.getTitle());
        vo.setSlug(blog.getSlug());
        vo.setSummary(blog.getSummary());
        vo.setPublishedAt(blog.getPublishedAt());
        vo.setTags(tags);
        return vo;
    }

    private BlogVO toVO(Blog blog) {
        BlogVO vo = new BlogVO();
        vo.setId(blog.getId());
        vo.setTitle(blog.getTitle());
        vo.setSlug(blog.getSlug());
        vo.setSummary(blog.getSummary());
        vo.setContent(blog.getContent());
        vo.setPublishedAt(blog.getPublishedAt());
        vo.setTags(getTagsByBlogId(blog.getId()));
        return vo;
    }

    private List<TagVO> getTagsByBlogId(Long blogId) {
        List<BlogTags> links = blogTagsMapper.selectList(
                new LambdaQueryWrapper<BlogTags>().eq(BlogTags::getBlogId, blogId));
        if (links.isEmpty()) return Collections.emptyList();

        List<Long> tagIds = links.stream().map(BlogTags::getTagId).collect(Collectors.toList());
        List<Tags> tags = tagsMapper.selectBatchIds(tagIds);
        return tags.stream().map(tag -> {
            TagVO vo = new TagVO();
            vo.setId(tag.getId());
            vo.setName(tag.getName());
            vo.setSlug(tag.getSlug());
            return vo;
        }).collect(Collectors.toList());
    }

    private void handleTags(Long blogId, List<Long> tagIds, List<String> newTags) {
        // 已有标签
        if (tagIds != null) {
            for (Long tagId : tagIds) {
                BlogTags link = new BlogTags();
                link.setBlogId(blogId);
                link.setTagId(tagId);
                blogTagsMapper.insert(link);
            }
        }
        // 新标签（当场创建，已存在则复用）
        if (newTags != null) {
            for (String name : newTags) {
                Tags tag = tagsMapper.selectOne(
                        new LambdaQueryWrapper<Tags>().eq(Tags::getName, name));
                if (tag == null) {
                    tag = new Tags();
                    tag.setName(name);
                    tag.setSlug(SlugUtil.toSlug(name));
                    tagsMapper.insert(tag);
                }

                BlogTags link = new BlogTags();
                link.setBlogId(blogId);
                link.setTagId(tag.getId());
                blogTagsMapper.insert(link);
            }
        }
    }

}

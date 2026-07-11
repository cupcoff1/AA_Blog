package com.javaee.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.javaee.blog.dto.request.BlogCreateRequest;
import com.javaee.blog.dto.vo.BlogListVO;
import com.javaee.blog.dto.vo.BlogVO;
import com.javaee.blog.dto.vo.TagVO;
import com.javaee.blog.entity.Blog;
import com.javaee.blog.entity.association.BlogTags;
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
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BlogServiceImpl implements BlogService {

    private final BlogMapper blogMapper;
    private final TagsMapper tagsMapper;
    private final BlogTagsMapper blogTagsMapper;

    // ==================== 私有方法 ====================

    /**
     * 列表查询核心逻辑。动态拼接 WHERE + 批量查 tag 避免 N+1。
     * @param limit null = 不限，非 null = LIMIT 子句
     */
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
        Map<Long, List<TagVO>> tagMap = batchGetTags(blogs.stream().map(Blog::getId).collect(Collectors.toList()));
        return blogs.stream().map(b -> toListVO(b, tagMap.getOrDefault(b.getId(), Collections.emptyList()))).collect(Collectors.toList());
    }

    /**
     * 批量查询多个博客的标签。2 次 DB 查询替代 N 次单条查询。
     * @param blogIds 博客 ID 列表
     * @return blogId → TagVO 列表的映射
     */
    private Map<Long, List<TagVO>> batchGetTags(List<Long> blogIds) {
        if (blogIds.isEmpty()) return Collections.emptyMap();
        List<BlogTags> links = blogTagsMapper.selectList(
                new LambdaQueryWrapper<BlogTags>().in(BlogTags::getBlogId, blogIds));
        if (links.isEmpty()) return Collections.emptyMap();
        List<Long> tagIds = links.stream().map(BlogTags::getTagId).distinct().collect(Collectors.toList());
        List<Tags> tags = tagsMapper.selectBatchIds(tagIds);
        Map<Long, TagVO> tagVoMap = tags.stream().collect(Collectors.toMap(Tags::getId, TagVO::from));
        Map<Long, List<TagVO>> result = new HashMap<>();
        for (BlogTags link : links) {
            TagVO vo = tagVoMap.get(link.getTagId());
            if (vo != null) result.computeIfAbsent(link.getBlogId(), k -> new ArrayList<>()).add(vo);
        }
        return result;
    }

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


    /** 单条博客的标签查询（详情页用，N=1 无性能问题） */
    private List<TagVO> getTagsByBlogId(Long blogId) {
        List<BlogTags> links = blogTagsMapper.selectList(
                new LambdaQueryWrapper<BlogTags>().eq(BlogTags::getBlogId, blogId));
        if (links.isEmpty()) return Collections.emptyList();
        List<Long> tagIds = links.stream().map(BlogTags::getTagId).collect(Collectors.toList());
        return tagsMapper.selectBatchIds(tagIds).stream().map(TagVO::from).collect(Collectors.toList());
    }

    /**
     * 维护博客-标签关联。已有标签直接关联，新标签批量查是否存在，不存在则创建再关联。
     */
    private void handleTags(Long blogId, List<Long> tagIds, List<String> newTags) {
        if (tagIds != null) {
            for (Long tagId : tagIds) {
                BlogTags link = new BlogTags();
                link.setBlogId(blogId);
                link.setTagId(tagId);
                blogTagsMapper.insert(link);
            }
        }
        if (newTags != null && !newTags.isEmpty()) {
            List<Tags> existing = tagsMapper.selectList(
                    new LambdaQueryWrapper<Tags>().in(Tags::getName, newTags));
            Set<String> existingNames = existing.stream().map(Tags::getName).collect(Collectors.toSet());
            for (String name : newTags) {
                Tags tag;
                if (existingNames.contains(name)) {
                    tag = existing.stream().filter(t -> t.getName().equals(name)).findFirst()
                                    .orElseThrow(() -> new IllegalStateException("标签数据异常"));
                } else {
                    tag = new Tags();
                    tag.setName(name);
                    tag.setSlug(SlugUtil.toSlug(name));
                    tagsMapper.insert(tag);
                    existingNames.add(name);
                }
                BlogTags link = new BlogTags();
                link.setBlogId(blogId);
                link.setTagId(tag.getId());
                blogTagsMapper.insert(link);
            }
        }
    }

    // ==================== 公开方法 ====================

    /**
     * 前台博客列表（全量）。
     * @param keyword 标题/正文关键词搜索，null 或空字符串表示不筛选
     * @param tagSlug 标签 slug 筛选
     * @return 按发布日期倒序的全部博客（含标签，不含正文）
     */
    @Override
    public List<BlogListVO> list(String keyword, String tagSlug) {
        return queryList(keyword, tagSlug, null);
    }

    /**
     * 前台博客列表（限制条数），供首页等场景使用。
     * @param limit 最大返回条数
     */
    @Override
    public List<BlogListVO> list(String keyword, String tagSlug, int limit) {
        return queryList(keyword, tagSlug, limit);
    }

    /**
     * 前台博客详情（含正文、标签、上一篇/下一篇导航）。
     * @param slug URL 标识
     * @return null 表示博客不存在
     */
    @Override
    public BlogVO getBySlug(String slug) {
        Blog blog = blogMapper.selectOne(new LambdaQueryWrapper<Blog>().eq(Blog::getSlug, slug));
        if (blog == null) return null;
        BlogVO vo = toVO(blog);
        Blog prev = blogMapper.selectOne(new LambdaQueryWrapper<Blog>()
                .lt(Blog::getPublishedAt, blog.getPublishedAt())
                .orderByDesc(Blog::getPublishedAt).last("LIMIT 1"));
        if (prev != null) vo.setPrev(new BlogVO.PostNav(prev.getTitle(), prev.getSlug()));
        Blog next = blogMapper.selectOne(new LambdaQueryWrapper<Blog>()
                .gt(Blog::getPublishedAt, blog.getPublishedAt())
                .orderByAsc(Blog::getPublishedAt).last("LIMIT 1"));
        if (next != null) vo.setNext(new BlogVO.PostNav(next.getTitle(), next.getSlug()));
        return vo;
    }

    /**
     * 后台创建博客。slug 自动生成（带随机后缀防并发冲突），发布日期取当前时间。
     */
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

    /**
     * 后台更新博客。不存在时抛 NoSuchElementException → 404。slug/发布日期不变。
     */
    @Override
    @Transactional
    public void update(Long id, BlogCreateRequest request) {
        Blog blog = blogMapper.selectById(id);
        if (blog == null) throw new NoSuchElementException("文章不存在");
        blog.setTitle(request.getTitle());
        blog.setSummary(request.getSummary());
        blog.setContent(request.getContent());
        blogMapper.updateById(blog);
        blogTagsMapper.delete(new LambdaQueryWrapper<BlogTags>().eq(BlogTags::getBlogId, id));
        handleTags(id, request.getTagIds(), request.getNewTags());
    }

    /**
     * 后台获取博客详情（供编辑页加载）。null = 不存在
     */
    @Override
    public BlogVO getById(Long id) {
        Blog blog = blogMapper.selectById(id);
        if (blog == null) return null;
        return toVO(blog);
    }

    /**
     * 后台删除博客。先删标签关联，再删博客本体。
     */
    @Override
    @Transactional
    public void delete(Long id) {
        blogTagsMapper.delete(new LambdaQueryWrapper<BlogTags>().eq(BlogTags::getBlogId, id));
        blogMapper.deleteById(id);
    }
}

package com.javaee.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.javaee.blog.dto.request.CommentCreateRequest;
import com.javaee.blog.dto.vo.CommentAdminVO;
import com.javaee.blog.dto.vo.CommentVO;
import com.javaee.blog.entity.Blog;
import com.javaee.blog.entity.Comment;
import com.javaee.blog.mapper.BlogMapper;
import com.javaee.blog.mapper.CommentMapper;
import com.javaee.blog.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentMapper commentMapper;
    private final BlogMapper blogMapper;

    @Override
    public List<CommentVO> listBySlug(String slug) {
        Blog blog = blogMapper.selectOne(new LambdaQueryWrapper<Blog>().eq(Blog::getSlug, slug));
        if (blog == null) return Collections.emptyList();

        List<Comment> all = commentMapper.selectList(
                new LambdaQueryWrapper<Comment>().eq(Comment::getBlogId, blog.getId())
                        .orderByAsc(Comment::getCreatedAt));

        Map<Long, List<Comment>> replyMap = all.stream()
                .filter(c -> c.getParentId() != null)
                .collect(Collectors.groupingBy(Comment::getParentId));

        return all.stream()
                .filter(c -> c.getParentId() == null)
                .map(c -> toVO(c, replyMap))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void create(String slug, CommentCreateRequest request, String authorName, String authorAvatar) {
        Blog blog = blogMapper.selectOne(new LambdaQueryWrapper<Blog>().eq(Blog::getSlug, slug));
        if (blog == null) return;

        if (request.getParentId() != null) {
            Comment parent = commentMapper.selectById(request.getParentId());
            if (parent == null || parent.getParentId() != null) return;
        }

        Comment comment = new Comment();
        comment.setBlogId(blog.getId());
        comment.setContent(request.getContent());
        comment.setParentId(request.getParentId());
        comment.setAuthorName(authorName);
        comment.setAuthorAvatar(authorAvatar);
        commentMapper.insert(comment);
    }

    @Override
    @Transactional
    public void delete(Long commentId, String authorName) {
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null) return;
        if (!comment.getAuthorName().equals(authorName)) return;

        commentMapper.delete(new LambdaQueryWrapper<Comment>().eq(Comment::getParentId, commentId));
        commentMapper.deleteById(commentId);
    }

    @Override
    public List<CommentAdminVO> adminList() {
        return queryAdminList(null);
    }

    @Override
    public List<CommentAdminVO> adminList(int limit) {
        return queryAdminList(limit);
    }

    private List<CommentAdminVO> queryAdminList(Integer limit) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<Comment>()
                .orderByDesc(Comment::getCreatedAt);
        if (limit != null) wrapper.last("LIMIT " + limit);
        List<Comment> comments = commentMapper.selectList(wrapper);
        return comments.stream().map(c -> {
            Blog blog = blogMapper.selectById(c.getBlogId());
            CommentAdminVO vo = new CommentAdminVO();
            vo.setId(c.getId());
            vo.setContent(c.getContent());
            vo.setParentId(c.getParentId());
            vo.setAuthorName(c.getAuthorName());
            vo.setBlogId(c.getBlogId());
            vo.setBlogTitle(blog != null ? blog.getTitle() : "");
            vo.setCreatedAt(c.getCreatedAt());
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        commentMapper.delete(new LambdaQueryWrapper<Comment>().eq(Comment::getParentId, id));
        commentMapper.deleteById(id);
    }

    private CommentVO toVO(Comment comment, Map<Long, List<Comment>> replyMap) {
        CommentVO vo = new CommentVO();
        vo.setId(comment.getId());
        vo.setContent(comment.getContent());
        vo.setParentId(comment.getParentId());
        vo.setAuthorName(comment.getAuthorName());
        vo.setAuthorAvatar(comment.getAuthorAvatar());
        vo.setCreatedAt(comment.getCreatedAt());

        List<Comment> replies = replyMap.getOrDefault(comment.getId(), Collections.emptyList());
        vo.setChildren(replies.stream()
                .map(r -> toVO(r, Collections.emptyMap()))
                .collect(Collectors.toList()));
        return vo;
    }
}

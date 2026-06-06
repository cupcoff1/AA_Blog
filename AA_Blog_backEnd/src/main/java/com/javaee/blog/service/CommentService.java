package com.javaee.blog.service;

import com.javaee.blog.dto.request.CommentCreateRequest;
import com.javaee.blog.dto.vo.CommentAdminVO;
import com.javaee.blog.dto.vo.CommentVO;

import java.util.List;

public interface CommentService {

    /** 获取文章评论列表（嵌套结构） */
    List<CommentVO> listBySlug(String slug);

    /** 发表评论 */
    void create(String slug, CommentCreateRequest request, String authorName, String authorAvatar);

    /** 删除自己的评论（级联删除子回复） */
    void delete(Long commentId, String authorName);

    /** 管理员：评论列表（平铺，含 blog_title） */
    List<CommentAdminVO> adminList();

    /** 管理员：最近 N 条评论 */
    List<CommentAdminVO> adminList(int limit);

    /** 管理员：删除任意评论（级联删除子回复） */
    void deleteById(Long id);
}

package com.javaee.blog.service;

import com.javaee.blog.dto.request.BlogCreateRequest;
import com.javaee.blog.dto.vo.BlogListVO;
import com.javaee.blog.dto.vo.BlogVO;

import java.util.List;

public interface BlogService {

    /** 前台：文章列表（支持搜索和标签筛选），不含正文 */
    List<BlogListVO> list(String keyword, String tagSlug);

    /** 取前 N 条（供首页等场景使用） */
    List<BlogListVO> list(String keyword, String tagSlug, int limit);

    /** 前台：文章详情 */
    BlogVO getBySlug(String slug);

    /** 后台：创建文章 */
    void create(BlogCreateRequest request);

    /** 后台：编辑文章 */
    void update(Long id, BlogCreateRequest request);

    /** 后台：获取文章详情（供编辑页使用） */
    BlogVO getById(Long id);

    /** 后台：删除文章 */
    void delete(Long id);
}

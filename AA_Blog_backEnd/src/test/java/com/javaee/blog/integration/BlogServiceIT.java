package com.javaee.blog.integration;

import com.javaee.blog.dto.request.BlogCreateRequest;
import com.javaee.blog.dto.vo.BlogVO;
import com.javaee.blog.entity.Blog;
import com.javaee.blog.mapper.BlogMapper;
import com.javaee.blog.service.BlogService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * BlogService 集成测试。
 * 使用真实 MySQL + Liquibase 建表，@Transactional 保证每个测试后自动回滚。
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class BlogServiceIT {

    @Autowired
    private BlogService blogService;

    @Autowired
    private BlogMapper blogMapper;

    // ==================== create + getById ====================

    @Test
    void createAndGetById_shouldWork() {
        BlogCreateRequest request = new BlogCreateRequest();
        request.setTitle("集成测试文章");
        request.setSummary("摘要");
        request.setContent("正文内容");

        blogService.create(request);

        // 真查数据库
        List<Blog> blogs = blogMapper.selectList(null);
        assertEquals(1, blogs.size());
        assertEquals("集成测试文章", blogs.get(0).getTitle());
        assertNotNull(blogs.get(0).getSlug());

        // 通过 Service 查
        BlogVO vo = blogService.getById(blogs.get(0).getId());
        assertEquals("正文内容", vo.getContent());
    }

    // ==================== update ====================

    @Test
    void update_shouldChangeTitleOnly() {
        BlogCreateRequest createReq = new BlogCreateRequest();
        createReq.setTitle("原标题");
        createReq.setSummary("原摘要");
        createReq.setContent("原内容");
        blogService.create(createReq);

        Long id = blogMapper.selectList(null).get(0).getId();

        BlogCreateRequest updateReq = new BlogCreateRequest();
        updateReq.setTitle("新标题");
        updateReq.setSummary("新摘要");
        updateReq.setContent("新内容");
        blogService.update(id, updateReq);

        Blog updated = blogMapper.selectById(id);
        assertEquals("新标题", updated.getTitle());
        assertEquals("新内容", updated.getContent());
        // slug 不变
        assertNotNull(updated.getSlug());
    }

    // ==================== getById 不存在 ====================

    @Test
    void getById_shouldThrow_whenNotFound() {
        assertThrows(NoSuchElementException.class, () -> blogService.getById(999999L));
    }

    // ==================== delete ====================

    @Test
    void delete_shouldRemoveBlog() {
        BlogCreateRequest request = new BlogCreateRequest();
        request.setTitle("待删除");
        request.setSummary("...");
        request.setContent("...");
        blogService.create(request);

        Long id = blogMapper.selectList(null).get(0).getId();
        blogService.delete(id);

        assertNull(blogMapper.selectById(id));
    }

    // ==================== list ====================

    @Test
    void list_shouldReturnAllBlogs() {
        BlogCreateRequest req = new BlogCreateRequest();
        req.setTitle("文章A"); req.setSummary("..."); req.setContent("...");
        blogService.create(req);
        req.setTitle("文章B");
        blogService.create(req);

        assertEquals(2, blogService.list(null, null).size());
        assertEquals(2, blogService.list(null, null, 5).size());   // limit
        assertEquals(1, blogService.list(null, null, 1).size());
    }

    // ==================== getBySlug ====================

    @Test
    void getBySlug_shouldReturnBlog() {
        BlogCreateRequest req = new BlogCreateRequest();
        req.setTitle("Slug 测试"); req.setSummary("..."); req.setContent("...");
        blogService.create(req);

        Blog created = blogMapper.selectList(null).get(0);
        BlogVO vo = blogService.getBySlug(created.getSlug());

        assertEquals("Slug 测试", vo.getTitle());
        assertNotNull(vo.getSlug());
    }

    // ==================== updateTags ====================

    @Test
    void updateTags_shouldReplaceTags() {
        BlogCreateRequest req = new BlogCreateRequest();
        req.setTitle("标签测试"); req.setSummary("..."); req.setContent("...");
        req.setTagIds(Collections.emptyList());
        req.setNewTags(List.of("Java"));
        blogService.create(req);

        Long id = blogMapper.selectList(null).get(0).getId();

        // 更换标签
        blogService.updateTags(id, Collections.emptyList(), List.of("Spring"));

        BlogVO vo = blogService.getById(id);
        assertEquals(1, vo.getTags().size());
        assertEquals("Spring", vo.getTags().get(0).getName());
    }
}

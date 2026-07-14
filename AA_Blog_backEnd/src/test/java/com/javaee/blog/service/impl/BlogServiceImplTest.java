package com.javaee.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.javaee.blog.dto.request.BlogCreateRequest;
import com.javaee.blog.dto.vo.BlogListVO;
import com.javaee.blog.dto.vo.BlogVO;
import com.javaee.blog.entity.Blog;
import com.javaee.blog.entity.association.BlogTags;
import com.javaee.blog.entity.Tag;
import com.javaee.blog.mapper.BlogMapper;
import com.javaee.blog.mapper.BlogTagsMapper;
import com.javaee.blog.mapper.TagMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BlogServiceImplTest {

    @Mock
    private BlogMapper blogMapper;

    @Mock
    private TagMapper tagMapper;

    @Mock
    private BlogTagsMapper blogTagsMapper;

    @InjectMocks
    private BlogServiceImpl service;

    private Blog blog;

    @BeforeEach
    void setUp() {
        blog = new Blog();
        blog.setId(1L);
        blog.setTitle("Test Blog");
        blog.setSlug("test-blog");
        blog.setSummary("A summary");
        blog.setContent("Full content");
        blog.setPublishedAt(LocalDateTime.of(2026, 1, 1, 0, 0));
    }

    // ==================== getBySlug ====================

    @Test
    void getBySlug_shouldReturnNull_whenNotFound() {
        when(blogMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

        assertNull(service.getBySlug("no-such-slug"));
    }

    @Test
    void getBySlug_shouldReturnBlogWithTags() {
        when(blogMapper.selectOne(any(LambdaQueryWrapper.class)))
                .thenReturn(blog)   // 查主博客
                .thenReturn(null)   // prev
                .thenReturn(null);  // next
        when(blogTagsMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());

        BlogVO result = service.getBySlug("test-blog");

        assertNotNull(result);
        assertEquals(blog.getTitle(), result.getTitle());
        assertEquals(blog.getContent(), result.getContent());
        assertNull(result.getPrev());
        assertNull(result.getNext());
    }

    // ==================== list ====================

    @Test
    void list_shouldReturnBlogs() {
        when(blogMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of(blog));
        when(blogTagsMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());

        List<BlogListVO> result = service.list(null, null);

        assertEquals(1, result.size());
        assertEquals(blog.getTitle(), result.get(0).getTitle());
    }

    // ==================== getById ====================

    @Test
    void getById_shouldThrow_whenNotFound() {
        when(blogMapper.selectById(99L)).thenReturn(null);

        assertThrows(NoSuchElementException.class, () -> service.getById(99L));
    }

    @Test
    void getById_shouldReturnBlog() {
        when(blogMapper.selectById(1L)).thenReturn(blog);
        when(blogTagsMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());

        BlogVO result = service.getById(1L);

        assertNotNull(result);
        assertEquals(blog.getTitle(), result.getTitle());
    }

    // ==================== create ====================

    @Test
    void create_shouldInsertBlog_whenNoTags() {
        BlogCreateRequest request = new BlogCreateRequest();
        request.setTitle("No Tags");
        request.setSummary("...");
        request.setContent("...");

        service.create(request);

        verify(blogMapper).insert(any(Blog.class));
        verify(blogTagsMapper, never()).insert(any(BlogTags.class));
    }

    @Test
    void create_shouldInsertBlogAndHandleTags() {
        BlogCreateRequest request = new BlogCreateRequest();
        request.setTitle("New Blog");
        request.setSummary("New summary");
        request.setContent("New content");
        request.setTagIds(List.of(1L));
        request.setNewTags(List.of("Java"));

        when(tagMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());

        service.create(request);

        verify(blogMapper).insert(any(Blog.class));
        verify(blogTagsMapper, atLeastOnce()).insert(any(BlogTags.class));
    }

    // ==================== update ====================

    @Test
    void update_shouldThrow_whenNotFound() {
        BlogCreateRequest request = new BlogCreateRequest();
        request.setTitle("X");
        request.setSummary("X");
        request.setContent("X");
        when(blogMapper.selectById(99L)).thenReturn(null);

        assertThrows(NoSuchElementException.class, () -> service.update(99L, request));
    }

    @Test
    void update_shouldOnlyUpdateContent_notTags() {
        BlogCreateRequest request = new BlogCreateRequest();
        request.setTitle("Updated");
        request.setSummary("Updated summary");
        request.setContent("Updated content");
        request.setTagIds(List.of(2L));
        request.setNewTags(List.of("Vue"));
        when(blogMapper.selectById(1L)).thenReturn(blog);

        service.update(1L, request);

        verify(blogMapper).updateById(blog);
        assertEquals("Updated", blog.getTitle());
        verify(blogTagsMapper, never()).delete(any(LambdaQueryWrapper.class));
    }

    @Test
    void updateTags_shouldClearOldAndRebuild() {
        when(blogMapper.selectById(1L)).thenReturn(blog);
        when(tagMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());

        service.updateTags(1L, List.of(2L), List.of("Vue"));

        verify(blogTagsMapper).delete(any(LambdaQueryWrapper.class));
        verify(blogTagsMapper, atLeastOnce()).insert(any(BlogTags.class));
    }

    @Test
    void updateTags_shouldThrow_whenBlogNotFound() {
        when(blogMapper.selectById(99L)).thenReturn(null);

        assertThrows(NoSuchElementException.class,
                () -> service.updateTags(99L, List.of(1L), List.of()));
    }

    // ==================== delete ====================

    @Test
    void delete_shouldRemoveTagsAndBlog() {
        service.delete(1L);

        verify(blogTagsMapper).delete(any(LambdaQueryWrapper.class));
        verify(blogMapper).deleteById(1L);
    }
}

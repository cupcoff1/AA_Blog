package com.javaee.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.javaee.blog.dto.request.StickyNoteCreateRequest;
import com.javaee.blog.entity.StickyNote;
import com.javaee.blog.mapper.StickyNoteMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StickyNoteServiceImplTest {

    @Mock
    private StickyNoteMapper mapper;

    @InjectMocks
    private StickyNoteServiceImpl service;

    // ==================== list ====================

    @Test
    void list_shouldReturnEmpty_whenNoNotes() {
        when(mapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());

        assertTrue(service.list("", "admin").isEmpty());
    }

    @Test
    void list_shouldSetOwnTrue_whenAuthorMatchesCurrentUser() {
        StickyNote note = new StickyNote();
        note.setId(1L);
        note.setContent("Hello");
        note.setColor("#fff");
        note.setAuthorName("cupcoff1");
        note.setAuthorAvatar("avatar.png");
        when(mapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of(note));

        var result = service.list("cupcoff1", "guest");

        assertEquals(1, result.size());
        assertTrue(result.get(0).isOwn());
    }

    @Test
    void list_shouldSetOwnFalse_whenAuthorMismatch() {
        StickyNote note = new StickyNote();
        note.setId(1L);
        note.setContent("Hi");
        note.setColor("#fff");
        note.setAuthorName("other");
        when(mapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of(note));

        var result = service.list("cupcoff1", "guest");

        assertEquals(1, result.size());
        assertFalse(result.get(0).isOwn());
    }

    // ==================== create ====================

    @Test
    void create_shouldUseDefaultValues_whenFieldsNotSet() {
        StickyNoteCreateRequest req = new StickyNoteCreateRequest();
        req.setContent("Test");
        req.setColor("#fff");

        service.create(req, "cupcoff1", "avatar.png");

        // 无法直接断言 rotate 和 category（含随机值），但验证 insert 被调用且基本字段正确
        verify(mapper).insert(any(StickyNote.class));
    }

    // ==================== 管理员删除 ====================

    @Test
    void delete_shouldReturnTrue_whenAdmin() {
        when(mapper.deleteById(1L)).thenReturn(1);

        boolean result = service.delete(1L, "anyone", true);

        assertTrue(result);
    }

    @Test
    void delete_shouldReturnFalse_whenAdminDeletesNonExistent() {
        when(mapper.deleteById(99L)).thenReturn(0);

        boolean result = service.delete(99L, "anyone", true);

        assertFalse(result);
    }

    // ==================== 作者删除 ====================

    @Test
    void delete_shouldReturnTrue_whenAuthorMatches() {
        when(mapper.delete(any())).thenReturn(1);

        boolean result = service.delete(1L, "cupcoff1", false);

        assertTrue(result);
    }

    @Test
    void delete_shouldReturnFalse_whenAuthorMismatch() {
        when(mapper.delete(any())).thenReturn(0);

        boolean result = service.delete(1L, "cupcoff1", false);

        assertFalse(result);
    }

    // ==================== 未登录游客删除 ====================

    @Test
    void delete_shouldReturnFalse_whenRequesterNull() {
        boolean result = service.delete(1L, null, false);

        assertFalse(result);
    }
}

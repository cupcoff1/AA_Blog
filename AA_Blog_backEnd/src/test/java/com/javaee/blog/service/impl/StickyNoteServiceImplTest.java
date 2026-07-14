package com.javaee.blog.service.impl;

import com.javaee.blog.mapper.StickyNoteMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StickyNoteServiceImplTest {

    @Mock
    private StickyNoteMapper mapper;

    @InjectMocks
    private StickyNoteServiceImpl service;

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

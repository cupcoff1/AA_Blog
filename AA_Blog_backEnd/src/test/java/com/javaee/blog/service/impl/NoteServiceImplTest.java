package com.javaee.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.javaee.blog.dto.request.NoteCreateRequest;
import com.javaee.blog.dto.vo.NoteVO;
import com.javaee.blog.entity.Note;
import com.javaee.blog.entity.association.NoteTags;
import com.javaee.blog.mapper.NoteMapper;
import com.javaee.blog.mapper.NoteTagsMapper;
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
class NoteServiceImplTest {

    @Mock
    private NoteMapper noteMapper;

    @Mock
    private TagMapper tagMapper;

    @Mock
    private NoteTagsMapper noteTagsMapper;

    @InjectMocks
    private NoteServiceImpl service;

    private Note note;

    @BeforeEach
    void setUp() {
        note = new Note();
        note.setId(1L);
        note.setTitle("Test Note");
        note.setSlug("test-note");
        note.setContent("Full content");
        note.setPublishedAt(LocalDateTime.of(2026, 1, 1, 0, 0));
    }

    // ==================== getById ====================

    @Test
    void getById_shouldThrow_whenNotFound() {
        when(noteMapper.selectById(99L)).thenReturn(null);

        assertThrows(NoSuchElementException.class, () -> service.getById(99L));
    }

    @Test
    void getById_shouldReturnNote() {
        when(noteMapper.selectById(1L)).thenReturn(note);
        when(noteTagsMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());

        NoteVO result = service.getById(1L);

        assertNotNull(result);
        assertEquals(note.getTitle(), result.getTitle());
        assertEquals(note.getContent(), result.getContent());
    }

    // ==================== list ====================

    @Test
    void list_shouldReturnNotes() {
        when(noteMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of(note));
        when(noteTagsMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());

        List<NoteVO> result = service.list(null, null);

        assertEquals(1, result.size());
        assertEquals(note.getTitle(), result.get(0).getTitle());
    }

    // ==================== create ====================

    @Test
    void create_shouldInsertNoteAndHandleTags() {
        NoteCreateRequest request = new NoteCreateRequest();
        request.setTitle("New Note");
        request.setContent("Content");
        request.setTagIds(List.of(1L));
        when(tagMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());

        service.create(request);

        verify(noteMapper).insert(any(Note.class));
        verify(noteTagsMapper, atLeastOnce()).insert(any(NoteTags.class));
    }

    // ==================== update ====================

    @Test
    void update_shouldThrow_whenNotFound() {
        NoteCreateRequest request = new NoteCreateRequest();
        request.setTitle("X");
        request.setContent("X");
        when(noteMapper.selectById(99L)).thenReturn(null);

        assertThrows(NoSuchElementException.class, () -> service.update(99L, request));
    }

    @Test
    void update_shouldOnlyUpdateContent() {
        NoteCreateRequest request = new NoteCreateRequest();
        request.setTitle("Updated");
        request.setContent("Updated content");
        when(noteMapper.selectById(1L)).thenReturn(note);

        service.update(1L, request);

        verify(noteMapper).updateById(note);
        assertEquals("Updated", note.getTitle());
        verify(noteTagsMapper, never()).delete(any(LambdaQueryWrapper.class));
    }

    // ==================== updateTags ====================

    @Test
    void updateTags_shouldThrow_whenNoteNotFound() {
        when(noteMapper.selectById(99L)).thenReturn(null);

        assertThrows(NoSuchElementException.class,
                () -> service.updateTags(99L, List.of(1L), List.of()));
    }

    @Test
    void updateTags_shouldClearOldAndRebuild() {
        when(noteMapper.selectById(1L)).thenReturn(note);
        when(tagMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());

        service.updateTags(1L, List.of(2L), List.of("Vue"));

        verify(noteTagsMapper).delete(any(LambdaQueryWrapper.class));
        verify(noteTagsMapper, atLeastOnce()).insert(any(NoteTags.class));
    }

    // ==================== delete ====================

    @Test
    void delete_shouldRemoveTagsAndNote() {
        service.delete(1L);

        verify(noteTagsMapper).delete(any(LambdaQueryWrapper.class));
        verify(noteMapper).deleteById(1L);
    }
}

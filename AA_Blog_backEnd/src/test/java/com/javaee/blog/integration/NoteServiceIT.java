package com.javaee.blog.integration;

import com.javaee.blog.dto.request.NoteCreateRequest;
import com.javaee.blog.dto.vo.NoteVO;
import com.javaee.blog.entity.Note;
import com.javaee.blog.mapper.NoteMapper;
import com.javaee.blog.service.NoteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class NoteServiceIT {

    @Autowired
    private NoteService noteService;

    @Autowired
    private NoteMapper noteMapper;

    private NoteCreateRequest createReq(String title, String content) {
        NoteCreateRequest req = new NoteCreateRequest();
        req.setTitle(title);
        req.setContent(content);
        return req;
    }

    @Test
    void createAndGetById_shouldWork() {
        noteService.create(createReq("笔记A", "正文"));

        Note saved = noteMapper.selectList(null).get(0);
        assertEquals("笔记A", saved.getTitle());
        assertEquals("正文", saved.getContent());
        assertNotNull(saved.getSlug());

        NoteVO vo = noteService.getById(saved.getId());
        assertEquals("笔记A", vo.getTitle());
    }

    @Test
    void getById_shouldThrow_whenNotFound() {
        assertThrows(NoSuchElementException.class, () -> noteService.getById(999999L));
    }

    @Test
    void update_shouldChangeContent() {
        noteService.create(createReq("旧标题", "旧正文"));
        Long id = noteMapper.selectList(null).get(0).getId();

        NoteCreateRequest updateReq = createReq("新标题", "新正文");
        noteService.update(id, updateReq);

        Note updated = noteMapper.selectById(id);
        assertEquals("新标题", updated.getTitle());
        assertEquals("新正文", updated.getContent());
    }

    @Test
    void list_shouldReturnNotes() {
        noteService.create(createReq("A", "..."));
        noteService.create(createReq("B", "..."));
        assertEquals(2, noteService.list(null, null).size());
    }

    @Test
    void delete_shouldRemoveNote() {
        noteService.create(createReq("删除", "..."));
        Long id = noteMapper.selectList(null).get(0).getId();
        noteService.delete(id);
        assertNull(noteMapper.selectById(id));
    }
}

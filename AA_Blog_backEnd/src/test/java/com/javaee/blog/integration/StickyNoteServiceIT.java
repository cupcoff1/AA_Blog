package com.javaee.blog.integration;

import com.javaee.blog.dto.request.StickyNoteCreateRequest;
import com.javaee.blog.entity.StickyNote;
import com.javaee.blog.mapper.StickyNoteMapper;
import com.javaee.blog.service.StickyNoteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class StickyNoteServiceIT {

    @Autowired
    private StickyNoteService service;

    @Autowired
    private StickyNoteMapper mapper;

    private StickyNoteCreateRequest createReq(String content) {
        StickyNoteCreateRequest req = new StickyNoteCreateRequest();
        req.setContent(content);
        req.setColor("#fff3cd");
        req.setRotate(0);
        return req;
    }

    // ==================== create ====================

    @Test
    void create_shouldSaveNote() {
        service.create(createReq("Hello"), "cupcoff1", "avatar.png");

        StickyNote saved = mapper.selectList(null).get(0);
        assertEquals("Hello", saved.getContent());
        assertEquals("cupcoff1", saved.getAuthorName());
        assertEquals("avatar.png", saved.getAuthorAvatar());
        assertEquals("to_aa", saved.getCategory());   // 默认分类
    }

    @Test
    void create_shouldUseAdminDefaults() {
        service.create(createReq("Admin note"), "", "");

        StickyNote saved = mapper.selectList(null).get(0);
        assertEquals("", saved.getAuthorName());
    }

    // ==================== list ====================

    @Test
    void list_shouldSeparateAdminAndGuest() {
        service.create(createReq("管理员便签"), "", "");
        service.create(createReq("游客便签"), "cupcoff1", "");

        assertEquals(1, service.list("cupcoff1", "admin").size());   // 只管理员
        assertEquals(1, service.list("cupcoff1", "guest").size());   // 只游客
    }

    @Test
    void list_shouldSetOwnFlag() {
        service.create(createReq("我的"), "cupcoff1", "");
        service.create(createReq("别人的"), "other", "");

        var result = service.list("cupcoff1", "guest");
        assertTrue(result.stream().anyMatch(v -> v.isOwn()));
        assertTrue(result.stream().anyMatch(v -> !v.isOwn()));
    }

    // ==================== delete ====================

    @Test
    void delete_adminCanDeleteAny() {
        service.create(createReq("便签"), "cupcoff1", "");
        Long id = mapper.selectList(null).get(0).getId();

        assertTrue(service.delete(id, "admin", true));
        assertNull(mapper.selectById(id));
    }

    @Test
    void delete_authorCanDeleteOwn() {
        service.create(createReq("我的"), "cupcoff1", "");
        Long id = mapper.selectList(null).get(0).getId();

        assertTrue(service.delete(id, "cupcoff1", false));
    }

    @Test
    void delete_strangerCannotDelete() {
        service.create(createReq("别人的"), "cupcoff1", "");
        Long id = mapper.selectList(null).get(0).getId();

        assertFalse(service.delete(id, "stranger", false));
        assertNotNull(mapper.selectById(id));  // 数据还在
    }
}

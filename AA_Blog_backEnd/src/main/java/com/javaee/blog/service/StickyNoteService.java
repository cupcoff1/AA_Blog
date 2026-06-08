package com.javaee.blog.service;

import com.javaee.blog.dto.request.StickyNoteCreateRequest;
import com.javaee.blog.dto.vo.StickyNoteVO;

import java.util.List;

public interface StickyNoteService {

    List<StickyNoteVO> list(String currentUser, String source);

    void create(StickyNoteCreateRequest req, String authorName, String authorAvatar);

    boolean delete(Long id, String requester, boolean isAdmin);
}

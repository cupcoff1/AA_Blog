package com.javaee.blog.service;

import com.javaee.blog.dto.request.StickyNoteCreateRequest;
import com.javaee.blog.dto.vo.StickyNoteVO;

import java.util.List;

public interface StickyNoteService {

    List<StickyNoteVO> list();

    void create(StickyNoteCreateRequest req);

    void delete(Long id);
}

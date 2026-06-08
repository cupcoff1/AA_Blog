package com.javaee.blog.controller;

import com.javaee.blog.common.Result;
import com.javaee.blog.dto.request.StickyNoteCreateRequest;
import com.javaee.blog.dto.vo.StickyNoteVO;
import com.javaee.blog.service.StickyNoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class StickyNoteController {

    private final StickyNoteService service;

    @GetMapping("/api/sticky-notes")
    public Result<List<StickyNoteVO>> list() {
        return Result.ok(service.list());
    }

    @PostMapping("/api/admin/sticky-notes")
    public Result<?> create(@Valid @RequestBody StickyNoteCreateRequest req) {
        service.create(req);
        return Result.ok();
    }

    @DeleteMapping("/api/admin/sticky-notes/{id}")
    public Result<?> delete(@PathVariable Long id) {
        service.delete(id);
        return Result.ok();
    }
}

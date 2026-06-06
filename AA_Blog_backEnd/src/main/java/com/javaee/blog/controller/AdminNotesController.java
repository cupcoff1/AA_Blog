package com.javaee.blog.controller;

import com.javaee.blog.common.Result;
import com.javaee.blog.common.ResultCode;
import com.javaee.blog.dto.request.NotesCreateRequest;
import com.javaee.blog.dto.vo.NotesVO;
import com.javaee.blog.service.NotesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminNotesController {

    private final NotesService notesService;

    @GetMapping("/notes")
    public Result<List<NotesVO>> list() {
        List<NotesVO> list = notesService.list(null, null);
        return Result.ok(list);
    }

    @GetMapping("/notes/{id}")
    public Result<NotesVO> getById(@PathVariable Long id) {
        NotesVO vo = notesService.getById(id);
        if (vo == null) {
            return Result.fail(ResultCode.NOT_FOUND);
        }
        return Result.ok(vo);
    }

    @PostMapping("/notes")
    public Result<?> create(@Valid @RequestBody NotesCreateRequest request) {
        notesService.create(request);
        return Result.ok();
    }

    @PutMapping("/notes/{id}")
    public Result<?> update(@PathVariable Long id, @Valid @RequestBody NotesCreateRequest request) {
        notesService.update(id, request);
        return Result.ok();
    }

    @DeleteMapping("/notes/{id}")
    public Result<?> delete(@PathVariable Long id) {
        notesService.delete(id);
        return Result.ok();
    }
}

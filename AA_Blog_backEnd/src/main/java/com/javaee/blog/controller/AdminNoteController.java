package com.javaee.blog.controller;

import com.javaee.blog.common.Result;
import com.javaee.blog.common.ResultCode;
import com.javaee.blog.dto.request.NoteCreateRequest;
import com.javaee.blog.dto.vo.NoteVO;
import com.javaee.blog.service.NoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminNoteController {

    private final NoteService noteService;

    @GetMapping("/notes")
    public Result<List<NoteVO>> list() {
        List<NoteVO> list = noteService.list(null, null);
        return Result.ok(list);
    }

    @GetMapping("/notes/{id}")
    public Result<NoteVO> getById(@PathVariable Long id) {
        NoteVO vo = noteService.getById(id);
        if (vo == null) {
            return Result.fail(ResultCode.NOT_FOUND);
        }
        return Result.ok(vo);
    }

    @PostMapping("/notes")
    public Result<?> create(@Valid @RequestBody NoteCreateRequest request) {
        noteService.create(request);
        return Result.ok();
    }

    @PutMapping("/notes/{id}")
    public Result<?> update(@PathVariable Long id, @Valid @RequestBody NoteCreateRequest request) {
        noteService.update(id, request);
        return Result.ok();
    }

    @DeleteMapping("/notes/{id}")
    public Result<?> delete(@PathVariable Long id) {
        noteService.delete(id);
        return Result.ok();
    }
}

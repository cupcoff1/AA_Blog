package com.javaee.blog.controller;

import com.javaee.blog.common.Result;
import com.javaee.blog.dto.vo.NotesVO;
import com.javaee.blog.service.NotesService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class NotesController {

    private final NotesService notesService;

    @GetMapping("/notes")
    public Result<List<NotesVO>> list(@RequestParam(required = false) String q,
                                       @RequestParam(required = false) String tag) {
        List<NotesVO> list = notesService.list(q, tag);
        return Result.ok(list);
    }
}

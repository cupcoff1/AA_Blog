package com.javaee.blog.controller;

import com.javaee.blog.common.Result;
import com.javaee.blog.dto.vo.NoteVO;
import com.javaee.blog.service.NoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class NoteController {

    private final NoteService noteService;

    @GetMapping("/notes")
    public Result<List<NoteVO>> list(@RequestParam(required = false) String q,
                                       @RequestParam(required = false) String tag) {
        List<NoteVO> list = noteService.list(q, tag);
        return Result.ok(list);
    }
}

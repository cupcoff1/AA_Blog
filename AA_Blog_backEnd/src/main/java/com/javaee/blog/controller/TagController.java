package com.javaee.blog.controller;

import com.javaee.blog.common.Result;
import com.javaee.blog.dto.vo.TagVO;
import com.javaee.blog.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @GetMapping("/tags")
    public Result<List<TagVO>> list() {
        List<TagVO> list = tagService.list();
        return Result.ok(list);
    }
}

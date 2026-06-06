package com.javaee.blog.controller;

import com.javaee.blog.common.Result;
import com.javaee.blog.dto.vo.TagVO;
import com.javaee.blog.service.TagsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TagsController {

    private final TagsService tagsService;

    @GetMapping("/tags")
    public Result<List<TagVO>> list() {
        List<TagVO> list = tagsService.list();
        return Result.ok(list);
    }
}

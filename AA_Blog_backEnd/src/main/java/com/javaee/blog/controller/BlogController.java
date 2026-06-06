package com.javaee.blog.controller;

import com.javaee.blog.common.Result;
import com.javaee.blog.common.ResultCode;
import com.javaee.blog.dto.vo.BlogListVO;
import com.javaee.blog.dto.vo.BlogVO;
import com.javaee.blog.dto.vo.CommentVO;
import com.javaee.blog.service.BlogService;
import com.javaee.blog.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BlogController {

    private final BlogService blogService;
    private final CommentService commentService;

    @GetMapping("/blog")
    public Result<List<BlogListVO>> list(@RequestParam(required = false) String q,
                                         @RequestParam(required = false) String tag) {
        List<BlogListVO> list = blogService.list(q, tag);
        return Result.ok(list);
    }

    @GetMapping("/blog/{slug}")
    public Result<BlogVO> detail(@PathVariable String slug) {
        BlogVO vo = blogService.getBySlug(slug);
        if (vo == null) {
            return Result.fail(ResultCode.NOT_FOUND);
        }
        return Result.ok(vo);
    }

    @GetMapping("/blog/{slug}/comments")
    public Result<List<CommentVO>> comments(@PathVariable String slug) {
        List<CommentVO> list = commentService.listBySlug(slug);
        return Result.ok(list);
    }
}

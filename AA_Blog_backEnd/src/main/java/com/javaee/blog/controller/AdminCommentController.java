package com.javaee.blog.controller;

import com.javaee.blog.common.Result;
import com.javaee.blog.dto.vo.CommentAdminVO;
import com.javaee.blog.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminCommentController {

    private final CommentService commentService;

    @GetMapping("/comments")
    public Result<List<CommentAdminVO>> list() {
        List<CommentAdminVO> list = commentService.adminList();
        return Result.ok(list);
    }

    @DeleteMapping("/comments/{id}")
    public Result<?> delete(@PathVariable Long id) {
        commentService.deleteById(id);
        return Result.ok();
    }
}

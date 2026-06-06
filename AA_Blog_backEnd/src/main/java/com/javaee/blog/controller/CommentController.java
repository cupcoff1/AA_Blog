package com.javaee.blog.controller;

import com.javaee.blog.common.Result;
import com.javaee.blog.dto.request.CommentCreateRequest;
import com.javaee.blog.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/api/blog/{slug}/comments")
    public Result<?> create(@PathVariable String slug,
                            @Valid @RequestBody CommentCreateRequest request,
                            @RequestHeader("X-Author-Name") String authorName,
                            @RequestHeader("X-Author-Avatar") String authorAvatar) {
        commentService.create(slug, request, authorName, authorAvatar);
        return Result.ok();
    }

    @DeleteMapping("/api/comments/{id}")
    public Result<?> delete(@PathVariable Long id,
                            @RequestHeader("X-Author-Name") String authorName) {
        commentService.delete(id, authorName);
        return Result.ok();
    }
}

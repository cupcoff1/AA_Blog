package com.javaee.blog.controller;

import com.javaee.blog.common.Result;
import com.javaee.blog.dto.request.BlogCreateRequest;
import com.javaee.blog.dto.request.TagUpdateRequest;
import com.javaee.blog.dto.vo.BlogListVO;
import com.javaee.blog.dto.vo.BlogVO;
import com.javaee.blog.service.BlogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminBlogController {

    private final BlogService blogService;

    @GetMapping("/blog")
    public Result<List<BlogListVO>> list() {
        List<BlogListVO> list = blogService.list(null, null);
        return Result.ok(list);
    }

    @GetMapping("/blog/{id}")
    public Result<BlogVO> getById(@PathVariable Long id) {
        return Result.ok(blogService.getById(id));
    }

    @PostMapping("/blog")
    public Result<?> create(@Valid @RequestBody BlogCreateRequest request) {
        blogService.create(request);
        return Result.ok();
    }

    @PutMapping("/blog/{id}")
    public Result<?> update(@PathVariable Long id, @Valid @RequestBody BlogCreateRequest request) {
        blogService.update(id, request);
        return Result.ok();
    }

    @PatchMapping("/blog/{id}/tags")
    public Result<?> updateTags(@PathVariable Long id, @Valid @RequestBody TagUpdateRequest request) {
        blogService.updateTags(id, request.getTagIds(), request.getNewTags());
        return Result.ok();
    }

    @DeleteMapping("/blog/{id}")
    public Result<?> delete(@PathVariable Long id) {
        blogService.delete(id);
        return Result.ok();
    }
}

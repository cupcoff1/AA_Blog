package com.javaee.blog.controller;

import com.javaee.blog.common.Result;
import com.javaee.blog.dto.request.ProjectCreateRequest;
import com.javaee.blog.dto.request.TagUpdateRequest;
import com.javaee.blog.dto.vo.ProjectVO;
import com.javaee.blog.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminProjectController {

    private final ProjectService projectService;

    @GetMapping("/projects")
    public Result<List<ProjectVO>> list() {
        List<ProjectVO> list = projectService.list();
        return Result.ok(list);
    }

    @GetMapping("/projects/{id}")
    public Result<ProjectVO> getById(@PathVariable Long id) {
        return Result.ok(projectService.getById(id));
    }

    @PostMapping("/projects")
    public Result<?> create(@Valid @RequestBody ProjectCreateRequest request) {
        projectService.create(request);
        return Result.ok();
    }

    @PutMapping("/projects/{id}")
    public Result<?> update(@PathVariable Long id, @Valid @RequestBody ProjectCreateRequest request) {
        projectService.update(id, request);
        return Result.ok();
    }

    @PatchMapping("/projects/{id}/tags")
    public Result<?> updateTags(@PathVariable Long id, @Valid @RequestBody TagUpdateRequest request) {
        projectService.updateTags(id, request.getTagIds(), request.getNewTags());
        return Result.ok();
    }

    @DeleteMapping("/projects/{id}")
    public Result<?> delete(@PathVariable Long id) {
        projectService.delete(id);
        return Result.ok();
    }
}

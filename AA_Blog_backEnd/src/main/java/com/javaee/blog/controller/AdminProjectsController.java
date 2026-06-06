package com.javaee.blog.controller;

import com.javaee.blog.common.Result;
import com.javaee.blog.common.ResultCode;
import com.javaee.blog.dto.request.ProjectsCreateRequest;
import com.javaee.blog.dto.vo.ProjectsVO;
import com.javaee.blog.service.ProjectsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminProjectsController {

    private final ProjectsService projectsService;

    @GetMapping("/projects")
    public Result<List<ProjectsVO>> list() {
        List<ProjectsVO> list = projectsService.list();
        return Result.ok(list);
    }

    @GetMapping("/projects/{id}")
    public Result<ProjectsVO> getById(@PathVariable Long id) {
        ProjectsVO vo = projectsService.getById(id);
        if (vo == null) {
            return Result.fail(ResultCode.NOT_FOUND);
        }
        return Result.ok(vo);
    }

    @PostMapping("/projects")
    public Result<?> create(@Valid @RequestBody ProjectsCreateRequest request) {
        projectsService.create(request);
        return Result.ok();
    }

    @PutMapping("/projects/{id}")
    public Result<?> update(@PathVariable Long id, @Valid @RequestBody ProjectsCreateRequest request) {
        projectsService.update(id, request);
        return Result.ok();
    }

    @DeleteMapping("/projects/{id}")
    public Result<?> delete(@PathVariable Long id) {
        projectsService.delete(id);
        return Result.ok();
    }
}

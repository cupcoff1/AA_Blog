package com.javaee.blog.controller;

import com.javaee.blog.common.Result;
import com.javaee.blog.dto.vo.ProjectsVO;
import com.javaee.blog.service.ProjectsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProjectsController {

    private final ProjectsService projectsService;

    @GetMapping("/projects")
    public Result<List<ProjectsVO>> list() {
        List<ProjectsVO> list = projectsService.list();
        return Result.ok(list);
    }
}

package com.javaee.blog.controller;

import com.javaee.blog.common.Result;
import com.javaee.blog.dto.vo.ProjectVO;
import com.javaee.blog.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping("/projects")
    public Result<List<ProjectVO>> list() {
        List<ProjectVO> list = projectService.list();
        return Result.ok(list);
    }
}

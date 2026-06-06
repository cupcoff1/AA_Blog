package com.javaee.blog.controller;

import com.javaee.blog.common.Result;
import com.javaee.blog.dto.vo.HomeVO;
import com.javaee.blog.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class HomeController {

    private final DashboardService dashboardService;

    @GetMapping("/home")
    public Result<HomeVO> home() {
        HomeVO vo = dashboardService.getHome();
        return Result.ok(vo);
    }
}

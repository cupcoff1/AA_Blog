package com.javaee.blog.controller;

import com.javaee.blog.common.Result;
import com.javaee.blog.dto.vo.DashboardVO;
import com.javaee.blog.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/dashboard")
    public Result<DashboardVO> dashboard() {
        DashboardVO vo = dashboardService.getDashboard();
        return Result.ok(vo);
    }
}

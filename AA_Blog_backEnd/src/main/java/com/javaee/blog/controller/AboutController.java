package com.javaee.blog.controller;

import com.javaee.blog.common.Result;
import com.javaee.blog.dto.request.AboutUpdateRequest;
import com.javaee.blog.dto.vo.AboutVO;
import com.javaee.blog.service.AboutService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AboutController {

    private final AboutService aboutService;

    @GetMapping("/api/about")
    public Result<AboutVO> get() {
        AboutVO vo = aboutService.get();
        return Result.ok(vo);
    }

    @PutMapping("/api/admin/about")
    public Result<?> update(@Valid @RequestBody AboutUpdateRequest request) {
        aboutService.update(request);
        return Result.ok();
    }
}

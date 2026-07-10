package com.javaee.blog.controller;

import com.javaee.blog.common.Result;
import com.javaee.blog.dto.request.HeroQuoteCreateRequest;
import com.javaee.blog.dto.vo.HeroQuoteVO;
import com.javaee.blog.service.HeroQuoteService;
import com.javaee.blog.service.UploadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class HeroQuoteController {

    private final HeroQuoteService service;
    private final UploadService uploadService;

    @GetMapping("/api/hero-quotes")
    public Result<List<HeroQuoteVO>> list() {
        return Result.ok(service.list());
    }

    @PostMapping("/api/admin/hero-quotes")
    public Result<?> create(@Valid @RequestBody HeroQuoteCreateRequest request) {
        service.create(request);
        return Result.ok();
    }

    @DeleteMapping("/api/admin/hero-quotes/{id}")
    public Result<?> delete(@PathVariable Long id) {
        service.delete(id);
        return Result.ok();
    }

    @GetMapping("/api/hero-config")
    public Result<Map<String, String>> heroConfig() {
        return Result.ok(uploadService.getHeroConfig());
    }
}

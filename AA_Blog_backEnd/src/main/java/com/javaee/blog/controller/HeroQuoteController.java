package com.javaee.blog.controller;

import com.javaee.blog.common.Result;
import com.javaee.blog.service.HeroQuoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class HeroQuoteController {

    private final HeroQuoteService service;

    @Value("${upload.base-dir}")
    private String baseDir;

    @GetMapping("/api/hero-quotes")
    public Result<?> list() {
        return Result.ok(service.list());
    }

    @PostMapping("/api/admin/hero-quotes")
    public Result<?> create(@RequestBody Map<String, String> body) {
        service.create(body.get("content"), body.get("author"), body.get("source"));
        return Result.ok();
    }

    @DeleteMapping("/api/admin/hero-quotes/{id}")
    public Result<?> delete(@PathVariable Long id) {
        service.delete(id);
        return Result.ok();
    }

    @GetMapping("/api/hero-config")
    public Result<?> heroConfig() {
        String heroLight = "/hero-light.png";
        String heroDark = "/hero.jpg";
        Path heroDir = Paths.get(baseDir, "hero");
        if (Files.exists(heroDir.resolve("hero-light.png"))) {
            heroLight = "/uploads/hero/hero-light.png";
        }
        if (Files.exists(heroDir.resolve("hero-dark.jpg"))) {
            heroDark = "/uploads/hero/hero-dark.jpg";
        }
        return Result.ok(Map.of("heroLight", heroLight, "heroDark", heroDark));
    }
}

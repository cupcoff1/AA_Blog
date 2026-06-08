package com.javaee.blog.controller;

import com.javaee.blog.common.Result;
import com.javaee.blog.service.UploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class UploadController {

    private final UploadService uploadService;

    @PostMapping("/upload")
    public Result<?> upload(@RequestParam("file") MultipartFile file,
                            @RequestParam(defaultValue = "avatar") String type) throws IOException {
        String url = uploadService.upload(file, type);
        return Result.ok(Map.of("url", url));
    }

    @PostMapping("/hero-image")
    public Result<?> uploadHero(@RequestParam("file") MultipartFile file,
                                @RequestParam String type) throws IOException {
        String url = uploadService.uploadHero(file, type);
        return Result.ok(Map.of("url", url));
    }
}

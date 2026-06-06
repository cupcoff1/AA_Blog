package com.javaee.blog.controller;

import com.javaee.blog.common.Result;
import com.javaee.blog.common.ResultCode;
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
                            @RequestParam(defaultValue = "avatar") String type) {
        try {
            String url = uploadService.upload(file, type);
            return Result.ok(Map.of("url", url));
        } catch (IllegalArgumentException e) {
            return Result.fail(ResultCode.BAD_REQUEST, e.getMessage());
        } catch (IOException e) {
            return Result.fail(ResultCode.SERVER_ERROR, "文件上传失败");
        }
    }
}

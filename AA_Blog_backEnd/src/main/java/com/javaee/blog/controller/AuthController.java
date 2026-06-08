package com.javaee.blog.controller;

import com.javaee.blog.common.Result;
import com.javaee.blog.service.GitHubAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final GitHubAuthService gitHubAuthService;

    @GetMapping("/github/url")
    public Result<Map<String, String>> getAuthorizationUrl() {
        String url = gitHubAuthService.getAuthorizationUrl();
        return Result.ok(Map.of("url", url));
    }

    @GetMapping("/github/callback")
    public Result<Map<String, Object>> callback(@RequestParam String code) {
        Map<String, Object> result = gitHubAuthService.handleCallback(code);
        return Result.ok(result);
    }
}

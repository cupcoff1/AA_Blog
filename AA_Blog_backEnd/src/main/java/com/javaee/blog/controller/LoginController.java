package com.javaee.blog.controller;

import com.javaee.blog.common.Result;
import com.javaee.blog.common.ResultCode;
import com.javaee.blog.dto.request.ChangePasswordRequest;
import com.javaee.blog.dto.request.LoginRequest;
import com.javaee.blog.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class LoginController {

    private final AuthService authService;

    @PostMapping("/login")
    public Result<?> login(@Valid @RequestBody LoginRequest request) {
        String token = authService.login(request);
        if (token == null) {
            return Result.fail(ResultCode.UNAUTHORIZED, "用户名或密码错误");
        }
        return Result.ok(Map.of("token", token));
    }

    @PostMapping("/refresh")
    public Result<?> refresh(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Result.fail(ResultCode.UNAUTHORIZED);
        }
        try {
            String newToken = authService.refresh(authHeader.substring(7));
            return Result.ok(Map.of("token", newToken));
        } catch (Exception e) {
            return Result.fail(ResultCode.UNAUTHORIZED, "Token 无效或已过期");
        }
    }

    @PutMapping("/password")
    public Result<?> changePassword(@Valid @RequestBody ChangePasswordRequest request,
                                     HttpServletRequest req) {
        String username = (String) req.getAttribute("username");
        authService.changePassword(username, request.getOldPassword(), request.getNewPassword());
        return Result.ok();
    }
}

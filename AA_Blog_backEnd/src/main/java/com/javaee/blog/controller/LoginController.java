package com.javaee.blog.controller;

import com.javaee.blog.common.AppConstants;
import com.javaee.blog.common.Result;
import com.javaee.blog.common.ResultCode;
import com.javaee.blog.dto.request.ChangePasswordRequest;
import com.javaee.blog.dto.request.LoginRequest;
import com.javaee.blog.service.AuthService;
import com.javaee.blog.util.JwtUtil;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class LoginController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    /** 设置 httpOnly Cookie */
    private void setTokenCookie(HttpServletResponse response, String token, int maxAge) {
        Cookie cookie = new Cookie(AppConstants.ADMIN_COOKIE, token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        cookie.setAttribute("SameSite", "Lax");
        response.addCookie(cookie);
    }

    @PostMapping("/login")
    public Result<?> login(@Valid @RequestBody LoginRequest request,
                           HttpServletResponse response) {
        String token = authService.login(request);
        setTokenCookie(response, token, 7 * 24 * 60 * 60); // 7 天
        return Result.ok();
    }

    /** 检查管理员登录状态（不拦截，前端 Sidebar 调用） */
    @GetMapping("/status")
    public Result<Map<String, Boolean>> status(HttpServletRequest request) {
        String token = extractToken(request);
        boolean authenticated = false;
        if (token != null) {
            try {
                jwtUtil.parseToken(token);
                authenticated = true;
            } catch (JwtException ignored) { }
        }
        return Result.ok(Map.of("authenticated", authenticated));
    }

    /** 退出登录，清除 Cookie */
    @PostMapping("/logout")
    public Result<?> logout(HttpServletResponse response) {
        setTokenCookie(response, "", 0); // maxAge=0 立即删除
        return Result.ok();
    }

    private String extractToken(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        for (Cookie cookie : request.getCookies()) {
            if (AppConstants.ADMIN_COOKIE.equals(cookie.getName())) return cookie.getValue();
        }
        return null;
    }

    @PostMapping("/refresh")
    public Result<?> refresh(HttpServletRequest request,
                             HttpServletResponse response) {
        String token = extractToken(request);
        if (token == null) return Result.fail(ResultCode.UNAUTHORIZED);
        try {
            String newToken = authService.refresh(token);
            setTokenCookie(response, newToken, 7 * 24 * 60 * 60);
            return Result.ok();
        } catch (JwtException e) {
            return Result.fail(ResultCode.UNAUTHORIZED, "Token 无效或已过期");
        }
    }

    @PutMapping("/password")
    public Result<?> changePassword(@Valid @RequestBody ChangePasswordRequest request,
                                     HttpServletRequest req) {
        String username = (String) req.getAttribute(AppConstants.USERNAME_ATTR);
        if (username == null) {
            return Result.fail(ResultCode.UNAUTHORIZED);
        }
        authService.changePassword(username, request.getOldPassword(), request.getNewPassword());
        return Result.ok();
    }
}

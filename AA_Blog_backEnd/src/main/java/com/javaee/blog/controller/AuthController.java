package com.javaee.blog.controller;

import com.javaee.blog.common.AppConstants;
import com.javaee.blog.common.Result;
import com.javaee.blog.service.GitHubAuthService;
import com.javaee.blog.util.JwtUtil;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final GitHubAuthService gitHubAuthService;
    private final JwtUtil jwtUtil;

    /** 设置 httpOnly Cookie */
    private void setTokenCookie(HttpServletResponse response, String token, int maxAge) {
        Cookie cookie = new Cookie(AppConstants.COMMENTER_COOKIE, token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        cookie.setAttribute("SameSite", "Lax");
        response.addCookie(cookie);
    }

    private String extractToken(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        for (Cookie cookie : request.getCookies()) {
            if (AppConstants.COMMENTER_COOKIE.equals(cookie.getName())) return cookie.getValue();
        }
        return null;
    }

    @GetMapping("/github/url")
    public Result<Map<String, String>> getAuthorizationUrl() {
        String url = gitHubAuthService.getAuthorizationUrl();
        return Result.ok(Map.of("url", url));
    }

    @GetMapping("/github/callback")
    public Result<Map<String, Object>> callback(@RequestParam String code,
                                                HttpServletResponse response) {
        Map<String, Object> result = gitHubAuthService.handleCallback(code);
        String token = (String) result.get("token");
        @SuppressWarnings("unchecked")
        Map<String, Object> user = (Map<String, Object>) result.get("user");
        setTokenCookie(response, token, 7 * 24 * 60 * 60); // 7 天
        return Result.ok(user); // 只返回用户信息，token 在 httpOnly Cookie 中
    }

    /** 获取当前评论者登录状态 */
    @GetMapping("/status")
    public Result<Map<String, Object>> status(HttpServletRequest request) {
        String token = extractToken(request);
        if (token == null) {
            return Result.ok(Map.of("authenticated", false));
        }
        try {
            var claims = jwtUtil.parseToken(token);
            return Result.ok(Map.of(
                    "authenticated", true,
                    "name", claims.getSubject(),
                    "avatar", claims.get("avatar", String.class)
            ));
        } catch (JwtException e) {
            return Result.ok(Map.of("authenticated", false));
        }
    }

    /** 退出评论者登录，清除 Cookie */
    @PostMapping("/logout")
    public Result<?> logout(HttpServletResponse response) {
        setTokenCookie(response, "", 0);
        return Result.ok();
    }
}

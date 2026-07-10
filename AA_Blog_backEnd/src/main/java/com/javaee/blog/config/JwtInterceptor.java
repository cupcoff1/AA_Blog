package com.javaee.blog.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javaee.blog.common.Result;
import com.javaee.blog.common.ResultCode;
import com.javaee.blog.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    public JwtInterceptor(JwtUtil jwtUtil, ObjectMapper objectMapper) {
        this.jwtUtil = jwtUtil;
        this.objectMapper = objectMapper;
    }

    private String json(ResultCode code, String message) {
        try {
            return objectMapper.writeValueAsString(Result.fail(code, message));
        } catch (Exception e) {
            return "{\"code\":" + code.getCode() + ",\"message\":\"" + message + "\"}";
        }
    }

    /** 从 Cookie 中提取 admin_token */
    private String extractToken(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        for (Cookie cookie : request.getCookies()) {
            if ("admin_token".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String token = extractToken(request);
        if (token == null) {
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(json(ResultCode.UNAUTHORIZED, "请先登录"));
            return false;
        }

        try {
            Claims claims = jwtUtil.parseToken(token);
            request.setAttribute("username", claims.getSubject());
            return true;
        } catch (ExpiredJwtException e) {
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(json(ResultCode.UNAUTHORIZED, "Token 已过期"));
        } catch (JwtException e) {
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(json(ResultCode.UNAUTHORIZED, "Token 无效"));
        }
        return false;
    }
}

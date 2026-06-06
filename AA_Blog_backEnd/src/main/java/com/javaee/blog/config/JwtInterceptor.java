package com.javaee.blog.config;

import com.javaee.blog.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;

    public JwtInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            response.setStatus(401);
            response.getWriter().write("{\"code\":401,\"message\":\"请先登录\"}");
            return false;
        }

        try {
            Claims claims = jwtUtil.parseToken(header.substring(7));
            request.setAttribute("username", claims.getSubject());
            return true;
        } catch (ExpiredJwtException e) {
            response.setStatus(401);
            response.getWriter().write("{\"code\":401,\"message\":\"Token 已过期\"}");
        } catch (JwtException e) {
            response.setStatus(401);
            response.getWriter().write("{\"code\":401,\"message\":\"Token 无效\"}");
        }
        return false;
    }
}

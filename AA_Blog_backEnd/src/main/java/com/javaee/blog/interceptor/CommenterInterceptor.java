package com.javaee.blog.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javaee.blog.common.AppConstants;
import com.javaee.blog.common.Result;
import com.javaee.blog.common.ResultCode;
import com.javaee.blog.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class CommenterInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    public CommenterInterceptor(JwtUtil jwtUtil, ObjectMapper objectMapper) {
        this.jwtUtil = jwtUtil;
        this.objectMapper = objectMapper;
    }

    /** 提取指定 Cookie 中的 token */
    private String extractToken(HttpServletRequest request, String cookieName) {
        if (request.getCookies() == null) return null;
        for (Cookie cookie : request.getCookies()) {
            if (cookieName.equals(cookie.getName())) return cookie.getValue();
        }
        return null;
    }

    /** 解析 token 并填入 request 属性 */
    private void trySetAttributes(HttpServletRequest request, String token) {
        try {
            Claims claims = jwtUtil.parseToken(token);
            request.setAttribute(AppConstants.USERNAME_ATTR, claims.getSubject());
            request.setAttribute("avatar", claims.get("avatar", String.class));
        } catch (JwtException ignored) { /* token 无效就当没登录 */ }
    }

    /** 返回 401 并写入 JSON */
    private boolean reject(HttpServletResponse response, String message) throws Exception {
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(Result.fail(ResultCode.UNAUTHORIZED, message)));
        return false;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) return true;

        // 先查 commenter token，再查 admin token
        String token = extractToken(request, AppConstants.COMMENTER_COOKIE);
        if (token == null) token = extractToken(request, AppConstants.ADMIN_COOKIE);

        // GET 请求：可选的认证
        if ("GET".equalsIgnoreCase(request.getMethod()) || "HEAD".equalsIgnoreCase(request.getMethod())) {
            if (token != null) trySetAttributes(request, token);
            return true;
        }

        // POST / DELETE 必须登录
        if (token == null) return reject(response, "请先登录");
        trySetAttributes(request, token);
        return true;
    }
}

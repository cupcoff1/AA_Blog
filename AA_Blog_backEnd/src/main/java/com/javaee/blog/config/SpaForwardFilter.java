package com.javaee.blog.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SpaForwardFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        String path = request.getRequestURI();

        if (path.startsWith("/api/") || path.startsWith("/uploads/") || path.contains(".")) {
            chain.doFilter(req, res);
        } else if ("GET".equalsIgnoreCase(request.getMethod())) {
            request.getRequestDispatcher("/index.html").forward(req, res);
        } else {
            chain.doFilter(req, res);  // POST/PUT/DELETE 等交给 Spring 返回 405
        }
    }
}

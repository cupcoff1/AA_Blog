package com.javaee.blog.controller;

import com.javaee.blog.common.Result;
import com.javaee.blog.common.ResultCode;
import com.javaee.blog.dto.request.CommentCreateRequest;
import com.javaee.blog.service.CommentService;
import com.javaee.blog.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final JwtUtil jwtUtil;

    @PostMapping("/api/blog/{slug}/comments")
    public Result<?> create(@PathVariable String slug,
                            @Valid @RequestBody CommentCreateRequest request,
                            HttpServletRequest req) {
        Claims claims = getClaims(req);
        if (claims == null) return Result.fail(ResultCode.UNAUTHORIZED);

        String username = claims.getSubject();
        String avatar = claims.get("avatar", String.class);
        commentService.create(slug, request, username, avatar);
        return Result.ok();
    }

    @DeleteMapping("/api/comments/{id}")
    public Result<?> delete(@PathVariable Long id, HttpServletRequest req) {
        Claims claims = getClaims(req);
        if (claims == null) return Result.fail(ResultCode.UNAUTHORIZED);

        commentService.delete(id, claims.getSubject());
        return Result.ok();
    }

    private Claims getClaims(HttpServletRequest req) {
        String header = req.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) return null;
        try {
            return jwtUtil.parseToken(header.substring(7));
        } catch (JwtException e) {
            return null;
        }
    }
}

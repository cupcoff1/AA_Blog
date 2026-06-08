package com.javaee.blog.controller;

import com.javaee.blog.common.Result;
import com.javaee.blog.common.ResultCode;
import com.javaee.blog.dto.request.StickyNoteCreateRequest;
import com.javaee.blog.dto.vo.StickyNoteVO;
import com.javaee.blog.service.StickyNoteService;
import com.javaee.blog.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class StickyNoteController {

    private final StickyNoteService service;
    private final JwtUtil jwtUtil;

    @GetMapping("/api/sticky-notes")
    public Result<List<StickyNoteVO>> list(@RequestParam(defaultValue = "") String source, HttpServletRequest req) {
        String user = getUsername(req, false);
        return Result.ok(service.list(user, source));
    }

    @PostMapping("/api/sticky-notes")
    public Result<?> create(@Valid @RequestBody StickyNoteCreateRequest body, HttpServletRequest req) {
        Claims claims = getClaims(req);
        if (claims == null) return Result.fail(ResultCode.UNAUTHORIZED);
        service.create(body, claims.getSubject(), claims.get("avatar", String.class));
        return Result.ok();
    }

    @DeleteMapping("/api/sticky-notes/{id}")
    public Result<?> delete(@PathVariable Long id, HttpServletRequest req) {
        boolean isAdmin = isAdmin(req);
        String user = getUsername(req, isAdmin);
        boolean ok = service.delete(id, user, isAdmin);
        return ok ? Result.ok() : Result.fail(ResultCode.FORBIDDEN, "无权删除此便签");
    }

    @PostMapping("/api/admin/sticky-notes")
    public Result<?> adminCreate(@Valid @RequestBody StickyNoteCreateRequest body) {
        service.create(body, "", "");
        return Result.ok();
    }

    @DeleteMapping("/api/admin/sticky-notes/{id}")
    public Result<?> adminDelete(@PathVariable Long id) {
        service.delete(id, "", true);
        return Result.ok();
    }

    private Claims getClaims(HttpServletRequest req) {
        String header = req.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) return null;
        try { return jwtUtil.parseToken(header.substring(7)); }
        catch (JwtException e) { return null; }
    }

    private String getUsername(HttpServletRequest req, boolean isAdmin) {
        if (isAdmin) return "AA_";
        Claims claims = getClaims(req);
        return claims != null ? claims.getSubject() : null;
    }

    private boolean isAdmin(HttpServletRequest req) {
        Claims claims = getClaims(req);
        return claims != null && "AA_".equals(claims.getSubject());
    }
}

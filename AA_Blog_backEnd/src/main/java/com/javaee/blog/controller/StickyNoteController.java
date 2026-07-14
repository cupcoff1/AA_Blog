package com.javaee.blog.controller;

import com.javaee.blog.common.AppConstants;
import com.javaee.blog.common.Result;
import com.javaee.blog.common.ResultCode;
import com.javaee.blog.dto.request.StickyNoteCreateRequest;
import com.javaee.blog.dto.vo.StickyNoteVO;
import com.javaee.blog.service.StickyNoteService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class StickyNoteController {

    /** 管理员用户名，首次启动由 DataInitializer 创建 */
    private static final String ADMIN_USERNAME = "AA_";

    private final StickyNoteService service;

    @GetMapping("/api/sticky-notes")
    public Result<List<StickyNoteVO>> list(@RequestParam(defaultValue = "") String source, HttpServletRequest req) {
        String user = (String) req.getAttribute(AppConstants.USERNAME_ATTR);
        return Result.ok(service.list(user != null ? user : "", source));
    }

    /**
     * 游客创建便签。CommenterInterceptor 已校验登录态，
     * request 属性中必有 username 和 avatar。
     */
    @PostMapping("/api/sticky-notes")
    public Result<?> create(@Valid @RequestBody StickyNoteCreateRequest body, HttpServletRequest req) {
        String username = (String) req.getAttribute(AppConstants.USERNAME_ATTR);
        String avatar = (String) req.getAttribute("avatar");
        service.create(body, username, avatar != null ? avatar : "");
        return Result.ok();
    }

    /**
     * 删除便签。管理员可删任意，游客仅可删自己创建的。
     * CommenterInterceptor 已解析登录态到 request 属性。
     */
    @DeleteMapping("/api/sticky-notes/{id}")
    public Result<?> delete(@PathVariable Long id, HttpServletRequest req) {
        String user = (String) req.getAttribute(AppConstants.USERNAME_ATTR);
        String requester = user != null ? user : "";
        boolean isAdmin = ADMIN_USERNAME.equals(user);
        boolean ok = service.delete(id, requester, isAdmin);
        return ok ? Result.ok() : Result.fail(ResultCode.FORBIDDEN, "无权删除此便签");
    }

    /** 管理员创建便签（About Me）。JwtInterceptor 已校验登录态。 */
    @PostMapping("/api/admin/sticky-notes")
    public Result<?> adminCreate(@Valid @RequestBody StickyNoteCreateRequest body) {
        service.create(body, "", "");
        return Result.ok();
    }

    /** 管理员删除便签。JwtInterceptor 已校验登录态。 */
    @DeleteMapping("/api/admin/sticky-notes/{id}")
    public Result<?> adminDelete(@PathVariable Long id) {
        service.delete(id, "", true);
        return Result.ok();
    }
}

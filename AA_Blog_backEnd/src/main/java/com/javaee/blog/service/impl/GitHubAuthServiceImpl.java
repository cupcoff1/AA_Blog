package com.javaee.blog.service.impl;

import com.javaee.blog.common.AppConstants;
import com.javaee.blog.common.ResultCode;
import com.javaee.blog.service.GitHubAuthService;
import com.javaee.blog.util.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;

/**
 * GitHub OAuth 认证流程实现。
 *
 *
 * 整体流程：
 *   1. getAuthorizationUrl()   — 生成 GitHub 授权页 URL，前端跳转过去
 *   2. handleCallback(code)     — GitHub 回调返回临时 code，后端换 token + 用户信息
 *      ├── 第一步：POST https://github.com/login/oauth/access_token — code 换 access_token
 *      └── 第二步：GET https://api.github.com/user — access_token 换用户信息
 *   3. 拿到用户 login + avatar → 生成 commenter JWT → 返回 AuthController 存入 httpOnly Cookie
 *
 */
@Service
public class GitHubAuthServiceImpl implements GitHubAuthService {

    @Value("${github.client-id}")
    private String clientId;

    @Value("${github.client-secret}")
    private String clientSecret;

    @Value("${github.redirect-uri}")
    private String redirectUri;

    private final JwtUtil jwtUtil;
    private final RestTemplate restTemplate;

    public GitHubAuthServiceImpl(JwtUtil jwtUtil, RestTemplate restTemplate) {
        this.jwtUtil = jwtUtil;
        this.restTemplate = restTemplate;
    }

    /**
     * 生成 GitHub OAuth 授权页 URL。
     * @return 形如 https://github.com/login/oauth/authorize?client_id=xxx&redirect_uri=xxx&scope=user:email
     */
    @Override
    public String getAuthorizationUrl() {
        return AppConstants.GITHUB_AUTH_URL
                + "?client_id=" + clientId
                + "&redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8)
                + "&scope=" + AppConstants.GITHUB_SCOPE;
    }

    /**
     * GitHub OAuth 回调处理。
     * 第一步：用临时 code 向 GitHub 换取 access_token。
     * 第二步：用 access_token 调用 GitHub API 获取用户信息（login、avatar_url）。
     * 第三步：生成 commenter JWT（含用户名和头像），返回给 Controller。
     *
     * @param code GitHub 重定向到 /auth/callback 时 URL 上的临时授权码
     * @return { token: JWT 字符串, user: { name, avatar } }
     */
    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> handleCallback(String code) {
        // ==== 第一步：code → access_token ====
        String tokenUrl = AppConstants.GITHUB_TOKEN_URL;
        Map<String, String> body = Map.of(
                "client_id", clientId,
                "client_secret", clientSecret,
                "code", code,
                "redirect_uri", redirectUri
        );
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> tokenResp = restTemplate.postForEntity(tokenUrl, request, Map.class);
        Map<String, Object> tokenBody = tokenResp.getBody();
        if (tokenBody == null) {
            throw new RuntimeException(ResultCode.MSG_GITHUB_AUTH_FAILED + "：无响应");
        }
        String accessToken = (String) tokenBody.get("access_token");
        if (accessToken == null) {
            throw new RuntimeException(ResultCode.MSG_GITHUB_AUTH_FAILED + "：无法获取 access_token");
        }

        // ==== 第二步：access_token → 用户信息 ====
        HttpHeaders userHeaders = new HttpHeaders();
        userHeaders.setBearerAuth(accessToken);
        HttpEntity<Void> userRequest = new HttpEntity<>(userHeaders);
        ResponseEntity<Map> userResp = restTemplate.exchange(
                AppConstants.GITHUB_USER_URL, HttpMethod.GET, userRequest, Map.class);

        Map<String, Object> user = userResp.getBody();
        if (user == null) {
            throw new RuntimeException(ResultCode.MSG_GITHUB_AUTH_FAILED + "：无法获取用户信息");
        }
        String username = (String) user.get("login");
        String avatar = (String) user.get("avatar_url");

        // ==== 第三步：生成自有 JWT，包含用户名和头像 ====
        String token = jwtUtil.generateCommenterToken(username, avatar);

        return Map.of("token", token,
                "user", Map.of("name", username, "avatar", avatar));
    }
}

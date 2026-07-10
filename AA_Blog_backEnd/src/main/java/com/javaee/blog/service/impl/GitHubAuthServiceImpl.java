package com.javaee.blog.service.impl;

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

    @Override
    public String getAuthorizationUrl() {
        return "https://github.com/login/oauth/authorize"
                + "?client_id=" + clientId
                + "&redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8)
                + "&scope=user:email";
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> handleCallback(String code) {
        String tokenUrl = "https://github.com/login/oauth/access_token";
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
            throw new RuntimeException("GitHub OAuth 失败：无响应");
        }
        String accessToken = (String) tokenBody.get("access_token");
        if (accessToken == null) {
            throw new RuntimeException("GitHub OAuth 失败：无法获取 access_token");
        }

        HttpHeaders userHeaders = new HttpHeaders();
        userHeaders.setBearerAuth(accessToken);
        HttpEntity<Void> userRequest = new HttpEntity<>(userHeaders);
        ResponseEntity<Map> userResp = restTemplate.exchange(
                "https://api.github.com/user", HttpMethod.GET, userRequest, Map.class);

        Map<String, Object> user = userResp.getBody();
        if (user == null) {
            throw new RuntimeException("GitHub OAuth 失败：无法获取用户信息");
        }
        String username = (String) user.get("login");
        String avatar = (String) user.get("avatar_url");

        String token = jwtUtil.generateCommenterToken(username, avatar);

        return Map.of("token", token,
                "user", Map.of("name", username, "avatar", avatar));
    }
}

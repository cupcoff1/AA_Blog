package com.javaee.blog.service;

import com.javaee.blog.util.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
public class GitHubAuthService {

    @Value("${github.client-id}")
    private String clientId;

    @Value("${github.client-secret}")
    private String clientSecret;

    @Value("${github.redirect-uri}")
    private String redirectUri;

    private final JwtUtil jwtUtil;
    private final RestTemplate restTemplate = new RestTemplate();

    public GitHubAuthService(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    public String getAuthorizationUrl() {
        return "https://github.com/login/oauth/authorize"
                + "?client_id=" + clientId
                + "&redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8)
                + "&scope=user:email";
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> handleCallback(String code) {
        // Step 1: code → access_token
        String tokenUrl = "https://github.com/login/oauth/access_token";
        Map<String, String> body = Map.of(
                "client_id", clientId,
                "client_secret", clientSecret,
                "code", code,
                "redirect_uri", redirectUri
        );
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(java.util.Collections.singletonList(MediaType.APPLICATION_JSON));
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

        // Step 2: access_token → 用户信息
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

        // Step 3: 签发评论者 JWT
        String token = jwtUtil.generateCommenterToken(username, avatar);

        return Map.of("token", token,
                "user", Map.of("name", username, "avatar", avatar));
    }
}

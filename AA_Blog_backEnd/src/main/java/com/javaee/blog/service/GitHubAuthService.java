package com.javaee.blog.service;

import java.util.Map;

public interface GitHubAuthService {

    String getAuthorizationUrl();

    Map<String, Object> handleCallback(String code);
}

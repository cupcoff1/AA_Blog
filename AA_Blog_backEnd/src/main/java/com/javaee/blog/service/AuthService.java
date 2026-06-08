package com.javaee.blog.service;

import com.javaee.blog.dto.request.LoginRequest;

public interface AuthService {

    String login(LoginRequest request);

    String refresh(String oldToken);

    void changePassword(String username, String oldPwd, String newPwd);
}

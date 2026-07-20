package com.javaee.blog.integration;

import com.javaee.blog.dto.request.ChangePasswordRequest;
import com.javaee.blog.dto.request.LoginRequest;
import com.javaee.blog.entity.Admin;
import com.javaee.blog.mapper.AdminMapper;
import com.javaee.blog.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AuthServiceIT {

    @Autowired
    private AuthService authService;

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        Admin admin = new Admin();
        admin.setUsername("test_admin");
        admin.setPassword(passwordEncoder.encode("old_password"));
        adminMapper.insert(admin);
    }

    // ==================== login ====================

    @Test
    void login_shouldReturnToken_whenCorrectPassword() {
        LoginRequest request = new LoginRequest();
        request.setUsername("test_admin");
        request.setPassword("old_password");

        String token = authService.login(request);

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void login_shouldThrow_whenWrongPassword() {
        LoginRequest request = new LoginRequest();
        request.setUsername("test_admin");
        request.setPassword("wrong");

        assertThrows(IllegalArgumentException.class, () -> authService.login(request));
    }

    @Test
    void login_shouldThrow_whenUserNotFound() {
        LoginRequest request = new LoginRequest();
        request.setUsername("nobody");
        request.setPassword("any");

        assertThrows(IllegalArgumentException.class, () -> authService.login(request));
    }

    // ==================== changePassword ====================

    @Test
    void changePassword_shouldAllowLoginWithNewPassword() {
        ChangePasswordRequest req = new ChangePasswordRequest();
        req.setOldPassword("old_password");
        req.setNewPassword("new_password_123");

        authService.changePassword("test_admin", req.getOldPassword(), req.getNewPassword());

        // 旧密码不能登录
        LoginRequest oldLogin = new LoginRequest();
        oldLogin.setUsername("test_admin");
        oldLogin.setPassword("old_password");
        assertThrows(IllegalArgumentException.class, () -> authService.login(oldLogin));

        // 新密码能登录
        LoginRequest newLogin = new LoginRequest();
        newLogin.setUsername("test_admin");
        newLogin.setPassword("new_password_123");
        assertNotNull(authService.login(newLogin));
    }
}

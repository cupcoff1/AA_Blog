package com.javaee.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.javaee.blog.dto.request.LoginRequest;
import com.javaee.blog.entity.Admin;
import com.javaee.blog.mapper.AdminMapper;
import com.javaee.blog.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private AdminMapper adminMapper;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    private final Admin admin = new Admin();

    @BeforeEach
    void setUp() {
        admin.setId(1L);
        admin.setUsername("AA_");
        // BCrypt 加密后的 "123456"
        admin.setPassword("$2a$10$dummy_hashed_password");
    }

    // ==================== 成功登录 ====================

    @Test
    void login_shouldReturnToken_whenCredentialsCorrect() {
        LoginRequest request = new LoginRequest();
        request.setUsername("AA_");
        request.setPassword("123456");

        when(adminMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(admin);
        when(passwordEncoder.matches(request.getPassword(), admin.getPassword())).thenReturn(true);
        String neededToken = "jwt-token-abc";
        when(jwtUtil.generateToken(request.getUsername())).thenReturn(neededToken);

        String token = authService.login(request);

        assertEquals(neededToken , token);
    }

    // ==================== 登录失败 ====================

    // 密码错误
    @Test
    void login_shouldThrow_whenPasswordWrong() {
        LoginRequest request = new LoginRequest();
        request.setUsername("AA_");
        request.setPassword("wrong");

        when(adminMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(admin);
        when(passwordEncoder.matches(request.getPassword(), admin.getPassword())).thenReturn(false);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> authService.login(request));

        assertEquals("用户名或密码错误", ex.getMessage());
    }

    //用户名不存在
    @Test
    void login_shouldThrow_whenUserNotFound() {

        LoginRequest request = new LoginRequest();
        request.setUsername("nobody");
        request.setPassword("any");

        when(adminMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> authService.login(request));
        assertEquals("用户名或密码错误", ex.getMessage());
    }
}

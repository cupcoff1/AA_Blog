package com.javaee.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.javaee.blog.dto.request.LoginRequest;
import com.javaee.blog.entity.Admin;
import com.javaee.blog.mapper.AdminMapper;
import com.javaee.blog.service.AuthService;
import com.javaee.blog.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AdminMapper adminMapper;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Override
    public String login(LoginRequest request) {
        Admin admin = adminMapper.selectOne(
                new LambdaQueryWrapper<Admin>().eq(Admin::getUsername, request.getUsername()));
        if (admin == null || !passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
            throw new IllegalArgumentException("用户名或密码错误");
        }
        return jwtUtil.generateToken(admin.getUsername());
    }

    @Override
    public String refresh(String oldToken) {
        String username = jwtUtil.parseToken(oldToken).getSubject();
        return jwtUtil.generateToken(username);
    }

    @Override
    @Transactional
    public void changePassword(String username, String oldPwd, String newPwd) {
        Admin admin = adminMapper.selectOne(
                new LambdaQueryWrapper<Admin>().eq(Admin::getUsername, username));
        if (admin == null || !passwordEncoder.matches(oldPwd, admin.getPassword())) {
            throw new IllegalArgumentException("旧密码错误");
        }
        admin.setPassword(passwordEncoder.encode(newPwd));
        adminMapper.updateById(admin);
    }
}

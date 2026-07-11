package com.javaee.blog.config;

import com.javaee.blog.entity.Admin;
import com.javaee.blog.mapper.AdminMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final AdminMapper adminMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (adminMapper.selectCount(null) > 0) return;

        String password = System.getenv("ADMIN_INIT_PASSWORD");
        if (password == null || password.isBlank()) {
            byte[] randomBytes = new byte[12];
            new SecureRandom().nextBytes(randomBytes);
            password = Base64.getEncoder().encodeToString(randomBytes);
            log.info("========================================");
            log.info("  ADMIN_INIT_PASSWORD 未设置，已生成随机密码：");
            log.info("  {}", password);
            log.info("  首次登录后请立即修改！");
            log.info("========================================");
        }

        String username = System.getenv("ADMIN_INIT_USERNAME");
        if (username == null || username.isBlank()) {
            username = "AA_";
        }

        Admin admin = new Admin();
        admin.setUsername(username);
        admin.setPassword(passwordEncoder.encode(password));
        adminMapper.insert(admin);
    }
}

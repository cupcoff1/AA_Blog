package com.javaee.blog.config;

import com.javaee.blog.entity.About;
import com.javaee.blog.entity.Admin;
import com.javaee.blog.mapper.AboutMapper;
import com.javaee.blog.mapper.AdminMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final AdminMapper adminMapper;
    private final AboutMapper aboutMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (adminMapper.selectCount(null) == 0) {
            Admin admin = new Admin();
            admin.setUsername("AA_");
            admin.setPassword(passwordEncoder.encode("123456"));
            adminMapper.insert(admin);
        }

        if (aboutMapper.selectById(1L) == null) {
            About about = new About();
            about.setId(1L);
            about.setNickname("AA");
            about.setAvatar("");
            about.setBio("");
            aboutMapper.insert(about);
        }
    }
}

package com.javaee.blog.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

@Service
public class UploadServiceImpl implements UploadService {

    private static final Set<String> ALLOWED_TYPES = Set.of("image/jpeg", "image/png", "image/webp");

    @Value("${upload.base-dir}")
    private String baseDir;

    @Value("${upload.max-size}")
    private long maxSize;

    @Override
    public String upload(MultipartFile file, String type) throws IOException {
        if (file.isEmpty() || file.getSize() > maxSize) {
            throw new IllegalArgumentException("文件大小超过限制（最大 2MB）");
        }
        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            throw new IllegalArgumentException("仅支持 jpg/png/webp 格式");
        }

        String subDir = "image".equals(type) ? "images" : "avatars";

        Path dir = Paths.get(baseDir, subDir);
        Files.createDirectories(dir);

        String originalName = file.getOriginalFilename();
        String ext = "";
        if (originalName != null && originalName.contains(".")) {
            ext = originalName.substring(originalName.lastIndexOf("."));
        }
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String filename = timestamp + ext;

        Path dest = dir.resolve(filename);
        file.transferTo(dest);

        return "/uploads/" + subDir + "/" + filename;
    }

    @Override
    public String uploadHero(MultipartFile file, String type) throws IOException {
        if (file.isEmpty() || file.getSize() > maxSize) {
            throw new IllegalArgumentException("文件大小超过限制（最大 2MB）");
        }
        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            throw new IllegalArgumentException("仅支持 jpg/png/webp 格式");
        }
        if (!"light".equals(type) && !"dark".equals(type)) {
            throw new IllegalArgumentException("type 必须为 light 或 dark");
        }

        Path dir = Paths.get(baseDir, "hero");
        Files.createDirectories(dir);

        String filename = "light".equals(type) ? "hero-light.png" : "hero-dark.jpg";
        Path dest = dir.resolve(filename);
        file.transferTo(dest);

        return "/uploads/hero/" + filename;
    }
}

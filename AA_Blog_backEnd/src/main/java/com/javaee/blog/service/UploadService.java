package com.javaee.blog.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public interface UploadService {

    String upload(MultipartFile file, String type) throws IOException;

    String uploadHero(MultipartFile file, String type) throws IOException;

    Map<String, String> getHeroConfig();
}

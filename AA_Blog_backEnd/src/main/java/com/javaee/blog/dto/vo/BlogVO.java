package com.javaee.blog.dto.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class BlogVO {

    private Long id;
    private String title;
    private String slug;
    private String summary;
    private String content;
    private LocalDateTime publishedAt;
    private List<TagVO> tags;
    private PostNav prev;
    private PostNav next;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PostNav {
        private String title;
        private String slug;
    }
}

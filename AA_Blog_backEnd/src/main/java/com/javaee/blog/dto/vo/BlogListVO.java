package com.javaee.blog.dto.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class BlogListVO {

    private Long id;
    private String title;
    private String slug;
    private String summary;
    private LocalDateTime publishedAt;
    private List<TagVO> tags;
}

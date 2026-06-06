package com.javaee.blog.dto.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentAdminVO {

    private Long id;
    private String content;

    @JsonProperty("parent_id")
    private Long parentId;

    @JsonProperty("author_name")
    private String authorName;

    @JsonProperty("blog_id")
    private Long blogId;

    @JsonProperty("blog_title")
    private String blogTitle;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;
}

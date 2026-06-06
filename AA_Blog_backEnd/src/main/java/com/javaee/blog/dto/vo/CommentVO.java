package com.javaee.blog.dto.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CommentVO {

    private Long id;
    private String content;

    @JsonProperty("parent_id")
    private Long parentId;

    @JsonProperty("author_name")
    private String authorName;

    @JsonProperty("author_avatar")
    private String authorAvatar;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    private List<CommentVO> children;
}

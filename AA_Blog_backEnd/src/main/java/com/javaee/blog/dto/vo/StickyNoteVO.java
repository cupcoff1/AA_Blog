package com.javaee.blog.dto.vo;

import lombok.Data;

@Data
public class StickyNoteVO {

    private Long id;
    private String content;
    private String color;
    private Integer rotate;
    private String category;
    private String authorName;
    private String authorAvatar;
    private boolean own = false;
}

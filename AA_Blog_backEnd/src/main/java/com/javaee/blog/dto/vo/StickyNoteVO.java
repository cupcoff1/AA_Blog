package com.javaee.blog.dto.vo;

import lombok.Data;

@Data
public class StickyNoteVO {

    private Long id;
    private String content;
    private String color;
    private Integer rotate;
    private boolean custom = true;
}

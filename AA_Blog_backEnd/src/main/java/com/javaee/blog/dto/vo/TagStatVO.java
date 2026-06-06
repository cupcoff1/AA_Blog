package com.javaee.blog.dto.vo;

import lombok.Data;

@Data
public class TagStatVO {

    private String name;
    private String slug;
    private int blogCount;
    private int noteCount;
    private int projectCount;
}

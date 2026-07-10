package com.javaee.blog.dto.vo;

import lombok.Data;

@Data
public class HeroQuoteVO {
    private Long id;
    private String content;
    private String author;
    private String source;
}

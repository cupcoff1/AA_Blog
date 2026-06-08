package com.javaee.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("hero_quotes")
public class HeroQuote {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String content;
    private String author;
    private String source;
    private LocalDateTime createdAt;
}

package com.javaee.blog.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.javaee.blog.entity.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("blog")
public class Blog extends BaseEntity {

    private String title;
    private String slug;
    private String summary;
    private String content;
    private LocalDateTime publishedAt;
}

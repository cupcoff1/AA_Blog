package com.javaee.blog.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.javaee.blog.entity.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sticky_notes")
public class StickyNote extends BaseEntity {

    private String content;
    private String color;
    private Integer rotate;
    private String category;
    private String authorName;
    private String authorAvatar;
}

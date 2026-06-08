package com.javaee.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sticky_notes")
public class StickyNote {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String content;
    private String color;
    private Integer rotate;
    private String category;
    private String authorName;
    private String authorAvatar;
    private LocalDateTime createdAt;
}

package com.javaee.blog.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("about")
public class About {

    @TableId
    private Long id;
    private String nickname;
    private String avatar;
    private String bio;
    private String skills;
    private String hobbies;
    private String location;
    private String socialLinks;
    private LocalDateTime updatedAt;
}

package com.javaee.blog.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.javaee.blog.entity.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("projects")
public class Project extends BaseEntity {

    private String name;
    private String slug;
    private String description;
    private String demoUrl;
    private String githubUrl;
}

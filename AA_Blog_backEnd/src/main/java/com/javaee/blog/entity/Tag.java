package com.javaee.blog.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.javaee.blog.entity.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tags")
public class Tag extends BaseEntity {

    private String name;
    private String slug;
}

package com.javaee.blog.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.javaee.blog.entity.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("admin")
public class Admin extends BaseEntity {

    private String username;
    private String password;
}

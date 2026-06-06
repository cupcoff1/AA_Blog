package com.javaee.blog.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("blog_tags")
public class BlogTags {

    private Long blogId;
    private Long tagId;
}

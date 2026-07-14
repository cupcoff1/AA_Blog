package com.javaee.blog.entity.association;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("projects_tags")
public class ProjectTags {

    private Long projectId;
    private Long tagId;
}

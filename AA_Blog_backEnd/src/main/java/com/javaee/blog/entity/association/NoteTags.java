package com.javaee.blog.entity.association;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("notes_tags")
public class NoteTags {

    private Long notesId;
    private Long tagId;
}

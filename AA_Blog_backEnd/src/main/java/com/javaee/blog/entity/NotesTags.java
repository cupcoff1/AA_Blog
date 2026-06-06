package com.javaee.blog.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("notes_tags")
public class NotesTags {

    private Long notesId;
    private Long tagId;
}

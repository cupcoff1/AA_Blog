package com.javaee.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.javaee.blog.entity.NotesTags;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface NotesTagsMapper extends BaseMapper<NotesTags> {

    @Select("SELECT tag_id, COUNT(*) AS cnt FROM notes_tags GROUP BY tag_id")
    List<Map<String, Object>> countByTag();
}

package com.javaee.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.javaee.blog.entity.association.NoteTags;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface NoteTagsMapper extends BaseMapper<NoteTags> {
}

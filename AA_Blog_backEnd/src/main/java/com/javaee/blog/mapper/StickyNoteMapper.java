package com.javaee.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.javaee.blog.entity.StickyNote;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StickyNoteMapper extends BaseMapper<StickyNote> {
}

package com.javaee.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.javaee.blog.entity.ProjectsTags;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface ProjectsTagsMapper extends BaseMapper<ProjectsTags> {

    @Select("SELECT tag_id, COUNT(*) AS cnt FROM projects_tags GROUP BY tag_id")
    List<Map<String, Object>> countByTag();
}

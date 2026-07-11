package com.javaee.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.javaee.blog.entity.association.ProjectsTags;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProjectsTagsMapper extends BaseMapper<ProjectsTags> {
}

package com.javaee.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.javaee.blog.entity.BlogTags;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BlogTagsMapper extends BaseMapper<BlogTags> {
}

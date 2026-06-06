package com.javaee.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.javaee.blog.dto.vo.TagVO;
import com.javaee.blog.entity.Tags;
import com.javaee.blog.mapper.TagsMapper;
import com.javaee.blog.service.TagsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagsServiceImpl implements TagsService {

    private final TagsMapper tagsMapper;

    @Override
    public List<TagVO> list() {
        List<Tags> tags = tagsMapper.selectList(new LambdaQueryWrapper<Tags>().orderByAsc(Tags::getId));
        return tags.stream().map(tag -> {
            TagVO vo = new TagVO();
            vo.setId(tag.getId());
            vo.setName(tag.getName());
            vo.setSlug(tag.getSlug());
            return vo;
        }).collect(Collectors.toList());
    }
}

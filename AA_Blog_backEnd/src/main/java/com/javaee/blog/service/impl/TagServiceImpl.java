package com.javaee.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.javaee.blog.dto.vo.TagVO;
import com.javaee.blog.entity.Tag;
import com.javaee.blog.mapper.TagMapper;
import com.javaee.blog.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagMapper tagMapper;

    @Override
    public List<TagVO> list() {
        List<Tag> tags = tagMapper.selectList(new LambdaQueryWrapper<Tag>().orderByAsc(Tag::getId));
        return tags.stream().map(tag -> {
            TagVO vo = new TagVO();
            vo.setId(tag.getId());
            vo.setName(tag.getName());
            vo.setSlug(tag.getSlug());
            return vo;
        }).collect(Collectors.toList());
    }
}

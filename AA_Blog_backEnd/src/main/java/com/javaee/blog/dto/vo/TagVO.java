package com.javaee.blog.dto.vo;

import com.javaee.blog.entity.Tags;
import lombok.Data;

@Data
public class TagVO {

    private Long id;
    private String name;
    private String slug;

    public static TagVO from(Tags tag) {
        TagVO vo = new TagVO();
        vo.setId(tag.getId());
        vo.setName(tag.getName());
        vo.setSlug(tag.getSlug());
        return vo;
    }
}

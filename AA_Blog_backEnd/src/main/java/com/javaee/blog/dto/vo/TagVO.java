package com.javaee.blog.dto.vo;

import com.javaee.blog.entity.Tag;
import lombok.Data;

@Data
public class TagVO {

    private Long id;
    private String name;
    private String slug;

    public static TagVO from(Tag tag) {
        TagVO vo = new TagVO();
        vo.setId(tag.getId());
        vo.setName(tag.getName());
        vo.setSlug(tag.getSlug());
        return vo;
    }
}

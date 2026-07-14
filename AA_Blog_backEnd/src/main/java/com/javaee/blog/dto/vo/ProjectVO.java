package com.javaee.blog.dto.vo;

import lombok.Data;

import java.util.List;

@Data
public class ProjectVO {

    private Long id;
    private String name;
    private String slug;
    private String description;
    private String demoUrl;
    private String githubUrl;
    private List<TagVO> tags;
}

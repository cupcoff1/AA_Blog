package com.javaee.blog.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class ProjectCreateRequest {

    @NotBlank(message = "项目名称不能为空")
    private String name;

    @NotBlank(message = "描述不能为空")
    private String description;

    private String demoUrl;

    private String githubUrl;

    private List<Long> tagIds;

    private List<String> newTags;
}

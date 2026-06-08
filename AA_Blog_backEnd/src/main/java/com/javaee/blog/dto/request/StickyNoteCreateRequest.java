package com.javaee.blog.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class StickyNoteCreateRequest {

    @NotBlank(message = "内容不能为空")
    private String content;

    @NotBlank(message = "颜色不能为空")
    private String color;

    private Integer rotate;
}

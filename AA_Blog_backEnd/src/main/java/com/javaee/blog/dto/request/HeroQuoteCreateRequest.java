package com.javaee.blog.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class HeroQuoteCreateRequest {

    @NotBlank(message = "引语内容不能为空")
    private String content;

    private String author = "";
    private String source = "";
}

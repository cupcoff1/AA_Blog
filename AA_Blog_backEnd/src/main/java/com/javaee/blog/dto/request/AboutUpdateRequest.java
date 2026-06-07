package com.javaee.blog.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AboutUpdateRequest {

    @NotBlank(message = "昵称不能为空")
    private String nickname;

    private String avatar;

    private String bio;

    private String skills;
    private String hobbies;
    private String location;
    private String socialLinks;
}

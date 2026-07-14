package com.javaee.blog.dto.request;

import jakarta.validation.Valid;
import lombok.Data;

import java.util.List;

/** PATCH 标签更新 */
@Data
public class TagUpdateRequest {

    private List<Long> tagIds;

    @Valid
    private List<String> newTags;
}

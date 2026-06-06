package com.javaee.blog.dto.vo;

import lombok.Data;

import java.util.List;

@Data
public class DashboardVO {

    private long blogCount;
    private long noteCount;
    private long projectCount;
    private long commentCount;
    private List<TagStatVO> tags;
    private List<CommentAdminVO> recentComments;
}

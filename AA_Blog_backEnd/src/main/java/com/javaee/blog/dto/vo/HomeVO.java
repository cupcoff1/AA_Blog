package com.javaee.blog.dto.vo;

import lombok.Data;

import java.util.List;

@Data
public class HomeVO {

    private List<BlogListVO> latestBlogs;
    private List<NoteVO> latestNotes;
    private List<ProjectVO> latestProjects;
}

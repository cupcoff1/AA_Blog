package com.javaee.blog.dto.vo;

import lombok.Data;

import java.util.List;

@Data
public class HomeVO {

    private AboutVO about;
    private List<BlogListVO> latestBlogs;
    private List<NotesVO> latestNotes;
    private List<ProjectsVO> latestProjects;
}

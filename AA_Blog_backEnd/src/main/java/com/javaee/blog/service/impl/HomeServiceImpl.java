package com.javaee.blog.service.impl;

import com.javaee.blog.dto.vo.HomeVO;
import com.javaee.blog.service.BlogService;
import com.javaee.blog.service.HomeService;
import com.javaee.blog.service.NoteService;
import com.javaee.blog.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HomeServiceImpl implements HomeService {

    private final BlogService blogService;
    private final NoteService noteService;
    private final ProjectService projectService;

    @Value("${home.latest-blogs}")
    private int latestBlogs;

    @Value("${home.latest-notes}")
    private int latestNotes;

    @Value("${home.latest-projects}")
    private int latestProjects;

    @Override
    public HomeVO getHome() {
        HomeVO vo = new HomeVO();
        vo.setLatestBlogs(blogService.list(null, null, latestBlogs));
        vo.setLatestNotes(noteService.list(null, null, latestNotes));
        vo.setLatestProjects(projectService.list(latestProjects));
        return vo;
    }
}

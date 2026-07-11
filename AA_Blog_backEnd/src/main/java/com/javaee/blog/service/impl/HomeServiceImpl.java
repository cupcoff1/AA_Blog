package com.javaee.blog.service.impl;

import com.javaee.blog.dto.vo.HomeVO;
import com.javaee.blog.service.BlogService;
import com.javaee.blog.service.HomeService;
import com.javaee.blog.service.NotesService;
import com.javaee.blog.service.ProjectsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HomeServiceImpl implements HomeService {

    private final BlogService blogService;
    private final NotesService notesService;
    private final ProjectsService projectsService;

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
        vo.setLatestNotes(notesService.list(null, null, latestNotes));
        vo.setLatestProjects(projectsService.list(latestProjects));
        return vo;
    }
}

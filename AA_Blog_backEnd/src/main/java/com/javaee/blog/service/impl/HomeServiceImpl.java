package com.javaee.blog.service.impl;

import com.javaee.blog.dto.vo.HomeVO;
import com.javaee.blog.service.BlogService;
import com.javaee.blog.service.HomeService;
import com.javaee.blog.service.NotesService;
import com.javaee.blog.service.ProjectsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HomeServiceImpl implements HomeService {

    private final BlogService blogService;
    private final NotesService notesService;
    private final ProjectsService projectsService;

    @Override
    public HomeVO getHome() {
        HomeVO vo = new HomeVO();
        vo.setLatestBlogs(blogService.list(null, null, 5));
        vo.setLatestNotes(notesService.list(null, null, 5));
        vo.setLatestProjects(projectsService.list(6));
        return vo;
    }
}

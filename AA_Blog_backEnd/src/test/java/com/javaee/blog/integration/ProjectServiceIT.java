package com.javaee.blog.integration;

import com.javaee.blog.dto.request.ProjectCreateRequest;
import com.javaee.blog.dto.vo.ProjectVO;
import com.javaee.blog.entity.Project;
import com.javaee.blog.mapper.ProjectMapper;
import com.javaee.blog.service.ProjectService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ProjectServiceIT {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectMapper projectMapper;

    private ProjectCreateRequest createReq(String name) {
        ProjectCreateRequest req = new ProjectCreateRequest();
        req.setName(name);
        req.setDescription("描述");
        req.setDemoUrl("https://demo.example.com");
        req.setGithubUrl("https://github.com/example/repo");
        return req;
    }

    @Test
    void createAndGetById_shouldWork() {
        projectService.create(createReq("项目A"));

        Project saved = projectMapper.selectList(null).get(0);
        assertEquals("项目A", saved.getName());
        assertEquals("描述", saved.getDescription());
        assertNotNull(saved.getSlug());

        ProjectVO vo = projectService.getById(saved.getId());
        assertEquals("https://demo.example.com", vo.getDemoUrl());
    }

    @Test
    void getById_shouldThrow_whenNotFound() {
        assertThrows(NoSuchElementException.class, () -> projectService.getById(999999L));
    }

    @Test
    void update_shouldChangeFields() {
        projectService.create(createReq("旧名称"));
        Long id = projectMapper.selectList(null).get(0).getId();

        ProjectCreateRequest updateReq = createReq("新名称");
        updateReq.setDescription("新描述");
        projectService.update(id, updateReq);

        Project updated = projectMapper.selectById(id);
        assertEquals("新名称", updated.getName());
        assertEquals("新描述", updated.getDescription());
        // slug 不变
        assertNotNull(updated.getSlug());
    }

    @Test
    void updateTags_shouldReplaceTags() {
        ProjectCreateRequest req = createReq("标签项目");
        req.setNewTags(List.of("Java"));
        projectService.create(req);
        Long id = projectMapper.selectList(null).get(0).getId();

        projectService.updateTags(id, Collections.emptyList(), List.of("Spring"));

        assertEquals("Spring", projectService.getById(id).getTags().get(0).getName());
    }

    @Test
    void list_shouldReturnProjects() {
        projectService.create(createReq("A"));
        projectService.create(createReq("B"));
        assertEquals(2, projectService.list().size());
    }

    @Test
    void delete_shouldRemoveProject() {
        projectService.create(createReq("删除"));
        Long id = projectMapper.selectList(null).get(0).getId();
        projectService.delete(id);
        assertNull(projectMapper.selectById(id));
    }
}

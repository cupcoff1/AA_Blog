package com.javaee.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.javaee.blog.dto.request.ProjectCreateRequest;
import com.javaee.blog.dto.vo.ProjectVO;
import com.javaee.blog.entity.Project;
import com.javaee.blog.entity.association.ProjectTags;
import com.javaee.blog.mapper.ProjectMapper;
import com.javaee.blog.mapper.ProjectTagsMapper;
import com.javaee.blog.mapper.TagMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceImplTest {

    @Mock
    private ProjectMapper projectMapper;

    @Mock
    private TagMapper tagMapper;

    @Mock
    private ProjectTagsMapper projectTagsMapper;

    @InjectMocks
    private ProjectServiceImpl service;

    private Project project;

    @BeforeEach
    void setUp() {
        project = new Project();
        project.setId(1L);
        project.setName("Test Project");
        project.setSlug("test-project");
        project.setDescription("A description");
        project.setDemoUrl("https://demo.example.com");
        project.setGithubUrl("https://github.com/example/project");
    }

    // ==================== getById ====================

    @Test
    void getById_shouldThrow_whenNotFound() {
        when(projectMapper.selectById(99L)).thenReturn(null);

        assertThrows(NoSuchElementException.class, () -> service.getById(99L));
    }

    @Test
    void getById_shouldReturnProject() {
        when(projectMapper.selectById(1L)).thenReturn(project);
        when(projectTagsMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());

        ProjectVO result = service.getById(1L);

        assertNotNull(result);
        assertEquals(project.getName(), result.getName());
        assertEquals(project.getDescription(), result.getDescription());
    }

    // ==================== list ====================

    @Test
    void list_shouldReturnProjects() {
        when(projectMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of(project));
        when(projectTagsMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());

        List<ProjectVO> result = service.list();

        assertEquals(1, result.size());
        assertEquals(project.getName(), result.get(0).getName());
    }

    // ==================== create ====================

    @Test
    void create_shouldInsertProjectAndHandleTags() {
        ProjectCreateRequest request = new ProjectCreateRequest();
        request.setName("New Project");
        request.setDescription("Description");
        request.setTagIds(List.of(1L));
        when(tagMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());

        service.create(request);

        verify(projectMapper).insert(any(Project.class));
        verify(projectTagsMapper, atLeastOnce()).insert(any(ProjectTags.class));
    }

    // ==================== update ====================

    @Test
    void update_shouldThrow_whenNotFound() {
        ProjectCreateRequest request = new ProjectCreateRequest();
        request.setName("X");
        request.setDescription("X");
        when(projectMapper.selectById(99L)).thenReturn(null);

        assertThrows(NoSuchElementException.class, () -> service.update(99L, request));
    }

    @Test
    void update_shouldOnlyUpdateContent() {
        ProjectCreateRequest request = new ProjectCreateRequest();
        request.setName("Updated");
        request.setDescription("Updated description");
        when(projectMapper.selectById(1L)).thenReturn(project);

        service.update(1L, request);

        verify(projectMapper).updateById(project);
        assertEquals("Updated", project.getName());
        verify(projectTagsMapper, never()).delete(any(LambdaQueryWrapper.class));
    }

    // ==================== updateTags ====================

    @Test
    void updateTags_shouldThrow_whenProjectNotFound() {
        when(projectMapper.selectById(99L)).thenReturn(null);

        assertThrows(NoSuchElementException.class,
                () -> service.updateTags(99L, List.of(1L), List.of()));
    }

    @Test
    void updateTags_shouldClearOldAndRebuild() {
        when(projectMapper.selectById(1L)).thenReturn(project);
        when(tagMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());

        service.updateTags(1L, List.of(2L), List.of("Vue"));

        verify(projectTagsMapper).delete(any(LambdaQueryWrapper.class));
        verify(projectTagsMapper, atLeastOnce()).insert(any(ProjectTags.class));
    }

    // ==================== delete ====================

    @Test
    void delete_shouldRemoveTagsAndProject() {
        service.delete(1L);

        verify(projectTagsMapper).delete(any(LambdaQueryWrapper.class));
        verify(projectMapper).deleteById(1L);
    }
}

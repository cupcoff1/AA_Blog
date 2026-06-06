package com.javaee.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.javaee.blog.dto.request.ProjectsCreateRequest;
import com.javaee.blog.dto.vo.ProjectsVO;
import com.javaee.blog.dto.vo.TagVO;
import com.javaee.blog.entity.Projects;
import com.javaee.blog.entity.ProjectsTags;
import com.javaee.blog.entity.Tags;
import com.javaee.blog.mapper.ProjectsMapper;
import com.javaee.blog.mapper.ProjectsTagsMapper;
import com.javaee.blog.mapper.TagsMapper;
import com.javaee.blog.service.ProjectsService;
import com.javaee.blog.util.SlugUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectsServiceImpl implements ProjectsService {

    private final ProjectsMapper projectsMapper;
    private final TagsMapper tagsMapper;
    private final ProjectsTagsMapper projectsTagsMapper;

    @Override
    public List<ProjectsVO> list() {
        return queryList(null);
    }

    @Override
    public List<ProjectsVO> list(int limit) {
        return queryList(limit);
    }

    private List<ProjectsVO> queryList(Integer limit) {
        LambdaQueryWrapper<Projects> wrapper = new LambdaQueryWrapper<Projects>()
                .orderByDesc(Projects::getId);
        if (limit != null) wrapper.last("LIMIT " + limit);
        List<Projects> projects = projectsMapper.selectList(wrapper);
        return projects.stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void create(ProjectsCreateRequest request) {
        Projects project = new Projects();
        project.setName(request.getName());
        project.setSlug(generateSlug(request.getName()));
        project.setDescription(request.getDescription());
        project.setDemoUrl(request.getDemoUrl());
        project.setGithubUrl(request.getGithubUrl());
        projectsMapper.insert(project);

        handleTags(project.getId(), request.getTagIds(), request.getNewTags());
    }

    @Override
    @Transactional
    public void update(Long id, ProjectsCreateRequest request) {
        Projects project = projectsMapper.selectById(id);
        if (project == null) return;
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setDemoUrl(request.getDemoUrl());
        project.setGithubUrl(request.getGithubUrl());
        projectsMapper.updateById(project);

        projectsTagsMapper.delete(new LambdaQueryWrapper<ProjectsTags>().eq(ProjectsTags::getProjectId, id));
        handleTags(id, request.getTagIds(), request.getNewTags());
    }

    @Override
    public ProjectsVO getById(Long id) {
        Projects project = projectsMapper.selectById(id);
        if (project == null) return null;
        return toVO(project);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        projectsTagsMapper.delete(new LambdaQueryWrapper<ProjectsTags>().eq(ProjectsTags::getProjectId, id));
        projectsMapper.deleteById(id);
    }

    // ==================== 私有方法 ====================

    private ProjectsVO toVO(Projects project) {
        ProjectsVO vo = new ProjectsVO();
        vo.setId(project.getId());
        vo.setName(project.getName());
        vo.setSlug(project.getSlug());
        vo.setDescription(project.getDescription());
        vo.setDemoUrl(project.getDemoUrl());
        vo.setGithubUrl(project.getGithubUrl());
        vo.setTags(getTagsByProjectId(project.getId()));
        return vo;
    }

    private List<TagVO> getTagsByProjectId(Long projectId) {
        List<ProjectsTags> links = projectsTagsMapper.selectList(
                new LambdaQueryWrapper<ProjectsTags>().eq(ProjectsTags::getProjectId, projectId));
        if (links.isEmpty()) return Collections.emptyList();

        List<Long> tagIds = links.stream().map(ProjectsTags::getTagId).collect(Collectors.toList());
        List<Tags> tags = tagsMapper.selectBatchIds(tagIds);
        return tags.stream().map(tag -> {
            TagVO vo = new TagVO();
            vo.setId(tag.getId());
            vo.setName(tag.getName());
            vo.setSlug(tag.getSlug());
            return vo;
        }).collect(Collectors.toList());
    }

    private void handleTags(Long projectId, List<Long> tagIds, List<String> newTags) {
        if (tagIds != null) {
            for (Long tagId : tagIds) {
                ProjectsTags link = new ProjectsTags();
                link.setProjectId(projectId);
                link.setTagId(tagId);
                projectsTagsMapper.insert(link);
            }
        }
        if (newTags != null) {
            for (String name : newTags) {
                Tags tag = tagsMapper.selectOne(
                        new LambdaQueryWrapper<Tags>().eq(Tags::getName, name));
                if (tag == null) {
                    tag = new Tags();
                    tag.setName(name);
                    tag.setSlug(SlugUtil.toSlug(name));
                    tagsMapper.insert(tag);
                }
                ProjectsTags link = new ProjectsTags();
                link.setProjectId(projectId);
                link.setTagId(tag.getId());
                projectsTagsMapper.insert(link);
            }
        }
    }

    private String generateSlug(String name) {
        String slug = SlugUtil.toSlug(name);
        Long count = projectsMapper.selectCount(new LambdaQueryWrapper<Projects>().eq(Projects::getSlug, slug));
        if (count > 0) slug += "-" + (count + 1);
        return slug;
    }
}

package com.javaee.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.javaee.blog.dto.request.ProjectsCreateRequest;
import com.javaee.blog.dto.vo.ProjectsVO;
import com.javaee.blog.dto.vo.TagVO;
import com.javaee.blog.entity.Projects;
import com.javaee.blog.entity.association.ProjectsTags;
import com.javaee.blog.entity.Tags;
import com.javaee.blog.mapper.ProjectsMapper;
import com.javaee.blog.mapper.ProjectsTagsMapper;
import com.javaee.blog.mapper.TagsMapper;
import com.javaee.blog.service.ProjectsService;
import com.javaee.blog.util.SlugUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectsServiceImpl implements ProjectsService {

    private final ProjectsMapper projectsMapper;
    private final TagsMapper tagsMapper;
    private final ProjectsTagsMapper projectsTagsMapper;

    // ==================== 私有方法 ====================

    /** 列表查询核心逻辑。按 id 倒序，批量取 tag */
    private List<ProjectsVO> queryList(Integer limit) {
        LambdaQueryWrapper<Projects> wrapper = new LambdaQueryWrapper<Projects>()
                .orderByDesc(Projects::getId);
        if (limit != null) wrapper.last("LIMIT " + limit);
        List<Projects> projects = projectsMapper.selectList(wrapper);
        Map<Long, List<TagVO>> tagMap = batchGetTags(projects.stream().map(Projects::getId).collect(Collectors.toList()));
        return projects.stream().map(p -> toVO(p, tagMap.getOrDefault(p.getId(), Collections.emptyList()))).collect(Collectors.toList());
    }

    /** 批量查询多个项目的标签 */
    private Map<Long, List<TagVO>> batchGetTags(List<Long> projectIds) {
        if (projectIds.isEmpty()) return Collections.emptyMap();
        List<ProjectsTags> links = projectsTagsMapper.selectList(
                new LambdaQueryWrapper<ProjectsTags>().in(ProjectsTags::getProjectId, projectIds));
        if (links.isEmpty()) return Collections.emptyMap();
        List<Long> tagIds = links.stream().map(ProjectsTags::getTagId).distinct().collect(Collectors.toList());
        List<Tags> tags = tagsMapper.selectBatchIds(tagIds);
        Map<Long, TagVO> tagVoMap = tags.stream().collect(Collectors.toMap(Tags::getId, TagVO::from));
        Map<Long, List<TagVO>> result = new HashMap<>();
        for (ProjectsTags link : links) {
            TagVO vo = tagVoMap.get(link.getTagId());
            if (vo != null) result.computeIfAbsent(link.getProjectId(), k -> new ArrayList<>()).add(vo);
        }
        return result;
    }

    private ProjectsVO toVO(Projects project, List<TagVO> tags) {
        ProjectsVO vo = new ProjectsVO();
        vo.setId(project.getId());
        vo.setName(project.getName());
        vo.setSlug(project.getSlug());
        vo.setDescription(project.getDescription());
        vo.setDemoUrl(project.getDemoUrl());
        vo.setGithubUrl(project.getGithubUrl());
        vo.setTags(tags);
        return vo;
    }


    private List<TagVO> getTagsByProjectId(Long projectId) {
        List<ProjectsTags> links = projectsTagsMapper.selectList(
                new LambdaQueryWrapper<ProjectsTags>().eq(ProjectsTags::getProjectId, projectId));
        if (links.isEmpty()) return Collections.emptyList();
        List<Long> tagIds = links.stream().map(ProjectsTags::getTagId).collect(Collectors.toList());
        return tagsMapper.selectBatchIds(tagIds).stream().map(TagVO::from).collect(Collectors.toList());
    }

    /** 维护项目-标签关联 */
    private void handleTags(Long projectId, List<Long> tagIds, List<String> newTags) {
        if (tagIds != null) {
            for (Long tagId : tagIds) {
                ProjectsTags link = new ProjectsTags();
                link.setProjectId(projectId);
                link.setTagId(tagId);
                projectsTagsMapper.insert(link);
            }
        }
        if (newTags != null && !newTags.isEmpty()) {
            List<Tags> existing = tagsMapper.selectList(
                    new LambdaQueryWrapper<Tags>().in(t -> t.getName(), newTags));
            Set<String> existingNames = existing.stream().map(t -> t.getName()).collect(Collectors.toSet());
            for (String name : newTags) {
                Tags tag;
                if (existingNames.contains(name)) {
                    tag = existing.stream().filter(t -> t.getName().equals(name)).findFirst()
                            .orElseThrow(() -> new IllegalStateException("标签数据异常"));
                } else {
                    tag = new Tags();
                    tag.setName(name);
                    tag.setSlug(SlugUtil.toSlug(name));
                    tagsMapper.insert(tag);
                    existingNames.add(name);
                }
                ProjectsTags link = new ProjectsTags();
                link.setProjectId(projectId);
                link.setTagId(tag.getId());
                projectsTagsMapper.insert(link);
            }
        }
    }

    // ==================== 公开方法 ====================

    /** 前台项目列表（全量）。按 id 倒序 */
    @Override
    public List<ProjectsVO> list() {
        return queryList(null);
    }

    /** 前台项目列表（限制条数），供首页使用 */
    @Override
    public List<ProjectsVO> list(int limit) {
        return queryList(limit);
    }

    /** 后台创建项目。slug 自动生成 */
    @Override
    @Transactional
    public void create(ProjectsCreateRequest request) {
        Projects project = new Projects();
        project.setName(request.getName());
        project.setSlug(SlugUtil.toUniqueSlug(request.getName()));
        project.setDescription(request.getDescription());
        project.setDemoUrl(request.getDemoUrl());
        project.setGithubUrl(request.getGithubUrl());
        projectsMapper.insert(project);
        handleTags(project.getId(), request.getTagIds(), request.getNewTags());
    }

    /** 后台更新项目。不存在时抛 NoSuchElementException */
    @Override
    @Transactional
    public void update(Long id, ProjectsCreateRequest request) {
        Projects project = projectsMapper.selectById(id);
        if (project == null) throw new NoSuchElementException("项目不存在");
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setDemoUrl(request.getDemoUrl());
        project.setGithubUrl(request.getGithubUrl());
        projectsMapper.updateById(project);
        projectsTagsMapper.delete(new LambdaQueryWrapper<ProjectsTags>().eq(ProjectsTags::getProjectId, id));
        handleTags(id, request.getTagIds(), request.getNewTags());
    }

    /** 后台获取项目详情（供编辑页加载） */
    @Override
    public ProjectsVO getById(Long id) {
        Projects project = projectsMapper.selectById(id);
        if (project == null) return null;
        return toVO(project, getTagsByProjectId(project.getId()));
    }

    /** 后台删除项目 */
    @Override
    @Transactional
    public void delete(Long id) {
        projectsTagsMapper.delete(new LambdaQueryWrapper<ProjectsTags>().eq(ProjectsTags::getProjectId, id));
        projectsMapper.deleteById(id);
    }
}

package com.javaee.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.javaee.blog.dto.request.ProjectCreateRequest;
import com.javaee.blog.dto.vo.ProjectVO;
import com.javaee.blog.dto.vo.TagVO;
import com.javaee.blog.entity.Project;
import com.javaee.blog.entity.association.ProjectTags;
import com.javaee.blog.entity.Tag;
import com.javaee.blog.mapper.ProjectMapper;
import com.javaee.blog.mapper.ProjectTagsMapper;
import com.javaee.blog.mapper.TagMapper;
import com.javaee.blog.service.ProjectService;
import com.javaee.blog.util.SlugUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectMapper projectMapper;
    private final TagMapper tagMapper;
    private final ProjectTagsMapper projectTagsMapper;

    // ==================== 私有方法 ====================

    /** 列表查询核心逻辑。按 id 倒序，批量取 tag */
    private List<ProjectVO> queryList(Integer limit) {
        LambdaQueryWrapper<Project> wrapper = new LambdaQueryWrapper<Project>()
                .orderByDesc(Project::getId);
        if (limit != null) wrapper.last("LIMIT " + limit);
        List<Project> projects = projectMapper.selectList(wrapper);
        Map<Long, List<TagVO>> tagMap = batchGetTags(projects.stream().map(Project::getId).collect(Collectors.toList()));
        return projects.stream().map(p -> toVO(p, tagMap.getOrDefault(p.getId(), Collections.emptyList()))).collect(Collectors.toList());
    }

    /** 批量查询多个项目的标签 */
    private Map<Long, List<TagVO>> batchGetTags(List<Long> projectIds) {
        if (projectIds.isEmpty()) return Collections.emptyMap();
        List<ProjectTags> links = projectTagsMapper.selectList(
                new LambdaQueryWrapper<ProjectTags>().in(ProjectTags::getProjectId, projectIds));
        if (links.isEmpty()) return Collections.emptyMap();
        List<Long> tagIds = links.stream().map(ProjectTags::getTagId).distinct().collect(Collectors.toList());
        List<Tag> tags = tagMapper.selectBatchIds(tagIds);
        Map<Long, TagVO> tagVoMap = tags.stream().collect(Collectors.toMap(Tag::getId, TagVO::from));
        Map<Long, List<TagVO>> result = new HashMap<>();
        for (ProjectTags link : links) {
            TagVO vo = tagVoMap.get(link.getTagId());
            if (vo != null) result.computeIfAbsent(link.getProjectId(), k -> new ArrayList<>()).add(vo);
        }
        return result;
    }

    private ProjectVO toVO(Project project, List<TagVO> tags) {
        ProjectVO vo = new ProjectVO();
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
        List<ProjectTags> links = projectTagsMapper.selectList(
                new LambdaQueryWrapper<ProjectTags>().eq(ProjectTags::getProjectId, projectId));
        if (links.isEmpty()) return Collections.emptyList();
        List<Long> tagIds = links.stream().map(ProjectTags::getTagId).collect(Collectors.toList());
        return tagMapper.selectBatchIds(tagIds).stream().map(TagVO::from).collect(Collectors.toList());
    }

    /** 维护项目-标签关联 */
    private void handleTags(Long projectId, List<Long> tagIds, List<String> newTags) {
        if (tagIds != null) {
            for (Long tagId : tagIds) {
                ProjectTags link = new ProjectTags();
                link.setProjectId(projectId);
                link.setTagId(tagId);
                projectTagsMapper.insert(link);
            }
        }
        if (newTags != null && !newTags.isEmpty()) {
            List<Tag> existing = tagMapper.selectList(
                    new LambdaQueryWrapper<Tag>().in(t -> t.getName(), newTags));
            Set<String> existingNames = existing.stream().map(t -> t.getName()).collect(Collectors.toSet());
            for (String name : newTags) {
                Tag tag;
                if (existingNames.contains(name)) {
                    tag = existing.stream().filter(t -> t.getName().equals(name)).findFirst()
                            .orElseThrow(() -> new IllegalStateException("标签数据异常"));
                } else {
                    tag = new Tag();
                    tag.setName(name);
                    tag.setSlug(SlugUtil.toSlug(name));
                    tagMapper.insert(tag);
                    existingNames.add(name);
                }
                ProjectTags link = new ProjectTags();
                link.setProjectId(projectId);
                link.setTagId(tag.getId());
                projectTagsMapper.insert(link);
            }
        }
    }

    // ==================== 公开方法 ====================

    /** 前台项目列表（全量）。按 id 倒序 */
    @Override
    public List<ProjectVO> list() {
        return queryList(null);
    }

    /** 前台项目列表（限制条数），供首页使用 */
    @Override
    public List<ProjectVO> list(int limit) {
        return queryList(limit);
    }

    /** 后台创建项目。slug 自动生成 */
    @Override
    @Transactional
    public void create(ProjectCreateRequest request) {
        Project project = new Project();
        project.setName(request.getName());
        project.setSlug(SlugUtil.toUniqueSlug(request.getName()));
        project.setDescription(request.getDescription());
        project.setDemoUrl(request.getDemoUrl());
        project.setGithubUrl(request.getGithubUrl());
        projectMapper.insert(project);
        handleTags(project.getId(), request.getTagIds(), request.getNewTags());
    }

    /** 后台更新项目。不存在时抛 NoSuchElementException */
    @Override
    @Transactional
    public void update(Long id, ProjectCreateRequest request) {
        Project project = projectMapper.selectById(id);
        if (project == null) throw new NoSuchElementException("项目不存在");
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setDemoUrl(request.getDemoUrl());
        project.setGithubUrl(request.getGithubUrl());
        projectMapper.updateById(project);
        projectTagsMapper.delete(new LambdaQueryWrapper<ProjectTags>().eq(ProjectTags::getProjectId, id));
        handleTags(id, request.getTagIds(), request.getNewTags());
    }

    /** 后台获取项目详情（供编辑页加载） */
    @Override
    public ProjectVO getById(Long id) {
        Project project = projectMapper.selectById(id);
        if (project == null) return null;
        return toVO(project, getTagsByProjectId(project.getId()));
    }

    /** 后台删除项目 */
    @Override
    @Transactional
    public void delete(Long id) {
        projectTagsMapper.delete(new LambdaQueryWrapper<ProjectTags>().eq(ProjectTags::getProjectId, id));
        projectMapper.deleteById(id);
    }
}

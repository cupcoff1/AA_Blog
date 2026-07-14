package com.javaee.blog.service;

import com.javaee.blog.dto.request.ProjectCreateRequest;
import com.javaee.blog.dto.vo.ProjectVO;

import java.util.List;

public interface ProjectService {

    /** 前台：项目列表 */
    List<ProjectVO> list();

    /** 取前 N 条 */
    List<ProjectVO> list(int limit);

    /** 后台：创建项目 */
    void create(ProjectCreateRequest request);

    /** 后台：编辑项目（只改基本信息，不动标签） */
    void update(Long id, ProjectCreateRequest request);

    /** 后台：编辑项目标签 */
    void updateTags(Long id, List<Long> tagIds, List<String> newTags);

    /** 后台：获取项目（供编辑页使用） */
    ProjectVO getById(Long id);

    /** 后台：删除项目 */
    void delete(Long id);
}

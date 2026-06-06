package com.javaee.blog.service;

import com.javaee.blog.dto.request.ProjectsCreateRequest;
import com.javaee.blog.dto.vo.ProjectsVO;

import java.util.List;

public interface ProjectsService {

    /** 前台：项目列表 */
    List<ProjectsVO> list();

    /** 取前 N 条 */
    List<ProjectsVO> list(int limit);

    /** 后台：创建项目 */
    void create(ProjectsCreateRequest request);

    /** 后台：编辑项目 */
    void update(Long id, ProjectsCreateRequest request);

    /** 后台：获取项目（供编辑页使用） */
    ProjectsVO getById(Long id);

    /** 后台：删除项目 */
    void delete(Long id);
}

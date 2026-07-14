import api from './client'
import type { ProjectVO, ProjectCreateRequest } from '@/models/types'

/** 前台列表 */
export const listProjects = () =>
  api.get<ProjectVO[]>('/projects')

/** 后台详情（编辑页加载用） */
export const getAdminProject = (id: number) =>
  api.get<ProjectVO>(`/admin/projects/${id}`)

/** 创建 */
export const createProject = (data: ProjectCreateRequest) =>
  api.post('/admin/projects', data)

/** 更新 */
export const updateProject = (id: number, data: ProjectCreateRequest) =>
  api.put(`/admin/projects/${id}`, data)

/** 删除 */
export const deleteProject = (id: number) =>
  api.delete(`/admin/projects/${id}`)

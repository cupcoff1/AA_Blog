import api from './client'
import type { BlogListVO, BlogVO, BlogCreateRequest } from '@/models/types'

/** 前台列表 */
export const listBlogs = (params?: { q?: string; tag?: string }) =>
  api.get<BlogListVO[]>('/blog', { params })

/** 前台详情 */
export const getBlogBySlug = (slug: string) =>
  api.get<BlogVO>(`/blog/${slug}`)

/** 后台列表 */
export const listAdminBlogs = (params?: { q?: string; tag?: string }) =>
  api.get<BlogListVO[]>('/admin/blog', { params })

/** 后台详情 */
export const getAdminBlog = (id: number) =>
  api.get<BlogVO>(`/admin/blog/${id}`)

/** 创建 */
export const createBlog = (data: BlogCreateRequest) =>
  api.post('/admin/blog', data)

/** 更新 */
export const updateBlog = (id: number, data: BlogCreateRequest) =>
  api.put(`/admin/blog/${id}`, data)

/** 删除 */
export const deleteBlog = (id: number) =>
  api.delete(`/admin/blog/${id}`)

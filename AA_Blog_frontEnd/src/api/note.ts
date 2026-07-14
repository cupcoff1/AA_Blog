import api from './client'
import type { NoteVO, NoteCreateRequest } from '@/models/types'

/** 前台列表（支持关键词和标签筛选） */
export const listNotes = (params?: { q?: string; tag?: string }) =>
  api.get<NoteVO[]>('/notes', { params })

/** 后台详情（编辑页加载用） */
export const getAdminNote = (id: number) =>
  api.get<NoteVO>(`/admin/notes/${id}`)

/** 创建 */
export const createNote = (data: NoteCreateRequest) =>
  api.post('/admin/notes', data)

/** 更新 */
export const updateNote = (id: number, data: NoteCreateRequest) =>
  api.put(`/admin/notes/${id}`, data)

/** 删除 */
export const deleteNote = (id: number) =>
  api.delete(`/admin/notes/${id}`)

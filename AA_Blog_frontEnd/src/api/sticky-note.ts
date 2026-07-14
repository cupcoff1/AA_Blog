import api from './client'
import type { StickyNoteVO } from '@/models/types'

export interface StickyNoteCreateBody {
  content: string
  color: string
  rotate: number
  category?: string
}

/** 获取便签（source: admin | guest） */
export const listStickyNotes = (source: 'admin' | 'guest') =>
  api.get<StickyNoteVO[]>(`/sticky-notes?source=${source}`)

/** 游客创建便签 */
export const createGuestNote = (data: StickyNoteCreateBody) =>
  api.post('/sticky-notes', data)

/** 游客删除自己的便签 */
export const deleteGuestNote = (id: number) =>
  api.delete(`/sticky-notes/${id}`)

/** 管理员创建便签 */
export const createAdminNote = (data: StickyNoteCreateBody) =>
  api.post('/admin/sticky-notes', data)

/** 管理员删除便签 */
export const deleteAdminNote = (id: number) =>
  api.delete(`/admin/sticky-notes/${id}`)

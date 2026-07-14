import api from './client'
import type { HeroQuoteVO, HeroConfigVO } from '@/models/types'

export interface HeroQuoteCreateBody {
  content: string
  author: string
  source: string
}

/** 首页引语列表 */
export const listQuotes = () =>
  api.get<HeroQuoteVO[]>('/hero-quotes')

/** 添加引语 */
export const createQuote = (data: HeroQuoteCreateBody) =>
  api.post('/admin/hero-quotes', data)

/** 删除引语 */
export const deleteQuote = (id: number) =>
  api.delete(`/admin/hero-quotes/${id}`)

/** Hero 图片配置（亮色/暗色） */
export const getHeroConfig = () =>
  api.get<HeroConfigVO>('/hero-config')

/** 上传 Hero 图片 */
export const uploadHeroImage = (type: 'light' | 'dark', file: File) => {
  const form = new FormData()
  form.append('file', file)
  return api.post<{ url: string }>(`/admin/hero-image?type=${type}`, form)
}

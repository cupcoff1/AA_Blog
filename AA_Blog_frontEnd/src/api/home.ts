import api from './client'
import type { HomeVO } from '@/models/types'

/** 首页数据（最新 Blog / Note / Project） */
export const getHome = () =>
  api.get<HomeVO>('/home')

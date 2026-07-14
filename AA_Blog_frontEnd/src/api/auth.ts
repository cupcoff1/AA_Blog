import api from './client'
import type { LoginRequest, ChangePasswordRequest } from '@/models/types'

/** 管理员登录 */
export const login = (data: LoginRequest) =>
  api.post('/admin/login', data)

/** 管理员退出 */
export const logout = () =>
  api.post('/admin/logout')

/** 检查管理员登录状态 */
export const checkAdminStatus = () =>
  api.get<{ authenticated: boolean }>('/admin/status')

/** 修改密码 */
export const changePassword = (data: ChangePasswordRequest) =>
  api.put('/admin/password', data)

/** GitHub OAuth 授权页 URL */
export const getGitHubAuthUrl = () =>
  api.get<{ url: string }>('/auth/github/url')

/** GitHub OAuth 回调（换取 token 和用户信息） */
export const githubCallback = (code: string) =>
  api.get<{ name: string; avatar: string }>(`/auth/github/callback?code=${encodeURIComponent(code)}`)

/** 评论者退出 */
export const commenterLogout = () =>
  api.post('/auth/logout')

/** 检查评论者登录状态 */
export const checkCommenterStatus = () =>
  api.get<{ authenticated: boolean; name?: string; avatar?: string }>('/auth/status')

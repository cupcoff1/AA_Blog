import { ref } from 'vue'
import api from '@/api/client'

/** 全局管理员认证状态，路由守卫和组件共享 */
export const isAdmin = ref<boolean | null>(null) // null = 尚未检查

/** 刷新认证状态（登录成功或登出后调用） */
export async function refreshAuth() {
  try {
    const data = await api.get<{ authenticated: boolean }>('/admin/status')
    isAdmin.value = data.authenticated
  } catch {
    isAdmin.value = false
  }
}

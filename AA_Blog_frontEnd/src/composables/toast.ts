import { ref } from 'vue'

export interface ToastItem {
  id: number
  message: string
  type: 'error' | 'success'
}

const toasts = ref<ToastItem[]>([])
let nextId = 0

function remove(id: number) {
  toasts.value = toasts.value.filter(t => t.id !== id)
}

/** 全局调用（组件内或组件外均可） */
export function showToast(message: string, type: 'error' | 'success' = 'error') {
  const id = nextId++
  toasts.value.push({ id, message, type })
  setTimeout(() => remove(id), 4000)
}

/** 组件内使用（获取响应式列表 + 移除方法） */
export function useToast() {
  return { toasts, remove: (id: number) => remove(id) }
}

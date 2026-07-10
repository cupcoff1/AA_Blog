import axios from 'axios'
import type { AxiosRequestConfig } from 'axios'

const http = axios.create({
  baseURL: '/api',
  timeout: 10000,
  withCredentials: true  // 发送 Cookie（httpOnly token）
})

// 响应拦截：解包 Result，处理 401
http.interceptors.response.use(
  res => {
    const result = res.data
    if (result.code === 200) {
      return result.data
    }
    return Promise.reject(new Error(result.message || '请求失败'))
  },
  err => {
    if (err.response?.status === 401) {
      const path = window.location.pathname
      if (path.startsWith('/admin')) {
        window.location.href = '/admin/login'
      }
    }
    return Promise.reject(err)
  }
)

// 泛型包装：调用方 api.get<T>(...) 返回 T，不用手动标注
const api = {
  get<T = unknown>(url: string, config?: AxiosRequestConfig) {
    return http.get(url, config) as Promise<T>
  },
  post<T = unknown>(url: string, data?: unknown, config?: AxiosRequestConfig) {
    return http.post(url, data, config) as Promise<T>
  },
  put<T = unknown>(url: string, data?: unknown, config?: AxiosRequestConfig) {
    return http.put(url, data, config) as Promise<T>
  },
  delete<T = unknown>(url: string, config?: AxiosRequestConfig) {
    return http.delete(url, config) as Promise<T>
  }
}

export default api

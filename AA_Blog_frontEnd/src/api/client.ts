import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 请求拦截：自动带 Token
api.interceptors.request.use(config => {
  const token = localStorage.getItem('admin_token') || localStorage.getItem('commenter_token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// 响应拦截：解包 Result，处理 401
api.interceptors.response.use(
  res => {
    const result = res.data
    if (result.code === 200) {
      return result.data      // 只返回 data，调用方不用写 .data.data
    }
    return Promise.reject(new Error(result.message || '请求失败'))
  },
  err => {
    if (err.response?.status === 401) {
      localStorage.removeItem('admin_token')
      const path = window.location.pathname
      if (path.startsWith('/admin')) {
        window.location.href = '/admin/login'
      }
    }
    return Promise.reject(err)
  }
)

export default api

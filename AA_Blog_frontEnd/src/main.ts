import { createApp } from 'vue'
import router from './router/routes'
import { showToast } from './composables/toast'
import './style.css'
import App from './App.vue'

const app = createApp(App)

// 全局错误兜底（组件内未捕获的错误落到这里）
app.config.errorHandler = (err: unknown, instance, info) => {
  const message = err instanceof Error ? err.message : '未知错误'
  console.error('[Vue Error]', message, { component: instance?.$.type?.name, info })
  showToast(message)
}

// 全局未捕获 Promise 拒绝兜底（未 try/catch 的 API 调用等）
window.addEventListener('unhandledrejection', (event) => {
  const message = event.reason instanceof Error ? event.reason.message : '请求失败'
  console.error('[Unhandled Rejection]', event.reason)
  showToast(message)
})

app.use(router)
app.mount('#app')

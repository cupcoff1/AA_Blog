<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import api from '@/api/client'

const router = useRouter()
const username = ref('')
const password = ref('')
const loading = ref(false)
const error = ref('')

const login = async () => {
  if (!username.value || !password.value) {
    error.value = '请填写用户名和密码'
    return
  }
  error.value = ''
  loading.value = true
  try {
    const { token } = await api.post('/admin/login', {
      username: username.value,
      password: password.value
    })
    localStorage.setItem('admin_token', token)
    router.push('/admin')
  } catch (e: any) {
    error.value = e.message || '登录失败'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="login-page">
    <h1>管理员登录</h1>
    <div class="error" v-if="error">{{ error }}</div>
    <form @submit.prevent="login">
      <input v-model="username" type="text" placeholder="用户名" autocomplete="username" />
      <input v-model="password" type="password" placeholder="密码" autocomplete="current-password" />
      <button type="submit" :disabled="loading">
        {{ loading ? '登录中...' : '登录' }}
      </button>
    </form>
  </div>
</template>

<style scoped>
.login-page {
  max-width: 360px;
  margin: 5rem auto;
  padding: 0 1.5rem;
}

.login-page h1 {
  text-align: center;
  margin-bottom: 1.5rem;
}

.error {
  background: #fed7d7;
  color: #c53030;
  padding: 0.6rem 1rem;
  border-radius: 6px;
  margin-bottom: 1rem;
  font-size: 0.9em;
}

form {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

input {
  padding: 0.6rem 1rem;
  border: 1px solid var(--border);
  border-radius: 6px;
  font-size: 1em;
  background: var(--bg);
  color: var(--text);
  outline: none;
}

input:focus {
  border-color: var(--link);
}

button {
  padding: 0.7rem;
  background: var(--link);
  color: #fff;
  border: none;
  border-radius: 6px;
  font-size: 1em;
  cursor: pointer;
}

button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>


<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import api from '@/api/client'
import LogoIcon from '@/components/LogoIcon.vue'

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
    router.push('/')
  } catch (e: unknown) {
    error.value = e instanceof Error ? e.message : '登录失败'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="login-page">
    <!-- 装饰便签 -->
    <div class="deco-note note-1">记得<br>喝水<br>(´▽`ʃ♡ƪ)</div>
    <div class="deco-note note-2">咕咕<br>嘎嘎<br>(°〆°)〜☆</div>
    <div class="deco-note note-3">我要<br>成为<br>...高手!<br>(๑•̀ㅂ•́)و✧</div>
    <div class="deco-note note-4">用户名<br>root<br>密码<br>admin123</div>

    <div class="login-card">
      <RouterLink to="/" class="login-logo">
        <LogoIcon class="logo-icon" />
        <span>AA_Blog</span>
      </RouterLink>
      <p class="login-sub">管理员登录</p>
      <div class="error" v-if="error">{{ error }}</div>
      <form @submit.prevent="login">
        <input v-model="username" type="text" placeholder="用户名" autocomplete="username" />
        <input v-model="password" type="password" placeholder="密码" autocomplete="current-password" />
        <button type="submit" :disabled="loading">
          {{ loading ? '潜入中...' : '让我康康' }}
        </button>
      </form>
    </div>
  </div>
</template>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 2rem;
  position: relative;
  overflow: hidden;
}
.deco-note {
  position: absolute;
  width: 170px;
  height: 170px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-family: 'Ma Shan Zheng', cursive; font-size: 1.1em; line-height: 1.4; color: rgba(0,0,0,0.6);
  opacity: 0.7;
  box-shadow: 2px 3px 8px rgba(0,0,0,0.1);
  background-size: 100% 28px;
}
.deco-note::after {
  content: ''; position: absolute; inset: 0;
  background:
    repeating-linear-gradient(transparent 0, transparent 26px, rgba(0,0,0,0.04) 26px, rgba(0,0,0,0.04) 28px),
    repeating-linear-gradient(transparent 0, transparent 23px, rgba(0,0,0,0.02) 23px, rgba(0,0,0,0.02) 24px);
  pointer-events: none;
}
.note-1 { top: 8%; left: 3%; background: #fff3cd; transform: rotate(-6deg); }
.note-2 { top: 5%; right: 6%; background: #d4edda; transform: rotate(4deg); }
.note-3 { bottom: 25%; left: 10%; background: #cce5ff; transform: rotate(-3deg); }
.note-4 { bottom: 8%; right: 2%; background: #f8d7da; transform: rotate(7deg); }
.login-card {
  width: 100%;
  max-width: 380px;
  padding: 3rem 2rem 2.5rem;
  position: relative;
  background-color: rgba(200, 146, 231, 0.15);
  box-shadow: 2px 3px 8px rgba(0,0,0,0.1);
  transform: rotate(-0.5deg);
  overflow: hidden;
}
.login-card::before {
  content: ''; position: absolute; top: 0; left: 0;
  width: 0; height: 0;
  border-style: solid;
  border-width: 40px 40px 0 0;
  border-color: var(--bg) transparent transparent transparent;
  z-index: 2;
}
.login-card::after {
  content: ''; position: absolute; inset: 0;
  background:
    repeating-linear-gradient(transparent 0, transparent 26px, rgba(0,0,0,0.04) 26px, rgba(0,0,0,0.04) 28px),
    repeating-linear-gradient(transparent 0, transparent 23px, rgba(0,0,0,0.02) 23px, rgba(0,0,0,0.02) 24px);
  pointer-events: none;
}
.login-logo {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  margin-bottom: 0.3rem;
  font-family: var(--heading);
  font-size: 1.3em;
  font-weight: 600;
  color: var(--text);
}
.logo-icon {
  width: 24px;
  height: 24px;
  color: var(--text);
}
.login-sub {
  text-align: center;
  color: var(--text-secondary);
  font-size: 0.9em;
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
  gap: 0.8rem;
}
input {
  padding: 0.5rem 0.8rem;
  border: 1px solid var(--border);
  border-radius: var(--radius);
  font-size: 0.9em;
  background: var(--bg);
  color: var(--text);
  outline: none;
}
input:focus {
  border-color: var(--link);
  box-shadow: 0 0 0 2px rgba(177, 45, 108, 0.1);
}
button {
  margin-top: 0.5rem;
  padding: 0.55rem;
  background: var(--text);
  color: var(--bg);
  border: none;
  border-radius: var(--radius);
  font-size: 1em;
  font-weight: 500;
  cursor: pointer;
  transition: background 0.2s;
}
button:hover {
  opacity: 0.85;
}
button:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}
</style>

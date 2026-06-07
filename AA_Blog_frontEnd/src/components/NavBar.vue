<script setup lang="ts">
import { ref } from 'vue'

const dark = ref(localStorage.getItem('theme') === 'dark')

const toggleTheme = () => {
  dark.value = !dark.value
  document.body.classList.toggle('dark', dark.value)
  localStorage.setItem('theme', dark.value ? 'dark' : 'light')
}

// 初始化
if (dark.value) {
  document.body.classList.add('dark')
}
</script>

<template>
  <nav class="navbar">
    <div class="nav-container">
      <RouterLink to="/" class="logo">AA</RouterLink>
      <div class="nav-links">
        <RouterLink to="/blog">Blog</RouterLink>
        <RouterLink to="/notes">Notes</RouterLink>
        <RouterLink to="/projects">Projects</RouterLink>
        <RouterLink to="/about">About</RouterLink>
        <button class="theme-btn" @click="toggleTheme" :title="dark ? '亮色模式' : '暗色模式'">
          {{ dark ? '☀' : '🌙' }}
        </button>
      </div>
    </div>
  </nav>
</template>

<style scoped>
.navbar {
  border-bottom: 1px solid var(--border);
  padding: 1rem 0;
}

.nav-container {
  max-width: var(--max-width);
  margin: 0 auto;
  padding: 0 1.5rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.logo {
  font-family: var(--serif);
  font-size: 1.3em;
  font-weight: 600;
  color: var(--text);
}

.nav-links {
  display: flex;
  gap: 1.5rem;
  align-items: center;
}

.nav-links a {
  color: var(--text-secondary);
  font-size: 0.95em;
}

.nav-links a:hover,
.nav-links a.router-link-exact-active {
  color: var(--link);
  text-decoration: none;
}

.theme-btn {
  background: none;
  border: none;
  cursor: pointer;
  font-size: 1.2em;
  padding: 0;
}
</style>

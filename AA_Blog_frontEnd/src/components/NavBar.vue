<script setup lang="ts">
import { ref } from 'vue'
const dark = ref(localStorage.getItem('theme') === 'dark')
const toggleTheme = () => {
  dark.value = !dark.value
  document.body.classList.toggle('dark', dark.value)
  localStorage.setItem('theme', dark.value ? 'dark' : 'light')
}
if (dark.value) document.body.classList.add('dark')
</script>

<template>
  <nav class="navbar container">
    <RouterLink to="/" class="logo">AA</RouterLink>
    <div class="nav-links">
      <RouterLink to="/blog">Blog</RouterLink>
      <RouterLink to="/notes">Notes</RouterLink>
      <RouterLink to="/projects">Projects</RouterLink>
      <RouterLink to="/about">About</RouterLink>
      <button class="theme-btn" @click="toggleTheme">{{ dark ? '☀' : '🌙' }}</button>
    </div>
  </nav>
</template>

<style scoped>
.navbar { display: flex; justify-content: space-between; align-items: center; padding: 1.2rem 0; }
.logo { font-family: var(--serif); font-weight: 600; font-size: 1.3em; color: var(--text); }
.nav-links { display: flex; gap: 1.5rem; align-items: center; }
.nav-links a { color: var(--text-secondary); font-size: 0.95em; }
.nav-links a:hover, .nav-links a.router-link-exact-active { color: var(--link); text-decoration: none; }
.theme-btn { background: none; border: none; cursor: pointer; font-size: 1.1em; padding: 0; }
</style>

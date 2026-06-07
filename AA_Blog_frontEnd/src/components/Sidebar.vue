<script setup lang="ts">
import { ref } from 'vue'
import { BookOpen, Pencil, FolderOpen, User, Sun, Moon, GitFork } from '@lucide/vue'
import LogoIcon from './LogoIcon.vue'

const dark = ref(localStorage.getItem('theme') === 'dark')
const toggleTheme = () => {
  dark.value = !dark.value
  document.body.classList.toggle('dark', dark.value)
  localStorage.setItem('theme', dark.value ? 'dark' : 'light')
}
if (dark.value) document.body.classList.add('dark')
</script>

<template>
  <aside class="sidebar">
    <section class="sidebar-section">
      <div class="sidebar-title-row">
        <RouterLink to="/" class="title-link">
          <LogoIcon class="title-icon" />
          <span class="site-name">AA_</span>
        </RouterLink>
        <button class="theme-btn" @click="toggleTheme">
          <Sun v-if="dark" :size="18" />
          <Moon v-else :size="18" />
        </button>
      </div>
    </section>

    <section class="sidebar-section">
      <h2 class="sidebar-heading">About Me</h2>
      <p class="sidebar-bio">I'm <RouterLink to="/about">AA_</RouterLink>, Student. This is my digital garden. 🌱</p>
    </section>

    <section class="sidebar-section">
      <nav class="sidebar-nav">
        <RouterLink to="/blog"><BookOpen :size="16" /> Blog</RouterLink>
        <RouterLink to="/notes"><Pencil :size="16" /> Notes</RouterLink>
        <RouterLink to="/projects"><FolderOpen :size="16" /> Projects</RouterLink>
        <RouterLink to="/about"><User :size="16" /> About Me</RouterLink>
      </nav>
    </section>

    <section class="sidebar-section">
      <h2 class="sidebar-heading">Stay Connected</h2>
      <div class="sidebar-links">
        <a href="https://github.com/cupcoff1" target="_blank"><GitFork :size="16" /> GitHub</a>
      </div>
    </section>
  </aside>
</template>

<style scoped>
.sidebar {
  display: none;
  position: sticky; top: 0; height: 100vh; overflow-y: auto;
  flex-direction: column;
  border-right: 1px solid rgba(128,128,128,0.15); background: var(--bg);
  min-width: var(--sidebar-width); width: var(--sidebar-width);
}
@media screen and (min-width: 900px) { .sidebar { display: flex; } }
.sidebar-section { margin: 0 1.25rem; padding: 1.25rem 0; border-bottom: 1px solid rgba(128,128,128,0.15); }
.sidebar-section:first-child { margin-top: 1.5rem; }
.sidebar-section:last-child { border-bottom: none; }
.sidebar-title-row { display: flex; align-items: center; justify-content: space-between; }
.title-link { display: flex; align-items: center; gap: 8px; }
.title-icon { width: 18px; height: 18px; color: var(--text); flex-shrink: 0; }
.site-name { font-family: var(--heading); font-size: 1.2em; font-weight: 600; color: var(--text); white-space: nowrap; }
.title-link:hover .site-name { color: var(--link); }
.theme-btn {
  display: flex; align-items: center; justify-content: center;
  border: none; background: transparent; cursor: pointer;
  width: 38px; height: 38px; border-radius: var(--radius);
  color: var(--text-secondary);
}
.theme-btn:hover { color: var(--link); background: var(--bg-secondary); }
.sidebar-bio { font-size: 0.92em; color: var(--text-secondary); margin: 0; line-height: 1.5; }
.sidebar-nav { display: flex; flex-direction: column; gap: 2px; }
.sidebar-nav a { display: flex; align-items: center; gap: 8px; color: var(--text-secondary); font-size: 0.93em; padding: 5px 8px; border-radius: var(--radius); }
.sidebar-nav a:hover, .sidebar-nav a.router-link-exact-active { color: var(--link); background: var(--bg-secondary); }
.sidebar-heading { font-family: var(--heading); font-size: 1em; font-weight: 600; color: var(--text-secondary); margin: 0 0 0.5em; }
.sidebar-links { display: flex; flex-direction: column; gap: 4px; }
.sidebar-links a { display: flex; align-items: center; gap: 8px; color: var(--text-secondary); font-size: 0.9em; }
</style>

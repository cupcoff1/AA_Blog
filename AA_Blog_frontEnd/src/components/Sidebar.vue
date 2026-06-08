<script setup lang="ts">
import { ref, computed } from 'vue'
import { BookOpen, Pencil, FolderOpen, User, Sun, Moon, GitFork, LogOut, Plus, MessageSquare, Menu } from '@lucide/vue'
import LogoIcon from './LogoIcon.vue'

const dark = ref(localStorage.getItem('theme') === 'dark')
const isAdmin = computed(() => !!localStorage.getItem('admin_token'))
const menuOpen = ref(false)

const toggleTheme = () => {
  dark.value = !dark.value
  document.body.classList.toggle('dark', dark.value)
  localStorage.setItem('theme', dark.value ? 'dark' : 'light')
}
const logout = () => {
  if (!confirm('(╥﹏╥) 真的要走了吗？')) return
  localStorage.removeItem('admin_token')
  location.href = '/'
}
if (dark.value) document.body.classList.add('dark')
</script>

<template>
  <!-- 移动端顶部导航 -->
  <header class="mobile-nav">
    <RouterLink :to="isAdmin ? '/admin/home' : '/'" class="mobile-logo" title="首页管理">
      <LogoIcon class="title-icon" />
    </RouterLink>
    <RouterLink to="/" class="mobile-site-name">AA_Blog</RouterLink>
    <div class="mobile-actions">
      <button class="theme-btn" @click="toggleTheme">
        <Sun v-if="dark" :size="18" />
        <Moon v-else :size="18" />
      </button>
      <button class="menu-btn" @click="menuOpen = !menuOpen">
        <Menu :size="20" />
      </button>
    </div>
    <Teleport to="body">
      <Transition name="fade">
        <div v-if="menuOpen" class="mobile-overlay" @click="menuOpen = false">
          <nav class="mobile-menu" @click.stop>
            <RouterLink :to="isAdmin ? '/admin/home' : '/'" @click="menuOpen = false">Home</RouterLink>
            <RouterLink to="/blog" @click="menuOpen = false">Blog</RouterLink>
            <RouterLink to="/notes" @click="menuOpen = false">Notes</RouterLink>
            <RouterLink to="/projects" @click="menuOpen = false">Projects</RouterLink>
            <RouterLink to="/about" @click="menuOpen = false">About Me</RouterLink>
            <RouterLink to="/guest" @click="menuOpen = false">Leave a Note</RouterLink>
            <a href="https://github.com/cupcoff1" target="_blank" class="mobile-gh">GitHub</a>
            <div class="mobile-divider" />
            <a v-if="isAdmin" href="#" @click.prevent="logout" class="logout-link"><LogOut :size="16" /></a>
            <div class="mobile-bio">
              I'm AA_ , Student. This is my digital garden. <RouterLink to="/admin/login" @click="menuOpen = false" class="secret-link">🌱</RouterLink>
            </div>
          </nav>
        </div>
      </Transition>
    </Teleport>
  </header>

  <aside class="sidebar">
    <section class="sidebar-section">
      <div class="sidebar-title-row">
        <div class="title-link">
          <RouterLink :to="isAdmin ? '/admin/home' : '/'" class="logo-link" title="首页管理">
            <LogoIcon class="title-icon" />
          </RouterLink>
          <RouterLink to="/" class="site-name">AA_Blog</RouterLink>
        </div>
        <button class="theme-btn" @click="toggleTheme">
          <Sun v-if="dark" :size="18" />
          <Moon v-else :size="18" />
        </button>
      </div>
    </section>

    <section class="sidebar-section">
      <h2 class="sidebar-heading">About Me</h2>
      <p class="sidebar-bio">I'm <RouterLink to="/about">AA_</RouterLink> , Student. This is my digital garden. <RouterLink to="/admin/login" class="secret-link">🌱</RouterLink></p>
    </section>

    <section class="sidebar-section">
      <nav class="sidebar-nav">
        <div class="nav-row">
          <RouterLink to="/blog" class="nav-item"><BookOpen :size="16" /> Blog</RouterLink>
          <RouterLink v-if="isAdmin" to="/blog/new" class="add-btn" title="新建"><Plus :size="14" /></RouterLink>
        </div>
        <div class="nav-row">
          <RouterLink to="/notes" class="nav-item"><Pencil :size="16" /> Notes</RouterLink>
          <RouterLink v-if="isAdmin" to="/notes/new" class="add-btn" title="新建"><Plus :size="14" /></RouterLink>
        </div>
        <div class="nav-row">
          <RouterLink to="/projects" class="nav-item"><FolderOpen :size="16" /> Projects</RouterLink>
          <RouterLink v-if="isAdmin" to="/projects/new" class="add-btn" title="新建"><Plus :size="14" /></RouterLink>
        </div>
        <RouterLink to="/about" class="nav-item"><User :size="16" /> About Me</RouterLink>
        <RouterLink to="/guest" class="nav-item"><MessageSquare :size="16" /> Leave a Note</RouterLink>
      </nav>
    </section>

    <section class="sidebar-section">
      <h2 class="sidebar-heading">Stay Connected</h2>
      <div class="sidebar-links">
        <a href="https://github.com/cupcoff1" target="_blank"><GitFork :size="16" /> GitHub</a>
      </div>
    </section>

    <section v-if="isAdmin" class="sidebar-section sidebar-footer">
      <a href="#" @click.prevent="logout" class="logout-link"><LogOut :size="16" /> 退出</a>
    </section>

  </aside>
</template>

<style scoped>
/* 移动端顶部导航 */
.mobile-nav { display: flex; align-items: center; gap: 8px; position: sticky; top: 0; z-index: 100; background: var(--bg); border-bottom: 1px solid rgba(128,128,128,0.08); padding: 0 1rem; height: 48px; }
.mobile-logo { display: flex; }
.mobile-site-name { font-family: var(--heading); font-size: 1.1em; font-weight: 600; color: var(--text); flex: 1; }
.mobile-actions { display: flex; align-items: center; gap: 4px; }
.menu-btn { display: flex; align-items: center; justify-content: center; border: none; background: none; cursor: pointer; color: var(--text); padding: 4px; }

.mobile-overlay { position: fixed; inset: 0; z-index: 200; background: rgba(0,0,0,0.25); display: flex; justify-content: flex-end; }
.mobile-menu { width: 240px; height: 100%; background: var(--bg); padding: 5rem 1.5rem 2rem; display: flex; flex-direction: column; gap: 4px; box-shadow: -4px 0 16px rgba(0,0,0,0.08); }
.mobile-menu a { display: block; padding: 0.6rem 0.5rem; color: var(--text-secondary); font-size: 1.05em; border-radius: 6px; }
.mobile-menu a:hover { background: var(--bg-secondary); color: var(--link); }
.mobile-menu .logout-link { color: #e53e3e; margin-top: 1rem; }
.mobile-gh { display: block; padding: 0.6rem 0.5rem; color: var(--text-secondary); font-size: 1.05em; border-radius: 6px; }
.mobile-gh:hover { background: var(--bg-secondary); color: var(--link); }
.mobile-divider { height: 1px; background: rgba(128,128,128,0.1); margin: 0.5rem 0; }
.mobile-bio { padding-top: 1rem; font-size: 0.85em; color: var(--text-secondary); line-height: 1.5; }

.fade-enter-active, .fade-leave-active { transition: opacity 0.2s; }
.fade-enter-from, .fade-leave-to { opacity: 0; }

@media screen and (min-width: 900px) { .mobile-nav { display: none; } }

.sidebar {
  display: none;
  position: sticky; top: 0; height: 100vh; overflow-y: auto;
  flex-direction: column;
  border-right: 1px solid rgba(128,128,128,0.15); background: var(--bg);
  min-width: var(--sidebar-width); width: var(--sidebar-width);
}
@media screen and (min-width: 900px) { .sidebar { display: flex; } }
.sidebar-section { margin: 0 1.25rem 0 0.5rem; padding: 1.25rem 0; border-bottom: 1px solid rgba(128,128,128,0.15); }
.sidebar-section:first-child { margin-top: 1.5rem; }
.sidebar-section:last-child { border-bottom: none; }
.sidebar-title-row { display: flex; align-items: center; justify-content: space-between; }
.title-link { display: flex; align-items: center; gap: 8px; }
.logo-link { display: flex; }
.title-icon { width: 18px; height: 18px; color: var(--text); flex-shrink: 0; }
.logo-link:hover .title-icon { color: var(--link); }
.site-name { font-family: var(--heading); font-size: 1.2em; font-weight: 600; color: var(--text); white-space: nowrap; }
.site-name:hover { color: var(--link); }
.theme-btn {
  display: flex; align-items: center; justify-content: center;
  border: none; background: transparent; cursor: pointer;
  width: 38px; height: 38px; border-radius: var(--radius);
  color: var(--text-secondary);
}
.theme-btn:hover { color: var(--link); background: var(--bg-secondary); }
.sidebar-bio { font-size: 0.92em; color: var(--text-secondary); margin: 0; line-height: 1.5; }
.sidebar-bio a { text-decoration: underline; text-decoration-thickness: 2px; text-decoration-style: dotted; text-decoration-color: #fff; text-underline-offset: 4px; }
.sidebar-nav { display: flex; flex-direction: column; gap: 2px; }
.sidebar-nav a { display: flex; align-items: center; gap: 8px; color: var(--text-secondary); font-size: 0.93em; padding: 5px 8px; border-radius: var(--radius); }
.sidebar-nav a:hover, .sidebar-nav a.router-link-exact-active { color: var(--link); background: var(--bg-secondary); }
.nav-row { display: flex; align-items: center; gap: 2px; }
.nav-row .nav-item { flex: 1; }
.add-btn { display: flex; align-items: center; justify-content: center; width: 28px; height: 28px; border-radius: 50%; color: var(--text-secondary); opacity: 0; }
.nav-row:hover .add-btn { opacity: 0.6; }
.add-btn:hover { opacity: 1 !important; background: var(--bg-secondary); color: var(--link); }
.sidebar-heading { font-family: var(--heading); font-size: 1em; font-weight: 600; color: var(--text-secondary); margin: 0 0 0.8em; }
.sidebar-links { display: flex; flex-direction: column; gap: 2px; }
.sidebar-links a { display: flex; align-items: center; gap: 8px; color: var(--text-secondary); font-size: 0.93em; padding: 5px 8px; border-radius: var(--radius); }
.sidebar-links a:hover { color: var(--link); background: var(--bg-secondary); }
.sidebar-footer { padding-top: 0.5rem !important; }
.logout-link { display: flex; align-items: center; gap: 8px; color: #e53e3e; font-size: 0.93em; padding: 5px 8px; border-radius: var(--radius); }
.logout-link:hover { background: var(--bg-secondary); }
.secret-link { color: inherit; text-decoration: none !important; opacity: 0.6; }
.secret-link:hover { opacity: 1; }

</style>

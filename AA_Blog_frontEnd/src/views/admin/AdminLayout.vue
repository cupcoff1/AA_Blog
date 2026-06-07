<script setup lang="ts">
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()

const token = localStorage.getItem('admin_token')
if (!token) {
  router.replace('/admin/login')
}

const navItems = [
  { path: '/admin', label: '仪表盘' },
  { path: '/admin/blog', label: '文章' },
  { path: '/admin/notes', label: '笔记' },
  { path: '/admin/projects', label: '项目' },
  { path: '/admin/comments', label: '评论' },
  { path: '/admin/about', label: '个人资料' }
]

const logout = () => {
  localStorage.removeItem('admin_token')
  router.push('/admin/login')
}
</script>

<template>
  <div class="admin-layout">
    <aside class="sidebar">
      <RouterLink to="/admin" class="brand">AA Blog</RouterLink>
      <nav>
        <RouterLink v-for="item in navItems" :key="item.path"
          :to="item.path" class="nav-item" :class="{ active: route.path === item.path }">
          {{ item.label }}
        </RouterLink>
      </nav>
      <button class="logout" @click="logout">退出登录</button>
    </aside>
    <main class="admin-content">
      <router-view />
    </main>
  </div>
</template>

<style scoped>
.admin-layout {
  display: flex;
  min-height: 100vh;
}

.sidebar {
  width: 200px;
  border-right: 1px solid var(--border);
  padding: 1.5rem 1rem;
  display: flex;
  flex-direction: column;
}

.brand {
  font-family: var(--serif);
  font-size: 1.2em;
  font-weight: 600;
  color: var(--text);
  margin-bottom: 1.5rem;
}

nav {
  display: flex;
  flex-direction: column;
  gap: 0.3rem;
  flex: 1;
}

.nav-item {
  padding: 0.5rem 0.8rem;
  color: var(--text-secondary);
  border-radius: 6px;
  font-size: 0.95em;
}

.nav-item:hover,
.nav-item.active {
  background: var(--bg-secondary);
  color: var(--link);
  text-decoration: none;
}

.logout {
  margin-top: auto;
  background: none;
  border: none;
  color: #e53e3e;
  cursor: pointer;
  text-align: left;
  padding: 0.5rem 0;
  font-size: 0.95em;
}

.admin-content {
  flex: 1;
  padding: 1.5rem 2rem;
  overflow-x: auto;
}
</style>


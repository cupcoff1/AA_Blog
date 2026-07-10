import { createRouter, createWebHistory } from 'vue-router'
import { isAdmin, refreshAuth } from './auth'

/** 需要管理员认证的路由 */
const ADMIN_PATTERNS = [
  /^\/admin\/(?!login)/,   // /admin/* 除了 /admin/login
  /^\/blog\/new$/,
  /^\/blog\/\d+\/edit$/,
  /^\/notes\/new$/,
  /^\/notes\/\d+\/edit$/,
  /^\/projects\/new$/,
  /^\/projects\/\d+\/edit$/,
]

function needsAuth(path: string): boolean {
  return ADMIN_PATTERNS.some(p => p.test(path))
}

const router = createRouter({
  history: createWebHistory(),
  routes: [
    // ==================== 前台 ====================
    {
      path: '/',
      name: 'home',
      component: () => import('@/views/HomePage.vue')
    },
    {
      path: '/blog',
      name: 'blog-list',
      component: () => import('@/views/BlogListPage.vue')
    },
    {
      path: '/blog/:slug',
      name: 'blog-detail',
      component: () => import('@/views/BlogDetailPage.vue')
    },
    {
      path: '/notes',
      name: 'notes',
      component: () => import('@/views/NotesPage.vue')
    },
    {
      path: '/projects',
      name: 'projects',
      component: () => import('@/views/ProjectsPage.vue')
    },
    {
      path: '/about',
      name: 'about',
      component: () => import('@/views/AboutPage.vue')
    },

    // ==================== 后台 ====================
    { path: '/admin/login', name: 'admin-login', component: () => import('@/views/admin/LoginPage.vue') },
    { path: '/admin', redirect: '/admin/home' },
    { path: '/admin/home', name: 'admin-home', component: () => import('@/views/admin/HomeAdminPage.vue') },

    // 编辑页（管理员专用）
    { path: '/blog/new', name: 'blog-new', component: () => import('@/views/admin/BlogEditPage.vue') },
    { path: '/blog/:id/edit', name: 'blog-edit', component: () => import('@/views/admin/BlogEditPage.vue') },
    { path: '/notes/new', name: 'notes-new', component: () => import('@/views/admin/NotesEditPage.vue') },
    { path: '/notes/:id/edit', name: 'notes-edit', component: () => import('@/views/admin/NotesEditPage.vue') },
    { path: '/projects/new', name: 'projects-new', component: () => import('@/views/admin/ProjectsEditPage.vue') },
    { path: '/projects/:id/edit', name: 'projects-edit', component: () => import('@/views/admin/ProjectsEditPage.vue') },

    // ==================== Auth ====================
    { path: '/auth/callback', name: 'auth-callback', component: () => import('@/views/auth/CallbackPage.vue') },

    // ==================== Guest ====================
    { path: '/guest', name: 'guest', component: () => import('@/views/GuestWall.vue') },

    // ==================== 404 ====================
    {
      path: '/:pathMatch(.*)*',
      name: 'not-found',
      component: () => import('@/views/NotFoundPage.vue')
    }
  ]
})

router.beforeEach(async (to) => {
  if (!needsAuth(to.path)) return true

  if (isAdmin.value === null) await refreshAuth()
  if (!isAdmin.value) return { path: '/admin/login' }
  return true
})

export default router

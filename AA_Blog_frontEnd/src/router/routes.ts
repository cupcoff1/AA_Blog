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
  scrollBehavior(_to, _from, savedPosition) {
    if (savedPosition) return savedPosition
    return { top: 0 }
  },
  routes: [
    // ==================== 前台 ====================
    {
      path: '/', name: 'home', component: () => import('@/views/HomePage.vue'),
      meta: { title: 'AA_Blog' }
    },
    {
      path: '/blog', name: 'blog-list', component: () => import('@/views/BlogListPage.vue'),
      meta: { title: 'Blog - AA_Blog' }
    },
    {
      path: '/blog/:slug', name: 'blog-detail', component: () => import('@/views/BlogDetailPage.vue'),
      meta: { title: '文章 - AA_Blog' }
    },
    {
      path: '/notes', name: 'notes', component: () => import('@/views/NotesPage.vue'),
      meta: { title: 'Notes - AA_Blog' }
    },
    {
      path: '/projects', name: 'projects', component: () => import('@/views/ProjectsPage.vue'),
      meta: { title: 'Projects - AA_Blog' }
    },
    {
      path: '/about', name: 'about', component: () => import('@/views/AboutPage.vue'),
      meta: { title: 'About Me - AA_Blog' }
    },

    // ==================== 后台 ====================
    { path: '/admin/login', name: 'admin-login', component: () => import('@/views/admin/LoginPage.vue'),
      meta: { title: '管理员登录 - AA_Blog' } },
    { path: '/admin', redirect: '/admin/home' },
    { path: '/admin/home', name: 'admin-home', component: () => import('@/views/admin/HomeAdminPage.vue'),
      meta: { title: '首页管理 - AA_Blog' } },

    // 编辑页（管理员专用）
    { path: '/blog/new', name: 'blog-new', component: () => import('@/views/admin/BlogEditPage.vue'),
      meta: { title: '新建文章 - AA_Blog' } },
    { path: '/blog/:id/edit', name: 'blog-edit', component: () => import('@/views/admin/BlogEditPage.vue'),
      meta: { title: '编辑文章 - AA_Blog' } },
    { path: '/notes/new', name: 'notes-new', component: () => import('@/views/admin/NotesEditPage.vue'),
      meta: { title: '新建笔记 - AA_Blog' } },
    { path: '/notes/:id/edit', name: 'notes-edit', component: () => import('@/views/admin/NotesEditPage.vue'),
      meta: { title: '编辑笔记 - AA_Blog' } },
    { path: '/projects/new', name: 'projects-new', component: () => import('@/views/admin/ProjectsEditPage.vue'),
      meta: { title: '新建项目 - AA_Blog' } },
    { path: '/projects/:id/edit', name: 'projects-edit', component: () => import('@/views/admin/ProjectsEditPage.vue'),
      meta: { title: '编辑项目 - AA_Blog' } },

    // ==================== Auth ====================
    { path: '/auth/callback', name: 'auth-callback', component: () => import('@/views/auth/CallbackPage.vue'),
      meta: { title: '登录中... - AA_Blog' } },

    // ==================== Guest ====================
    { path: '/guest', name: 'guest', component: () => import('@/views/GuestWall.vue'),
      meta: { title: 'Leave a Note - AA_Blog' } },

    // ==================== 404 ====================
    {
      path: '/:pathMatch(.*)*', name: 'not-found', component: () => import('@/views/NotFoundPage.vue'),
      meta: { title: '404 - AA_Blog' }
    }
  ]
})

router.afterEach((to) => {
  document.title = (to.meta.title as string) || 'AA_Blog'
})

router.beforeEach(async (to) => {
  if (!needsAuth(to.path)) return true

  if (isAdmin.value === null) await refreshAuth()
  if (!isAdmin.value) return { path: '/admin/login' }
  return true
})

export default router

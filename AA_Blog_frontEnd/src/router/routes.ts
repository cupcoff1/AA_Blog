import { createRouter, createWebHistory } from 'vue-router'

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
    { path: '/admin/comments', name: 'admin-comments', component: () => import('@/views/admin/CommentsPage.vue') },


    { path: '/admin/home', name: 'admin-home', component: () => import('@/views/admin/HomeAdminPage.vue') },

    // 编辑页（管理员专用）
    { path: '/blog/new', name: 'blog-new', component: () => import('@/views/admin/BlogEditPage.vue') },
    { path: '/blog/:id/edit', name: 'blog-edit', component: () => import('@/views/admin/BlogEditPage.vue') },
    { path: '/notes/new', name: 'notes-new', component: () => import('@/views/admin/NotesEditPage.vue') },
    { path: '/notes/:id/edit', name: 'notes-edit', component: () => import('@/views/admin/NotesEditPage.vue') },
    { path: '/projects/new', name: 'projects-new', component: () => import('@/views/admin/ProjectsEditPage.vue') },
    { path: '/projects/:id/edit', name: 'projects-edit', component: () => import('@/views/admin/ProjectsEditPage.vue') },

    // ==================== 404 ====================
    {
      path: '/:pathMatch(.*)*',
      name: 'not-found',
      component: () => import('@/views/NotFoundPage.vue')
    }
  ]
})

export default router

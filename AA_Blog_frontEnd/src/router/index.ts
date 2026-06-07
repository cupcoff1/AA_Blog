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
    {
      path: '/admin/login',
      name: 'admin-login',
      component: () => import('@/views/admin/LoginPage.vue')
    },
    {
      path: '/admin',
      name: 'admin-dashboard',
      component: () => import('@/views/admin/DashboardPage.vue')
    },
    {
      path: '/admin/blog',
      name: 'admin-blog-list',
      component: () => import('@/views/admin/BlogListPage.vue')
    },
    {
      path: '/admin/blog/new',
      name: 'admin-blog-new',
      component: () => import('@/views/admin/BlogEditPage.vue')
    },
    {
      path: '/admin/blog/:id/edit',
      name: 'admin-blog-edit',
      component: () => import('@/views/admin/BlogEditPage.vue')
    },
    {
      path: '/admin/notes',
      name: 'admin-notes-list',
      component: () => import('@/views/admin/NotesListPage.vue')
    },
    {
      path: '/admin/notes/new',
      name: 'admin-notes-new',
      component: () => import('@/views/admin/NotesEditPage.vue')
    },
    {
      path: '/admin/notes/:id/edit',
      name: 'admin-notes-edit',
      component: () => import('@/views/admin/NotesEditPage.vue')
    },
    {
      path: '/admin/projects',
      name: 'admin-projects-list',
      component: () => import('@/views/admin/ProjectsListPage.vue')
    },
    {
      path: '/admin/projects/new',
      name: 'admin-projects-new',
      component: () => import('@/views/admin/ProjectsEditPage.vue')
    },
    {
      path: '/admin/projects/:id/edit',
      name: 'admin-projects-edit',
      component: () => import('@/views/admin/ProjectsEditPage.vue')
    },
    {
      path: '/admin/comments',
      name: 'admin-comments',
      component: () => import('@/views/admin/CommentsPage.vue')
    },
    {
      path: '/admin/about',
      name: 'admin-about',
      component: () => import('@/views/admin/AboutPage.vue')
    },

    // ==================== 404 ====================
    {
      path: '/:pathMatch(.*)*',
      name: 'not-found',
      component: () => import('@/views/NotFoundPage.vue')
    }
  ]
})

export default router

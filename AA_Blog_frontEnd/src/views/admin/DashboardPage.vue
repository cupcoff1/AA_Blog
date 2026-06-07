<script setup lang="ts">
import { ref, onMounted } from 'vue'
import api from '@/api/client'
import type { DashboardVO } from '@/models/types'

const data = ref<DashboardVO | null>(null)
const loading = ref(true)
const error = ref(false)

onMounted(async () => {
  try {
    data.value = await api.get('/admin/dashboard')
  } catch {
    error.value = true
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <div class="dashboard">
    <h1>仪表盘</h1>

    <div v-if="loading" class="state">加载中...</div>
    <div v-else-if="error" class="state error">加载失败</div>

    <div v-else-if="data" class="dashboard-content">
      <div class="stats">
        <div class="stat-item">文章 <strong>{{ data.blogCount }}</strong></div>
        <div class="stat-item">笔记 <strong>{{ data.noteCount }}</strong></div>
        <div class="stat-item">项目 <strong>{{ data.projectCount }}</strong></div>
        <div class="stat-item">评论 <strong>{{ data.commentCount }}</strong></div>
      </div>

      <h2>标签统计</h2>
      <table v-if="data.tags.length" class="tag-table">
        <thead>
          <tr><th>标签</th><th>文章</th><th>笔记</th><th>项目</th></tr>
        </thead>
        <tbody>
          <tr v-for="tag in data.tags" :key="tag.slug">
            <td>{{ tag.name }}</td>
            <td>{{ tag.blogCount }}</td>
            <td>{{ tag.noteCount }}</td>
            <td>{{ tag.projectCount }}</td>
          </tr>
        </tbody>
      </table>
      <p v-else class="empty">暂无标签</p>

      <h2>最近评论</h2>
      <div v-if="data.recentComments.length" class="comment-list">
        <div v-for="c in data.recentComments" :key="c.id" class="comment-item">
          <strong>{{ c.author_name }}</strong>
          <span class="comment-blog">《{{ c.blog_title }}》</span>
          <span class="comment-date">{{ c.created_at }}</span>
          <p>{{ c.content }}</p>
        </div>
      </div>
      <p v-else class="empty">暂无评论</p>
    </div>
  </div>
</template>

<style scoped>
.dashboard { padding: 1rem 0; }

.state { text-align: center; color: var(--text-secondary); padding: 3rem 0; }
.state.error { color: #e53e3e; }

.stats { display: flex; gap: 1.5rem; margin: 1.5rem 0; flex-wrap: wrap; }
.stat-item {
  background: var(--bg-secondary);
  padding: 1rem 1.5rem;
  border-radius: 8px;
  font-size: 0.95em;
}
.stat-item strong { display: block; font-size: 1.5em; }

.tag-table { width: 100%; border-collapse: collapse; margin: 1rem 0 2rem; }
.tag-table th, .tag-table td { padding: 0.5rem 0.8rem; border-bottom: 1px solid var(--border); text-align: left; }
.tag-table th { font-size: 0.85em; color: var(--text-secondary); }

.comment-item { padding: 0.8rem 0; border-bottom: 1px solid var(--border); }
.comment-item p { margin-top: 0.3rem; color: var(--text-secondary); font-size: 0.9em; }
.comment-meta { font-size: 0.85em; color: var(--text-secondary); }
.comment-blog { margin-left: 0.5rem; }
.comment-date { margin-left: 0.5rem; color: var(--text-secondary); font-size: 0.85em; }
.empty { color: var(--text-secondary); font-size: 0.95em; }
</style>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { BookOpen, Search, Pencil, Trash2 } from '@lucide/vue'
import { isAdmin } from '@/router/auth'
import { listBlogs, deleteBlog } from '@/api/blog'
import type { BlogListVO } from '@/models/types'

const route = useRoute()
const router = useRouter()
const blogs = ref<BlogListVO[]>([])
const loading = ref(true)
const error = ref(false)

const handleDelete = async (id: number, title: string) => {
  if (!confirm(`删除「${title}」？`)) return
  try {
    await deleteBlog(id)
    blogs.value = blogs.value.filter(b => b.id !== id)
  } catch {
    alert('删除失败，请重试')
  }
}
const keyword = ref(String(route.query.q || ''))

const fetchBlogs = async () => {
  loading.value = true
  error.value = false
  try {
    const q = String(route.query.q || '')
    const tag = String(route.query.tag || '')
    blogs.value = await listBlogs({ q, tag })
  } catch {
    error.value = true
  } finally {
    loading.value = false
  }
}

const search = () => {
  const query: Record<string, string> = {}
  if (keyword.value) query.q = keyword.value
  const tag = String(route.query.tag || '')
  if (tag) query.tag = tag
  router.replace({ query })
}

fetchBlogs()
watch(() => route.query, fetchBlogs)
watch(() => route.query.q, (val) => { keyword.value = String(val || '') })
</script>

<template>
  <div class="blog-page">
    <div class="section-head">
      <h1 class="section-title"><BookOpen :size="32" /> Blog</h1>
      <p class="section-desc">指南、参考与教程</p>
    </div>

    <div class="search-bar">
      <Search :size="16" class="search-icon" />
      <input v-model="keyword" @keyup.enter="search" placeholder="搜索文章..." />
    </div>

    <div v-if="loading" class="state">加载中...</div>
    <div v-else-if="error" class="state error">加载失败</div>
    <div v-else-if="!blogs.length" class="state">暂无文章</div>

    <div v-else class="post-list">
      <div v-for="blog in blogs" :key="blog.id" class="post-row">
        <RouterLink :to="`/blog/${blog.slug}`" class="post-item">
          <time>{{ blog.publishedAt?.split('T')[0] }}</time>
          <div class="post-title">{{ blog.title }}</div>
        </RouterLink>
        <div v-if="isAdmin" class="post-actions">
          <RouterLink :to="`/blog/${blog.id}/edit`" class="icon-btn" title="编辑">
            <Pencil :size="14" />
          </RouterLink>
          <button @click="handleDelete(blog.id, blog.title)" class="icon-btn icon-del" title="删除">
            <Trash2 :size="14" />
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.blog-page { padding: 2.5rem 0; }
.section-head { margin-bottom: 1.5rem; }
.section-title { font-size: 2.8rem; margin-bottom: 0.2em; display: flex; align-items: center; gap: 10px; }
.section-desc { color: var(--text-secondary); font-size: 0.95em; margin: 0; }
.search-bar { display: flex; align-items: center; gap: 8px; margin-bottom: 2rem; border: 1px solid var(--border); border-radius: var(--radius); background: rgba(0,0,0,0.06); padding: 0 10px; max-width: 240px; }
.search-icon { color: var(--text-secondary); flex-shrink: 0; }
.search-bar input {
  width: 100%; padding: 8px 0; border: none; border-radius: 0;
  font-size: 0.9em; background: transparent; color: var(--text); outline: none;
}
.search-bar:focus-within { border-color: var(--link); background: var(--bg); }
.state { text-align: center; color: var(--text-secondary); padding: 4rem 0; }
.state.error { color: #e53e3e; }
.post-list { display: flex; flex-direction: column; }
.post-item {
  display: flex; flex-direction: column; gap: 0.2rem;
  padding: 6px 0; text-decoration: none; width: fit-content;
  margin-bottom: 0.5rem; border-radius: var(--radius);
}
@media screen and (min-width: 900px) {
  .post-item { flex-direction: row; align-items: baseline; gap: 1.5rem; }
}
.post-item time {
  color: var(--text-secondary); font-size: 0.8em; font-weight: 400;
  min-width: 100px; text-decoration: none;
}
.post-title {
  font-weight: 600; font-size: 1.05em;
  text-decoration: underline; text-decoration-thickness: 2px;
  text-decoration-style: dotted; text-decoration-color: var(--text); text-underline-offset: 6px;
}
.post-item:hover .post-title { text-decoration-color: var(--link); color: var(--link); }
.post-row { display: flex; align-items: center; gap: 0.5rem; }
.post-row .post-item { flex: 1; }
.post-actions { display: flex; gap: 4px; flex-shrink: 0; }
.icon-btn { display: flex; align-items: center; justify-content: center; width: 28px; height: 28px; border-radius: 50%; color: var(--text-secondary); background: none; border: none; cursor: pointer; }
.icon-btn:hover { background: rgba(147, 197, 253, 0.25); color: rgba(147, 197, 253, 1); }
.icon-del:hover { background: rgba(229, 62, 62, 0.15); color: #e53e3e; }
</style>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import api from '@/api/client'
import type { BlogListVO } from '@/models/types'

const route = useRoute()
const router = useRouter()
const blogs = ref<BlogListVO[]>([])
const loading = ref(true)
const error = ref(false)
const keyword = ref((route.query.q as string) || '')

const fetchBlogs = async () => {
  loading.value = true
  error.value = false
  try {
    const q = (route.query.q as string) || ''
    const tag = (route.query.tag as string) || ''
    blogs.value = await api.get('/blog', { params: { q, tag } })
  } catch {
    error.value = true
  } finally {
    loading.value = false
  }
}

const search = () => {
  const query: Record<string, string> = {}
  if (keyword.value) query.q = keyword.value
  const tag = route.query.tag as string
  if (tag) query.tag = tag
  router.replace({ query })
}

fetchBlogs()
watch(() => route.query, fetchBlogs)
// 浏览器前进后退时同步搜索框
watch(() => route.query.q, (val) => {
  keyword.value = (val as string) || ''
})
</script>

<template>
  <div class="blog-page">
    <h1 class="page-title">Blog</h1>
    <div class="search-bar">
      <input v-model="keyword" @keyup.enter="search" placeholder="搜索文章..." />
    </div>

    <div v-if="loading" class="state">加载中...</div>
    <div v-else-if="!blogs.length" class="state">暂无文章</div>

    <div v-else class="post-list">
      <article v-for="blog in blogs" :key="blog.id" class="post-item">
        <time>{{ blog.publishedAt?.split('T')[0] }}</time>
        <RouterLink :to="`/blog/${blog.slug}`" class="post-title">{{ blog.title }}</RouterLink>
        <div class="post-meta">
          <span class="post-summary">{{ blog.summary }}</span>
        </div>
        <div class="tags" v-if="blog.tags?.length">
          <RouterLink v-for="tag in blog.tags" :key="tag.id"
            :to="`/blog?tag=${tag.slug}`" class="tag">{{ tag.name }}</RouterLink>
        </div>
      </article>
    </div>
  </div>
</template>

<style scoped>
.blog-page { padding: 2.5rem 0; }
.page-title { font-size: 1.8em; margin-bottom: 1rem; }
.search-bar { margin-bottom: 1.5rem; }
.search-bar input {
  width: 100%; padding: 0.65rem 1rem; border: 1px solid var(--border);
  border-radius: var(--radius); font-size: 1em; background: var(--bg-secondary);
  color: var(--text); outline: none; transition: border-color 0.2s;
}
.search-bar input:focus { border-color: var(--link); background: var(--bg); }
.state { text-align: center; color: var(--text-secondary); padding: 4rem 0; }
.post-list { display: flex; flex-direction: column; gap: 1.8rem; }
.post-item { display: flex; flex-direction: column; gap: 0.2rem; }
.post-item time { color: var(--text-secondary); font-size: 0.8em; text-transform: uppercase; letter-spacing: 0.05em; }
.post-title { font-family: var(--heading); font-size: 1.15em; font-weight: 600; }
.post-meta { color: var(--text-secondary); font-size: 0.88em; margin-top: 0.1em; }
.post-summary { flex: 1; }
.tags { display: flex; gap: 0.4rem; margin-top: 0.4rem; flex-wrap: wrap; }
</style>

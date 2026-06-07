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
    <div class="search-bar">
      <input v-model="keyword" @keyup.enter="search" placeholder="搜索文章..." />
    </div>

    <div v-if="loading" class="state">加载中...</div>
    <div v-else-if="!blogs.length" class="state">暂无文章</div>

    <div v-else class="post-list">
      <article v-for="blog in blogs" :key="blog.id" class="post-item">
        <RouterLink :to="`/blog/${blog.slug}`" class="post-title">{{ blog.title }}</RouterLink>
        <div class="post-meta">
          <time>{{ blog.publishedAt?.split('T')[0] }}</time>
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
.blog-page {
  padding: 2rem 0;
}

.search-bar {
  margin-bottom: 2rem;
}

.search-bar input {
  width: 100%;
  padding: 0.6rem 1rem;
  border: 1px solid var(--border);
  border-radius: 6px;
  font-size: 1em;
  background: var(--bg);
  color: var(--text);
  outline: none;
}

.search-bar input:focus {
  border-color: var(--link);
}

.state {
  text-align: center;
  color: var(--text-secondary);
  padding: 3rem 0;
}

.post-list {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.post-item {
  padding-bottom: 1.2rem;
  border-bottom: 1px solid var(--border);
}

.post-title {
  font-family: var(--serif);
  font-size: 1.25em;
  font-weight: 600;
}

.post-meta {
  display: flex;
  gap: 1rem;
  margin-top: 0.3rem;
  color: var(--text-secondary);
  font-size: 0.9em;
}

.post-summary {
  flex: 1;
}

.tags {
  display: flex;
  gap: 0.5rem;
  margin-top: 0.5rem;
  flex-wrap: wrap;
}

.tag {
  font-size: 0.8em;
  color: var(--text-secondary);
  background: var(--bg-secondary);
  padding: 2px 8px;
  border-radius: 4px;
}
</style>

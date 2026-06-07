<script setup lang="ts">
import { ref, onMounted } from 'vue'
import api from '@/api/client'
import type { HomeVO } from '@/models/types'

const data = ref<HomeVO | null>(null)
const error = ref(false)

onMounted(async () => {
  try {
    data.value = await api.get('/home')
  } catch {
    error.value = true
  }
})
</script>

<template>
  <div v-if="data" class="home">
    <!-- 个人简介 -->
    <section class="intro">
      <div class="avatar" v-if="data.about.avatar">
        <img :src="data.about.avatar" :alt="data.about.nickname" />
      </div>
      <h1>{{ data.about.nickname }}</h1>
      <p class="bio">{{ data.about.bio }}</p>
    </section>

    <!-- 最新文章 -->
    <section class="section">
      <h2>
        <RouterLink to="/blog">Blog</RouterLink>
      </h2>
      <div v-if="data.latestBlogs.length" class="post-list">
        <article v-for="blog in data.latestBlogs" :key="blog.id" class="post-item">
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
      <p v-else class="empty">暂无文章</p>
    </section>

    <!-- 最新笔记 -->
    <section class="section">
      <h2>
        <RouterLink to="/notes">Notes</RouterLink>
      </h2>
      <div v-if="data.latestNotes.length" class="post-list">
        <article v-for="note in data.latestNotes" :key="note.id" class="post-item">
          <span class="post-title no-link">{{ note.title }}</span>
          <div class="post-meta">
            <time>{{ note.publishedAt?.split('T')[0] }}</time>
            <span class="tags" v-if="note.tags?.length">
              <RouterLink v-for="tag in note.tags" :key="tag.id"
                :to="`/notes?tag=${tag.slug}`" class="tag">{{ tag.name }}</RouterLink>
            </span>
          </div>
        </article>
      </div>
      <p v-else class="empty">暂无笔记</p>
    </section>

    <!-- 最新项目 -->
    <section class="section">
      <h2>
        <RouterLink to="/projects">Projects</RouterLink>
      </h2>
      <div v-if="data.latestProjects.length" class="project-list">
        <div v-for="proj in data.latestProjects" :key="proj.id" class="project-item">
          <span class="project-name">{{ proj.name }}</span>
          <span class="project-desc">{{ proj.description }}</span>
          <div class="project-links">
            <a v-if="proj.demoUrl" :href="proj.demoUrl" target="_blank">Demo</a>
            <a v-if="proj.githubUrl" :href="proj.githubUrl" target="_blank">GitHub</a>
          </div>
          <div class="tags" v-if="proj.tags?.length">
            <span v-for="tag in proj.tags" :key="tag.id" class="tag">{{ tag.name }}</span>
          </div>
        </div>
      </div>
      <p v-else class="empty">暂无项目</p>
    </section>
  </div>
  <p v-else-if="error" class="error">加载失败，请检查后端服务</p>
  <p v-else class="loading">加载中...</p>
</template>

<style scoped>
.home { padding: 4rem 0; }
.intro { text-align: center; margin-bottom: 4rem; }
.avatar { width: 90px; height: 90px; border-radius: 50%; overflow: hidden; margin: 0 auto 1.2rem; }
.avatar img { width: 100%; height: 100%; object-fit: cover; }
.bio { color: var(--text-secondary); max-width: 480px; margin: 0.5rem auto 0; font-size: 0.95em; }
.section { margin-bottom: 3rem; }
.section h2 { font-size: 1.4em; margin-bottom: 1rem; border-bottom: 1px solid var(--border); padding-bottom: 0.3em; }
.section h2 a { color: var(--text); }
.post-list { display: flex; flex-direction: column; gap: 1.4rem; }
.post-item { padding-bottom: 1.4rem; border-bottom: 1px solid var(--border); }
.post-title { font-family: var(--serif); font-weight: 600; font-size: 1.15em; }
.no-link { color: var(--text); }
.post-meta { display: flex; gap: 1rem; margin-top: 0.25em; color: var(--text-secondary); font-size: 0.88em; }
.post-summary { flex: 1; }
.tags { display: flex; gap: 0.4rem; margin-top: 0.4rem; flex-wrap: wrap; }
.tag { font-size: 0.78em; color: var(--text-secondary); background: var(--bg-secondary); padding: 2px 8px; border-radius: 4px; letter-spacing: 0.02em; }
.project-list { display: grid; grid-template-columns: repeat(auto-fill, minmax(280px, 1fr)); gap: 1.2rem; }
.project-item { padding: 1.2rem; background: var(--bg-card); border: 1px solid var(--border); border-radius: var(--radius); }
.project-name { font-weight: 600; display: block; font-family: var(--serif); }
.project-desc { color: var(--text-secondary); font-size: 0.88em; margin-top: 0.25em; display: block; }
.project-links { margin-top: 0.6rem; display: flex; gap: 1rem; font-size: 0.9em; }
.error, .empty, .loading { text-align: center; color: var(--text-secondary); padding: 4rem 0; }
.error { color: #e53e3e; }
</style>

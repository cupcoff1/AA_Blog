<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { BookOpen, Pencil, FolderOpen } from '@lucide/vue'
import api from '@/api/client'
import type { HomeVO, HeroQuoteVO } from '@/models/types'

const quotes = ref<HeroQuoteVO[]>([{ id: 0, content: '真正重要的东西，眼睛是看不见的', author: '', source: '' }])
const currentQuote = ref(0)
const quoteVisible = ref(true)
let timer: ReturnType<typeof setInterval>

const data = ref<HomeVO | null>(null)
const error = ref(false)
const heroLight = ref('/hero-light.png')
const heroDark = ref('/hero.jpg')

onMounted(async () => {
  try { data.value = await api.get('/home') }
  catch { error.value = true }

  try {
    const qs: HeroQuoteVO[] = await api.get('/hero-quotes')
    if (qs && qs.length) quotes.value = qs
  } catch {}

  try {
    const cfg = await api.get('/hero-config')
    if (cfg) { heroLight.value = cfg.heroLight; heroDark.value = cfg.heroDark }
  } catch {}

  timer = setInterval(() => {
    quoteVisible.value = false
    setTimeout(() => {
      currentQuote.value = (currentQuote.value + 1) % quotes.value.length
      quoteVisible.value = true
    }, 400)
  }, 5000)
})
onUnmounted(() => clearInterval(timer))
</script>

<template>
  <div v-if="data" class="home">
    <!-- Hero -->
    <section class="hero">
      <div class="hero-text">
        <h1>Hey, I'm AA_!</h1>
        <p class="hero-desc" :class="{ quote: true, visible: quoteVisible }">
          {{ quotes[currentQuote].content }}
          <span v-if="quotes[currentQuote].author" class="quote-attribution">—— {{ quotes[currentQuote].author }}《{{ quotes[currentQuote].source }}》</span>
        </p>

      </div>
      <div class="hero-image">
        <img :src="heroLight" alt="Hero" class="hero-light" />
        <img :src="heroDark" alt="Hero" class="hero-dark" />
      </div>
    </section>

    <!-- Blog -->
    <section class="section">
      <div class="section-head">
        <h2 class="section-title"><BookOpen :size="22" /> Blog</h2>
        <p class="section-desc">指南、参考与教程</p>
      </div>
      <div v-if="data.latestBlogs.length" class="post-list">
        <article v-for="blog in data.latestBlogs" :key="blog.id" class="post-item">
          <time>{{ blog.publishedAt?.split('T')[0] }}</time>
          <div>
            <RouterLink :to="`/blog/${blog.slug}`" class="post-title">{{ blog.title }}</RouterLink>
          </div>
        </article>
      </div>
      <p v-else class="empty">暂无文章</p>
    </section>

    <!-- Notes -->
    <section class="section">
      <div class="section-head">
        <h2 class="section-title"><Pencil :size="22" /> Notes</h2>
        <p class="section-desc">生活、项目以及一切</p>
      </div>
      <div v-if="data.latestNotes.length" class="post-list">
        <article v-for="note in data.latestNotes" :key="note.id" class="post-item">
          <time>{{ note.publishedAt?.split('T')[0] }}</time>
          <span class="post-title no-link">{{ note.title }}</span>
        </article>
      </div>
      <p v-else class="empty">暂无笔记</p>
    </section>

    <!-- Projects -->
    <section class="section">
      <div class="section-head">
        <h2 class="section-title"><FolderOpen :size="22" /> Projects</h2>
        <p class="section-desc">我做过的开源项目</p>
      </div>
      <div v-if="data.latestProjects.length" class="project-list">
        <div v-for="proj in data.latestProjects" :key="proj.id" class="project-card">
          <a v-if="proj.githubUrl" :href="proj.githubUrl" target="_blank" class="project-name">{{ proj.name }}</a>
          <span v-else class="project-name">{{ proj.name }}</span>
          <p>{{ proj.description }}</p>
        </div>
      </div>
      <p v-else class="empty">暂无项目</p>
    </section>
  </div>
  <p v-else-if="error" class="state error">加载失败，请检查后端服务</p>
  <p v-else class="state">加载中...</p>
</template>

<style scoped>
.home { padding: 1.5rem 0 3rem; }

/* Hero */
.hero { display: flex; gap: 2rem; align-items: center; margin-bottom: 4rem; }
.hero-text { flex: 1; }
.hero h1 { font-size: 3em; margin-bottom: 0.2em; }
.hero-desc { font-family: 'Space Mono', monospace; font-size: 1.2em; max-width: 560px; margin-top: 2em; margin-bottom: 1em; min-height: 3.6em; color: rgba(34, 139, 34, 0.8); }
body.dark .hero-desc { color: rgba(147, 197, 253, 0.9); }
.quote-attribution { display: block; font-size: 0.75em; margin-top: 0.4em; opacity: 0.7; }
.quote { opacity: 1; transform: translateY(0); transition: opacity 0.4s ease, transform 0.4s ease; }
.quote:not(.visible) { opacity: 0; transform: translateY(8px); }
.hero-image { width: 260px; height: 260px; border-radius: 50%; overflow: hidden; flex-shrink: 0; margin-top: 1.5rem; position: relative; }
.hero-image img { width: 200%; height: 100%; object-fit: cover; object-position: left; position: absolute; top: 0; left: 0; }
.hero-light { display: block; }
.hero-dark { display: none; }
body.dark .hero-light { display: none; }
body.dark .hero-dark { display: block; }
/* Sections */
.section { margin-bottom: 3.5rem; }
.section-head { margin-bottom: 1.2rem; }
.section-title { font-size: 1.8em; margin: 0 0 0.2em; display: flex; align-items: center; gap: 8px; }
.section-title a { color: var(--text); }
.section-desc { color: var(--text-secondary); font-size: 0.95em; margin: 0; }

/* Post list */
.post-list { display: flex; flex-direction: column; gap: 1.5rem; }
.post-item { display: flex; flex-direction: column; gap: 0.2rem; }
.post-item time { color: var(--text-secondary); font-size: 0.8em; text-transform: uppercase; letter-spacing: 0.05em; }
.post-title { font-weight: 600; font-size: 1.1em; }
a.post-title { text-decoration: underline; text-decoration-thickness: 2px; text-decoration-style: dotted; text-decoration-color: var(--text); text-underline-offset: 6px; }
a.post-title:hover { text-decoration-color: var(--link); }
.no-link { color: var(--link); }
/* Projects */
.project-list { display: grid; grid-template-columns: repeat(auto-fill, minmax(240px, 1fr)); gap: 0.8rem; }
.project-card { padding: 0.8rem 1rem; background: var(--bg-card); backdrop-filter: blur(10px); border: 2px solid var(--border); border-radius: var(--radius); box-shadow: 0 2px 8px rgba(0,0,0,0.12); }
.project-name { font-weight: 600; display: block; margin-bottom: 0.2em; }
.project-name:hover { text-decoration: underline; text-decoration-thickness: 2px; }
.project-card p { color: var(--text-secondary); font-size: 0.9em; margin-bottom: 0.4em; }
.state { text-align: center; color: var(--text-secondary); padding: 4rem 0; }
.error { color: #e53e3e; }
.empty { color: var(--text-secondary); font-size: 0.95em; }
</style>

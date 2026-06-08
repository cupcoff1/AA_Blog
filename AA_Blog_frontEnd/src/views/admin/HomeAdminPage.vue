<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { Plus, X } from '@lucide/vue'
import api from '@/api/client'
import type { HeroQuoteVO, HeroConfigVO } from '@/models/types'

const quotes = ref<HeroQuoteVO[]>([])
const loading = ref(true)
const error = ref(false)
const newContent = ref('')
const newAuthor = ref('')
const newSource = ref('')

const heroConfig = ref<HeroConfigVO>({ heroLight: '/hero-light.png', heroDark: '/hero.jpg' })

const fetchQuotes = async () => {
  loading.value = true; error.value = false
  try { quotes.value = await api.get('/hero-quotes') || [] }
  catch { error.value = true }
  finally { loading.value = false }
}

const addQuote = async () => {
  if (!newContent.value.trim()) return
  await api.post('/admin/hero-quotes', {
    content: newContent.value.trim(),
    author: newAuthor.value.trim(),
    source: newSource.value.trim()
  })
  newContent.value = ''
  newAuthor.value = ''
  newSource.value = ''
  fetchQuotes()
}

const delQuote = async (id: number) => {
  if (!confirm('删除这条引语？')) return
  await api.delete(`/admin/hero-quotes/${id}`)
  quotes.value = quotes.value.filter(q => q.id !== id)
}

const uploadHero = async (type: 'light' | 'dark', e: Event) => {
  const input = e.target as HTMLInputElement
  if (!input.files?.length) return
  const form = new FormData()
  form.append('file', input.files[0])
  const { url } = await api.post('/admin/hero-image?type=' + type, form)
  heroConfig.value[type === 'light' ? 'heroLight' : 'heroDark'] = url + '?t=' + Date.now()
}

const fetchHeroConfig = async () => {
  try { heroConfig.value = await api.get('/hero-config') }
  catch { /* use defaults */ }
}

onMounted(() => {
  fetchQuotes()
  fetchHeroConfig()
})
</script>

<template>
  <div class="admin-list">
    <h1>首页管理</h1>

    <!-- 引语管理 -->
    <section class="section">
      <h2>引语管理</h2>
      <div v-if="loading" class="state">加载中...</div>
      <div v-else-if="error" class="state error">加载失败 <button @click="fetchQuotes">重试</button></div>
      <ul v-else class="quote-list">
        <li v-for="q in quotes" :key="q.id">
          <div>
            <div class="quote-text">{{ q.content }}</div>
            <div class="quote-meta" v-if="q.author || q.source">{{ q.author }}<span v-if="q.author && q.source"> · </span>{{ q.source }}</div>
          </div>
          <button class="del" @click="delQuote(q.id)"><X :size="14" /></button>
        </li>
        <li v-if="!quotes.length" class="state">暂无引语</li>
      </ul>
      <div class="add-form">
        <input v-model="newContent" placeholder="引语内容..." @keyup.enter="addQuote" />
        <div class="add-meta">
          <input v-model="newAuthor" placeholder="作者" />
          <input v-model="newSource" placeholder="作品名" />
        </div>
        <button class="btn" @click="addQuote"><Plus :size="16" /> 添加</button>
      </div>
    </section>

    <!-- Hero 图管理 -->
    <section class="section">
      <h2>Hero 图片</h2>
      <div class="hero-grid">
        <div class="hero-card">
          <label>亮色主题</label>
          <img :src="heroConfig.heroLight" alt="亮色 Hero" />
          <input type="file" accept="image/*" @change="uploadHero('light', $event)" />
        </div>
        <div class="hero-card">
          <label>暗色主题</label>
          <img :src="heroConfig.heroDark" alt="暗色 Hero" />
          <input type="file" accept="image/*" @change="uploadHero('dark', $event)" />
        </div>
      </div>
    </section>
  </div>
</template>

<style scoped>
.admin-list { padding: 1rem 0; max-width: 700px; }
.section { margin-top: 2rem; }
h1 { margin-bottom: 1rem; }
h2 { font-size: 1.2em; margin-bottom: 0.8rem; }
.state { color: var(--text-secondary); padding: 1rem 0; }
.state.error { color: #e53e3e; }

.quote-list { list-style: none; padding: 0; margin: 0 0 1rem; }
.quote-list li { display: flex; align-items: flex-start; justify-content: space-between; gap: 0.5rem; padding: 0.6rem 0; border-bottom: 1px solid var(--border); }
.quote-text { line-height: 1.4; }
.quote-meta { font-size: 0.85em; color: var(--text-secondary); margin-top: 2px; }
.del { display: flex; align-items: center; border: none; background: none; color: var(--text-secondary); cursor: pointer; padding: 4px; border-radius: 4px; flex-shrink: 0; }
.del:hover { color: #e53e3e; background: rgba(229,62,62,0.1); }

.add-form { display: flex; flex-direction: column; gap: 0.4rem; }
.add-form input { border: 1px solid var(--border); border-radius: 6px; padding: 0.4rem 0.6rem; background: var(--bg); color: var(--text); font-size: 0.95em; }
.add-meta { display: flex; gap: 0.5rem; }
.add-meta input { flex: 1; }
.btn { display: flex; align-items: center; gap: 4px; border: 1px solid var(--border); border-radius: 6px; padding: 0.4rem 0.8rem; background: var(--bg-secondary); color: var(--text); cursor: pointer; font-size: 0.95em; }
.btn:hover { border-color: var(--link); color: var(--link); }

.hero-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 1rem; }
.hero-card { display: flex; flex-direction: column; gap: 0.5rem; }
.hero-card label { font-weight: 600; font-size: 0.95em; }
.hero-card img { width: 100%; aspect-ratio: 1; object-fit: cover; border-radius: 50%; border: 1px solid var(--border); }
.hero-card input[type="file"] { font-size: 0.9em; }
</style>

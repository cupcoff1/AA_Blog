<script setup lang="ts">
import { ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import api from '@/api/client'
import type { NotesVO } from '@/models/types'
import { marked } from 'marked'

const route = useRoute()
const router = useRouter()
const notes = ref<NotesVO[]>([])
const loading = ref(true)
const error = ref(false)
const keyword = ref((route.query.q as string) || '')

const fetchNotes = async () => {
  loading.value = true
  error.value = false
  try {
    const q = (route.query.q as string) || ''
    const tag = (route.query.tag as string) || ''
    notes.value = await api.get('/notes', { params: { q, tag } })
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

fetchNotes()
watch(() => route.query, fetchNotes)
watch(() => route.query.q, (val) => { keyword.value = (val as string) || '' })
</script>

<template>
  <div class="notes-page">
    <div class="search-bar">
      <input v-model="keyword" @keyup.enter="search" placeholder="搜索笔记..." />
    </div>

    <div v-if="loading" class="state">加载中...</div>
    <div v-else-if="error" class="state error">加载失败</div>
    <div v-else-if="!notes.length" class="state">暂无笔记</div>

    <div v-else class="note-list">
      <article v-for="note in notes" :key="note.id" class="note-item">
        <div class="note-header">
          <h2>{{ note.title }}</h2>
          <time>{{ note.publishedAt?.split('T')[0] }}</time>
        </div>
        <div class="tags" v-if="note.tags?.length">
          <RouterLink v-for="tag in note.tags" :key="tag.id"
            :to="`/notes?tag=${tag.slug}`" class="tag">{{ tag.name }}</RouterLink>
        </div>
        <div class="note-body" v-html="marked(note.content)" />
      </article>
    </div>
  </div>
</template>

<style scoped>
.notes-page {
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

.state.error {
  color: #e53e3e;
}

.note-list {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.note-item {
  padding-bottom: 2rem;
  border-bottom: 1px solid var(--border);
}

.note-header {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  margin-bottom: 0.5rem;
}

.note-header h2 {
  margin: 0;
  font-size: 1.25em;
}

.note-header time {
  color: var(--text-secondary);
  font-size: 0.9em;
}

.tags {
  display: flex;
  gap: 0.5rem;
  margin-bottom: 0.8rem;
  flex-wrap: wrap;
}

.tag {
  font-size: 0.8em;
  color: var(--text-secondary);
  background: var(--bg-secondary);
  padding: 2px 8px;
  border-radius: 4px;
}

.note-body {
  line-height: 1.9;
}
</style>

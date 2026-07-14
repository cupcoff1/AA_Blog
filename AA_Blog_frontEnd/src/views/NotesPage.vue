<script setup lang="ts">
import { ref, watch, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Pencil, Search, Trash2 } from '@lucide/vue'
import { listNotes, deleteNote as deleteNoteApi } from '@/api/note'
import { isAdmin } from '@/router/auth'
import type { NoteVO } from '@/models/types'
import { marked } from 'marked'
import DOMPurify from 'dompurify'

const route = useRoute()
const router = useRouter()
const notes = ref<NoteVO[]>([])
const loading = ref(true)
const error = ref(false)
const keyword = ref(String(route.query.q || ''))

const renderedContents = computed(() => new Map(
  notes.value.map(n => [n.id, DOMPurify.sanitize(marked(n.content || '') as string)] as const)
))

const handleDelete = async (id: number, title: string) => {
  if (!confirm(`删除「${title}」？`)) return
  try {
    await deleteNoteApi(id)
    notes.value = notes.value.filter(n => n.id !== id)
  } catch {
    alert('删除失败，请重试')
  }
}

const fetchNotes = async () => {
  loading.value = true
  error.value = false
  try {
    const q = String(route.query.q || '')
    const tag = String(route.query.tag || '')
    notes.value = await listNotes({ q, tag })
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

fetchNotes()
watch(() => route.query, fetchNotes)
watch(() => route.query.q, (val) => { keyword.value = String(val || '') })
</script>

<template>
  <div class="notes-page">
    <div class="section-head">
      <h1 class="section-title"><Pencil :size="32" /> Notes</h1>
      <p class="section-desc">生活、项目以及一切</p>
    </div>

    <div class="search-bar">
      <Search :size="16" class="search-icon" />
      <input v-model="keyword" @keyup.enter="search" placeholder="搜索笔记..." />
    </div>

    <div v-if="loading" class="state">加载中...</div>
    <div v-else-if="error" class="state error">加载失败</div>
    <div v-else-if="!notes.length" class="state">暂无笔记</div>

    <div v-else class="note-list">
      <article v-for="note in notes" :key="note.id" class="note-item">
        <div class="note-header">
          <time>{{ note.publishedAt?.split('T')[0] }}</time>
          <h2>{{ note.title }}</h2>
        </div>
        <div class="note-body" v-html="renderedContents.get(note.id)" />
        <div v-if="isAdmin" class="note-actions">
          <RouterLink :to="`/notes/${note.id}/edit`" class="icon-btn" title="编辑">
            <Pencil :size="14" />
          </RouterLink>
          <button @click="handleDelete(note.id, note.title)" class="icon-btn icon-del" title="删除">
            <Trash2 :size="14" />
          </button>
        </div>
      </article>
    </div>
  </div>
</template>

<style scoped>
.notes-page { padding: 2.5rem 0; }
.section-head { margin-bottom: 1.5rem; }
.section-title { font-size: 2.8rem; margin-bottom: 0.2em; display: flex; align-items: center; gap: 10px; }
.section-desc { color: var(--text-secondary); font-size: 0.95em; margin: 0; }
.search-bar { display: flex; align-items: center; gap: 8px; margin-bottom: 2rem; border: 1px solid var(--border); border-radius: var(--radius); background: rgba(0,0,0,0.06); padding: 0 10px; max-width: 240px; }
.search-icon { color: var(--text-secondary); flex-shrink: 0; }
.search-bar input { width: 100%; padding: 8px 0; border: none; font-size: 0.9em; background: transparent; color: var(--text); outline: none; }
.search-bar:focus-within { border-color: var(--link); }
.state { text-align: center; color: var(--text-secondary); padding: 4rem 0; }
.state.error { color: #e53e3e; }
.note-list { display: flex; flex-direction: column; gap: 1rem; }
.note-item { padding-bottom: 0; }
.note-header { display: flex; align-items: baseline; gap: 1rem; }
.note-item time { color: var(--text-secondary); font-size: 0.8em; min-width: 80px; }
.note-item h2 { font-size: 1.15em; margin: 0; color: var(--link); }
.note-body { line-height: 1.9; font-size: 1.02em; margin-top: 0.8em; background: rgba(0,0,0,0.08); border: 1px solid var(--border); border-radius: var(--radius); padding: 1rem 1.2rem; box-shadow: 0 2px 6px rgba(0,0,0,0.08); }
.note-body :deep(a) { text-decoration: underline; text-decoration-thickness: 2px; text-decoration-style: dotted; text-underline-offset: 6px; }
.note-actions { display: flex; gap: 4px; margin-top: 0.5rem; justify-content: flex-end; }
.icon-btn { display: flex; align-items: center; justify-content: center; width: 28px; height: 28px; border-radius: 50%; color: var(--text-secondary); background: none; border: none; cursor: pointer; }
.icon-btn:hover { background: rgba(147, 197, 253, 0.25); color: rgba(147, 197, 253, 1); }
.icon-del:hover { background: rgba(229, 62, 62, 0.15); color: #e53e3e; }
</style>

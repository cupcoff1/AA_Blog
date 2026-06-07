<script setup lang="ts">
import { ref, onMounted } from 'vue'
import api from '@/api/client'
import type { NotesVO } from '@/models/types'

const notes = ref<NotesVO[]>([])
const loading = ref(true); const error = ref(false)
const fetchList = async () => {
  loading.value = true; error.value = false
  try { notes.value = await api.get('/admin/notes') }
  catch { error.value = true }
  finally { loading.value = false }
}
const del = async (id: number, title: string) => {
  if (!confirm(`确定删除「${title}」？`)) return
  await api.delete(`/admin/notes/${id}`)
  notes.value = notes.value.filter(n => n.id !== id)
}
onMounted(fetchList)
</script>
<template>
  <div class="admin-list">
    <div class="toolbar"><h1>笔记管理</h1><RouterLink to="/admin/notes/new" class="btn">新建笔记</RouterLink></div>
    <div v-if="loading" class="state">加载中...</div>
    <div v-else-if="error" class="state error">加载失败 <button @click="fetchList">重试</button></div>
    <div v-else-if="!notes.length" class="state">暂无笔记</div>
    <table v-else class="data-table">
      <thead><tr><th>标题</th><th>日期</th><th>操作</th></tr></thead>
      <tbody><tr v-for="n in notes" :key="n.id">
        <td>{{ n.title }}</td><td>{{ n.publishedAt?.split('T')[0] }}</td>
        <td class="actions"><RouterLink :to="`/admin/notes/${n.id}/edit`">编辑</RouterLink><button @click="del(n.id, n.title)">删除</button></td>
      </tr></tbody>
    </table>
  </div>
</template>
<style scoped>
.admin-list { padding: 1rem 0; }
.toolbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.5rem; }
.btn { background: var(--link); color: #fff; padding: 0.5rem 1rem; border-radius: 6px; font-size: 0.95em; }
.state { text-align: center; color: var(--text-secondary); padding: 3rem 0; }
.state.error { color: #e53e3e; }
.data-table { width: 100%; border-collapse: collapse; }
.data-table th, .data-table td { padding: 0.6rem 0.8rem; border-bottom: 1px solid var(--border); text-align: left; }
.data-table th { font-size: 0.85em; color: var(--text-secondary); }
.actions { display: flex; gap: 0.8rem; }
.actions a { color: var(--link); }
.actions button { color: #e53e3e; background: none; border: none; cursor: pointer; font-size: 0.95em; }
</style>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import api from '@/api/client'
import type { ProjectsVO } from '@/models/types'

const projects = ref<ProjectsVO[]>([])
const loading = ref(true); const error = ref(false)
const fetchList = async () => {
  loading.value = true; error.value = false
  try { projects.value = await api.get('/admin/projects') }
  catch { error.value = true }
  finally { loading.value = false }
}
const del = async (id: number, name: string) => {
  if (!confirm(`确定删除「${name}」？`)) return
  await api.delete(`/admin/projects/${id}`)
  projects.value = projects.value.filter(p => p.id !== id)
}
onMounted(fetchList)
</script>
<template>
  <div class="admin-list">
    <div class="toolbar"><h1>项目管理</h1><RouterLink to="/admin/projects/new" class="btn">新建项目</RouterLink></div>
    <div v-if="loading" class="state">加载中...</div>
    <div v-else-if="error" class="state error">加载失败 <button @click="fetchList">重试</button></div>
    <div v-else-if="!projects.length" class="state">暂无项目</div>
    <table v-else class="data-table">
      <thead><tr><th>名称</th><th>描述</th><th>操作</th></tr></thead>
      <tbody><tr v-for="p in projects" :key="p.id">
        <td>{{ p.name }}</td><td>{{ p.description }}</td>
        <td class="actions"><RouterLink :to="`/admin/projects/${p.id}/edit`">编辑</RouterLink><button @click="del(p.id, p.name)">删除</button></td>
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

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import api from '@/api/client'
import type { CommentAdminVO } from '@/models/types'

const comments = ref<CommentAdminVO[]>([])
const loading = ref(true); const error = ref(false)
const fetchList = async () => {
  loading.value = true; error.value = false
  try { comments.value = await api.get<CommentAdminVO[]>('/admin/comments') }
  catch { error.value = true }
  finally { loading.value = false }
}
const del = async (id: number) => {
  if (!confirm('确定删除此评论？（子回复将一并删除）')) return
  await api.delete(`/admin/comments/${id}`)
  comments.value = comments.value.filter(c => c.id !== id)
}
onMounted(fetchList)
</script>
<template>
  <div class="admin-list">
    <h1>评论管理</h1>
    <div v-if="loading" class="state">加载中...</div>
    <div v-else-if="error" class="state error">加载失败 <button @click="fetchList">重试</button></div>
    <div v-else-if="!comments.length" class="state">暂无评论</div>
    <table v-else class="data-table">
      <thead><tr><th>作者</th><th>内容</th><th>文章</th><th>时间</th><th>操作</th></tr></thead>
      <tbody><tr v-for="c in comments" :key="c.id">
        <td>{{ c.author_name }}</td><td>{{ c.content?.slice(0, 50) }}</td><td>{{ c.blog_title }}</td>
        <td>{{ c.created_at }}</td>
        <td><button class="del" @click="del(c.id)">删除</button></td>
      </tr></tbody>
    </table>
  </div>
</template>
<style scoped>
.admin-list { padding: 1rem 0; }
.state { text-align: center; color: var(--text-secondary); padding: 3rem 0; }
.state.error { color: #e53e3e; }
.data-table { width: 100%; border-collapse: collapse; margin-top: 1rem; }
.data-table th, .data-table td { padding: 0.6rem 0.8rem; border-bottom: 1px solid var(--border); text-align: left; }
.data-table th { font-size: 0.85em; color: var(--text-secondary); }
.del { color: #e53e3e; background: none; border: none; cursor: pointer; }
</style>

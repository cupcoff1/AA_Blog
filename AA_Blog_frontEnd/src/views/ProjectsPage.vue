<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { FolderOpen, Pencil, Trash2 } from '@lucide/vue'
import api from '@/api/client'
import { isAdmin } from '@/router/auth'
import type { ProjectsVO } from '@/models/types'

const projects = ref<ProjectsVO[]>([])
const loading = ref(true)
const error = ref(false)

const deleteProject = async (id: number, name: string) => {
  if (!confirm(`删除「${name}」？`)) return
  try {
    await api.delete(`/admin/projects/${id}`)
    projects.value = projects.value.filter(p => p.id !== id)
  } catch {
    alert('删除失败，请重试')
  }
}

onMounted(async () => {
  try {
    projects.value = await api.get<ProjectsVO[]>('/projects')
  } catch {
    error.value = true
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <div class="projects-page">
    <div class="section-head">
      <h1 class="section-title"><FolderOpen :size="32" /> Projects</h1>
      <p class="section-desc">我做过的开源项目</p>
    </div>

    <div v-if="loading" class="state">加载中...</div>
    <div v-else-if="error" class="state error">加载失败</div>
    <div v-else-if="!projects.length" class="state">暂无项目</div>

    <div v-else class="project-grid">
      <div v-for="proj in projects" :key="proj.id" class="project-card">
        <a v-if="proj.githubUrl" :href="proj.githubUrl" target="_blank" class="project-name">{{ proj.name }}</a>
        <span v-else class="project-name">{{ proj.name }}</span>
        <p>{{ proj.description }}</p>
        <div v-if="isAdmin" class="card-actions">
          <RouterLink :to="`/projects/${proj.id}/edit`" class="icon-btn" title="编辑">
            <Pencil :size="14" />
          </RouterLink>
          <button @click="deleteProject(proj.id, proj.name)" class="icon-btn icon-del" title="删除">
            <Trash2 :size="14" />
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.projects-page { padding: 2.5rem 0; }
.section-head { margin-bottom: 1.5rem; }
.section-title { font-size: 2.8rem; margin-bottom: 0.2em; display: flex; align-items: center; gap: 10px; }
.section-desc { color: var(--text-secondary); font-size: 0.95em; margin: 0; }
.state { text-align: center; color: var(--text-secondary); padding: 4rem 0; }
.state.error { color: #e53e3e; }
.project-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(260px, 1fr)); gap: 1rem; }
.project-card { padding: 1rem 1.2rem; background: var(--bg-card); backdrop-filter: blur(10px); border: 2px solid var(--border); border-radius: var(--radius); box-shadow: 0 2px 8px rgba(0,0,0,0.12); }
.project-name { font-weight: 600; display: block; margin-bottom: 0.2em; font-size: 1.1em; }
.project-name[href]:hover { text-decoration: underline; text-decoration-thickness: 2px; }
.project-card p { color: var(--text-secondary); font-size: 0.92em; margin-bottom: 0.5em; }
.card-actions { display: flex; gap: 4px; margin-top: 0.5rem; justify-content: flex-end; }
.icon-btn { display: flex; align-items: center; justify-content: center; width: 28px; height: 28px; border-radius: 50%; color: var(--text-secondary); background: none; border: none; cursor: pointer; }
.icon-btn:hover { background: rgba(147, 197, 253, 0.25); color: rgba(147, 197, 253, 1); }
.icon-del:hover { background: rgba(229, 62, 62, 0.15); color: #e53e3e; }
</style>

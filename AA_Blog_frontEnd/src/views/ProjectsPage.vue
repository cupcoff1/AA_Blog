<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { FolderOpen } from '@lucide/vue'
import api from '@/api/client'
import type { ProjectsVO } from '@/models/types'

const projects = ref<ProjectsVO[]>([])
const loading = ref(true)
const error = ref(false)

onMounted(async () => {
  try {
    projects.value = await api.get('/projects')
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
        <div class="tags" v-if="proj.tags?.length">
          <span v-for="tag in proj.tags" :key="tag.id" class="tag-btn">{{ tag.name }}</span>
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
.project-card { padding: 1rem 1.2rem; background: rgba(246, 240, 223, 0.6); backdrop-filter: blur(10px); border: 2px solid var(--border); border-radius: var(--radius); box-shadow: 0 2px 8px rgba(0,0,0,0.12); }
.project-name { font-weight: 600; display: block; margin-bottom: 0.2em; font-size: 1.1em; }
.project-name[href]:hover { text-decoration: underline; text-decoration-thickness: 2px; }
.project-card p { color: var(--text-secondary); font-size: 0.92em; margin-bottom: 0.5em; }
.tags { display: flex; gap: 0.4rem; flex-wrap: wrap; }
.tag-btn {
  display: inline-block; padding: 2px 10px;
  border: 1px solid var(--border); border-radius: var(--radius);
  font-size: 0.78em; color: var(--text-secondary);
  background: var(--bg-secondary);
}
</style>

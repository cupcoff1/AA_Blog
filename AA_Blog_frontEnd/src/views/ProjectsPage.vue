<script setup lang="ts">
import { ref, onMounted } from 'vue'
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
    <h1>Projects</h1>

    <div v-if="loading" class="state">加载中...</div>
    <div v-else-if="error" class="state error">加载失败</div>
    <div v-else-if="!projects.length" class="state">暂无项目</div>

    <div v-else class="project-grid">
      <div v-for="proj in projects" :key="proj.id" class="project-card">
        <h2>{{ proj.name }}</h2>
        <p>{{ proj.description }}</p>
        <div class="tags" v-if="proj.tags?.length">
          <span v-for="tag in proj.tags" :key="tag.id" class="tag">{{ tag.name }}</span>
        </div>
        <div class="project-links">
          <a v-if="proj.demoUrl" :href="proj.demoUrl" target="_blank">Demo</a>
          <a v-if="proj.githubUrl" :href="proj.githubUrl" target="_blank">GitHub</a>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.projects-page {
  padding: 2rem 0;
}

.projects-page h1 {
  margin-bottom: 1.5rem;
}

.state {
  text-align: center;
  color: var(--text-secondary);
  padding: 3rem 0;
}

.state.error {
  color: #e53e3e;
}

.project-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 1.5rem;
}

.project-card {
  padding: 1.2rem;
  border: 1px solid var(--border);
  border-radius: 6px;
}

.project-card h2 {
  margin: 0 0 0.5rem;
}

.project-card p {
  color: var(--text-secondary);
  font-size: 0.95em;
  margin-bottom: 0.8rem;
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

.project-links {
  display: flex;
  gap: 1rem;
}
</style>


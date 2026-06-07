<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import api from '@/api/client'
import type { AboutVO } from '@/models/types'

const about = ref<AboutVO | null>(null)
const loading = ref(true)
const error = ref(false)

const parseList = (s: string) => {
  try { return JSON.parse(s) as string[] } catch { return [] }
}

const stickyNotes = computed(() => {
  const a = about.value
  const skills = a?.skills ? parseList(a.skills) : []
  const hobbies = a?.hobbies ? parseList(a.hobbies) : []
  return [
    { title: '👋', content: `I'm ${a?.nickname || 'AA_'}`, color: '#fff3cd', rotate: -3 },
    { title: '💻', content: skills.join(' · ') || 'Loading...', color: '#d4edda', rotate: 2 },
    { title: '📍', content: a?.location || '地球某处', color: '#cce5ff', rotate: -1 },
    { title: '📚', content: a?.bio?.substring(0, 60) || '...', color: '#f8d7da', rotate: 4 },
    { title: '🎵', content: hobbies.join(' · ') || '探索中', color: '#e8daef', rotate: -2 },
    { title: '🔗', content: 'github.com/cupcoff1', color: '#d1ecf1', rotate: 1 },
  ]
})

onMounted(async () => {
  try { about.value = await api.get('/about') }
  catch { error.value = true }
  finally { loading.value = false }
})
</script>

<template>
  <div class="about-page">
    <div v-if="loading" class="state">加载中...</div>
    <div v-else-if="error" class="state error">加载失败</div>

    <div v-else class="sticky-wall">
      <div v-for="(note, i) in stickyNotes" :key="i"
        class="sticky" :style="{
          '--bg': note.color,
          transform: `rotate(${note.rotate}deg)`,
          marginTop: i === 1 ? '2rem' : '0'
        }">
        <div class="sticky-title">{{ note.title }}</div>
        <div class="sticky-body">{{ note.content }}</div>
      </div>
      <div class="center-note sticky" style="--bg: #fef3c7; transform: rotate(-1deg);">
        <div class="sticky-title">🌟</div>
        <div class="sticky-body">这是我用 Spring Boot + Vue 3 做的全栈博客</div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.about-page { padding: 3rem 0; }
.state { text-align: center; color: var(--text-secondary); padding: 4rem 0; }
.state.error { color: #e53e3e; }

.sticky-wall {
  display: flex; flex-wrap: wrap; justify-content: center;
  gap: 1.5rem; padding: 1rem 0;
}
.sticky {
  background: var(--bg);
  position: relative;
  padding: 1.2rem 1.4rem;
  width: 200px; min-height: 120px;
  box-shadow: 2px 3px 8px rgba(0,0,0,0.1);
  transition: transform 0.2s;
}
.sticky::after {
  content: ''; position: absolute; inset: 0;
  background:
    repeating-linear-gradient(transparent 0, transparent 26px, rgba(0,0,0,0.04) 26px, rgba(0,0,0,0.04) 28px),
    repeating-linear-gradient(transparent 0, transparent 23px, rgba(0,0,0,0.02) 23px, rgba(0,0,0,0.02) 24px);
  pointer-events: none;
}
.sticky:hover { transform: scale(1.05) !important; z-index: 10; }
.sticky-title { font-size: 1.5em; margin-bottom: 0.5em; }
.sticky-body { font-family: 'Ma Shan Zheng', cursive; font-size: 1.05em; color: rgba(0,0,0,0.65); line-height: 1.6; }
.center-note { width: 220px; min-height: 140px; }
</style>

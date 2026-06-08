<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'

import { Plus, X, Check } from '@lucide/vue'
import api from '@/api/client'
import type { StickyNoteVO } from '@/models/types'

const customNotes = ref<StickyNoteVO[]>([])
const loading = ref(true)
const error = ref(false)
const isAdmin = !!localStorage.getItem('admin_token')
const showForm = ref(false)
const newContent = ref('')
const newColor = ref('#fff3cd')
const colors = ['#fff3cd', '#d4edda', '#cce5ff', '#f8d7da', '#e8daef', '#d1ecf1']

const allNotes = computed(() => {
  return customNotes.value.map(n => ({
    id: n.id, title: '', content: n.content, color: n.color, rotate: n.rotate, custom: true
  }))
})

const addNote = async () => {
  if (!newContent.value.trim()) return
  try {
    await api.post('/admin/sticky-notes', {
      content: newContent.value.trim(),
      color: newColor.value,
      rotate: Math.floor(Math.random() * 7) - 3
    })
    newContent.value = ''
    showForm.value = false
    // 刷新自定义便签
    customNotes.value = await api.get('/sticky-notes')
  } catch {}
}

const delNote = async (id: number) => {
  if (!confirm('删除这张便签？')) return
  await api.delete(`/admin/sticky-notes/${id}`)
  customNotes.value = customNotes.value.filter(n => n.id !== id)
}

onMounted(async () => {
  try {
    customNotes.value = await api.get('/sticky-notes') || []
  } catch { error.value = true }
  finally { loading.value = false }
})
</script>

<template>
  <div class="about-page">
    <div v-if="loading" class="state">加载中...</div>
    <div v-else-if="error" class="state error">加载失败</div>

    <div v-else class="sticky-wall">
      <div v-for="(note, i) in allNotes" :key="i"
        class="sticky" :class="{ 'is-custom': (note as any).custom }"
        :style="{ '--bg': note.color, transform: `rotate(${note.rotate}deg)` }">
        <div class="sticky-body">{{ note.content }}</div>
        <button v-if="isAdmin && (note as any).custom"
          class="del-note" @click="delNote((note as any).id)" title="删除">
          <X :size="14" />
        </button>
      </div>

      <!-- 新增按钮 -->
      <button v-if="isAdmin && !showForm" class="sticky add-sticky" @click="showForm = true">
        <Plus :size="24" />
      </button>

      <!-- 新增表单 -->
      <div v-if="showForm" class="sticky form-sticky" :style="{ '--bg': newColor, transform: 'rotate(-1deg)' }">
        <textarea v-model="newContent" rows="3" placeholder="写点什么..." class="form-input" />
        <div class="color-pick">
          <button v-for="c in colors" :key="c" class="color-dot"
            :class="{ active: newColor === c }"
            :style="{ background: c }"
            @click="newColor = c" />
        </div>
        <div class="form-actions">
          <button class="form-btn" @click="addNote" title="贴上去"><Check :size="18" /></button>
          <button class="form-cancel" @click="showForm = false" title="取消"><X :size="18" /></button>
        </div>
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
  gap: 1rem; padding: 1rem 0;
}
.sticky { margin: 0.5rem; }
.sticky:nth-child(odd) { margin-top: 1.5rem; }
.sticky:nth-child(3n) { margin-left: -0.5rem; margin-right: 1rem; }
.sticky:nth-child(4n+1) { margin-top: -0.3rem; }
.sticky {
  background: var(--bg); position: relative;
  padding: 1.2rem 1.4rem; width: 200px; min-height: 120px;
  box-shadow: 2px 3px 8px rgba(0,0,0,0.1);
  transition: transform 0.2s;
}
.sticky:hover { transform: scale(1.05) !important; z-index: 10; }
.sticky::after {
  content: ''; position: absolute; inset: 0;
  background:
    repeating-linear-gradient(transparent 0, transparent 26px, rgba(0,0,0,0.04) 26px, rgba(0,0,0,0.04) 28px),
    repeating-linear-gradient(transparent 0, transparent 23px, rgba(0,0,0,0.02) 23px, rgba(0,0,0,0.02) 24px);
  pointer-events: none;
}
.sticky-title { font-size: 1.5em; margin-bottom: 0.5em; }
.sticky-body { font-family: 'Ma Shan Zheng', cursive; font-size: 1.05em; color: rgba(0,0,0,0.65); line-height: 1.6; }
.del-note { position: absolute; top: 6px; right: 6px; width: 24px; height: 24px; border-radius: 50%; border: none; background: rgba(0,0,0,0.08); color: rgba(0,0,0,0.4); cursor: pointer; display: flex; align-items: center; justify-content: center; z-index: 5; }
.del-note:hover { background: rgba(229,62,62,0.2); color: #e53e3e; }

.add-sticky { display: flex; align-items: center; justify-content: center; cursor: pointer; border: 2px dashed var(--border); background: transparent; color: var(--text-secondary); min-height: 120px; }
.add-sticky:hover { border-color: var(--link); color: var(--link); background: rgba(177,45,108,0.05); }
body.dark .add-sticky:hover { background: rgba(200,146,231,0.08); }

.form-sticky { min-height: 180px; }
.form-input { width: 100%; border: none; background: transparent; font-family: 'Ma Shan Zheng', cursive; font-size: 1em; resize: none; outline: none; color: rgba(0,0,0,0.65); }
.color-pick { display: flex; gap: 6px; margin: 0.5rem 0; }
.color-dot { width: 20px; height: 20px; border-radius: 50%; border: 2px solid transparent; cursor: pointer; }
.color-dot.active { border-color: rgba(0,0,0,0.3); }
.form-actions { display: flex; gap: 6px; justify-content: flex-end; }
.form-btn, .form-cancel { width: 32px; height: 32px; border-radius: 50%; border: none; cursor: pointer; display: flex; align-items: center; justify-content: center; }
.form-btn { background: rgba(0,0,0,0.1); color: rgba(0,0,0,0.5); }
.form-btn:hover { background: rgba(147,197,253,0.3); color: rgba(59,130,246,0.9); }
.form-cancel { background: none; color: rgba(0,0,0,0.3); }
.form-cancel:hover { background: rgba(229,62,62,0.15); color: #e53e3e; }
</style>

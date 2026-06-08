<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { GitFork, Send, X, MessageSquare, LogOut } from '@lucide/vue'
import api from '@/api/client'
import type { StickyNoteVO } from '@/models/types'

const notes = ref<StickyNoteVO[]>([])
const loading = ref(true)
const error = ref(false)

const commenter = computed(() => {
  const raw = localStorage.getItem('commenter')
  return raw ? JSON.parse(raw) as { name: string; avatar: string } : null
})
const isAdmin = computed(() => !!localStorage.getItem('admin_token'))

const newContent = ref('')
const newCategory = ref<'to_aa' | 'to_website'>('to_aa')
const submitting = ref(false)

const colors = ['#fff3cd', '#d4edda', '#cce5ff', '#f8d7da', '#e8daef', '#d1ecf1']

const login = async () => {
  const { url } = await api.get<{ url: string }>('/auth/github/url')
  window.location.href = url
}
const logout = () => {
  localStorage.removeItem('commenter_token')
  localStorage.removeItem('commenter')
  window.location.reload()
}

const fetchNotes = async () => {
  try { notes.value = await api.get<StickyNoteVO[]>('/sticky-notes?source=guest') || [] }
  catch { error.value = true }
  finally { loading.value = false }
}

const submit = async () => {
  if (!newContent.value.trim()) return
  submitting.value = true
  try {
    const color = colors[Math.floor(Math.random() * colors.length)]
    await api.post('/sticky-notes', {
      content: newContent.value.trim(),
      color,
      rotate: Math.floor(Math.random() * 7) - 3,
      category: newCategory.value
    })
    newContent.value = ''
    await fetchNotes()
  } catch { /* handled */ }
  finally { submitting.value = false }
}

const del = async (id: number) => {
  if (!confirm('删除这张便签？')) return
  try {
    if (isAdmin.value) await api.delete(`/admin/sticky-notes/${id}`)
    else await api.delete(`/sticky-notes/${id}`)
    notes.value = notes.value.filter(n => n.id !== id)
  } catch { alert('无权删除此便签') }
}

onMounted(fetchNotes)
</script>

<template>
  <div class="guest-wall">
    <div class="section-head">
      <h1 class="section-title"><MessageSquare :size="32" /> Leave a Note</h1>
      <p class="section-desc">想对我说的话，或者对网站的建议</p>
    </div>

    <div v-if="loading" class="state">加载中...</div>
    <div v-else-if="error" class="state error">加载失败</div>

    <div v-else>
      <div v-if="!commenter && !isAdmin" class="login-area">
        <button class="github-btn" @click="login"><GitFork :size="18" /> 通过 GitHub 登录后留言</button>
      </div>

      <div v-if="commenter || isAdmin" class="write-area">
        <div class="write-header">
          <span v-if="commenter">
            <img :src="commenter.avatar" class="avatar" alt="" />
            <strong>{{ commenter.name }}</strong>
          </span>
          <button v-if="commenter" class="logout-btn" @click="logout"><LogOut :size="14" /></button>
        </div>
        <div class="category-row">
          <button
            :class="{ active: newCategory === 'to_aa' }"
            @click="newCategory = 'to_aa'">To AA_</button>
          <button
            :class="{ active: newCategory === 'to_website' }"
            @click="newCategory = 'to_website'">To Website</button>
        </div>
        <div class="write-row">
          <textarea v-model="newContent" rows="3" :placeholder="newCategory === 'to_aa' ? '写下你对 AA_ 说的话...' : '写下你对网站的建议...'" />
          <button class="send-btn" :disabled="submitting || !newContent.trim()" @click="submit">
            <Send :size="16" /> 贴上去
          </button>
        </div>
      </div>

      <div class="sticky-wall">
        <div v-for="note in notes" :key="note.id"
          class="sticky" :class="[note.category === 'to_website' ? 'note-website' : 'note-aa']"
          :style="{ '--bg': note.color, transform: `rotate(${note.rotate}deg)` }">
          <div class="sticky-body">{{ note.content }}</div>
          <span class="sticky-tag" :class="note.category === 'to_website' ? 'tag-website' : 'tag-aa'">
            {{ note.category === 'to_website' ? 'To Website' : 'To AA_' }}
          </span>
          <div class="sticky-foot" v-if="note.authorName">
            <span class="sticky-author">{{ note.authorName }}</span>
          </div>
          <button v-if="note.own || isAdmin"
            class="del-btn" @click="del(note.id)" title="删除">
            <X :size="12" />
          </button>
        </div>
      </div>

      <p v-if="!notes.length && !loading" class="state">还没有便签，来做第一个留言的人吧 (◕‿◕)</p>
    </div>
  </div>
</template>

<style scoped>
.guest-wall { padding: 2rem 0; max-width: 800px; }
.section-head { margin-bottom: 1.5rem; }
.section-title { font-size: 2.8rem; margin-bottom: 0.2em; display: flex; align-items: center; gap: 10px; }
@media screen and (max-width: 600px) { .section-title { font-size: 1.8rem; } }
.section-desc { color: var(--text-secondary); font-size: 0.95em; margin: 0; }
.state { text-align: center; color: var(--text-secondary); padding: 3rem 0; }
.state.error { color: #e53e3e; }

.login-area { text-align: center; padding: 2rem 0; }
.github-btn { display: inline-flex; align-items: center; gap: 8px; padding: 0.5rem 1.2rem; background: #333; color: #fff; border: none; border-radius: 8px; font-size: 0.95em; cursor: pointer; }
.github-btn:hover { background: #555; }

.write-area {
  background: #fef9ef; border: 1px solid #e8dcc8; border-radius: 8px; padding: 1.2rem 1.4rem;
  margin-bottom: 2rem; box-shadow: 1px 2px 6px rgba(0,0,0,0.04);
}
body.dark .write-area { background: #2a2a24; border-color: #4a4a3f; }

.write-header { display: flex; align-items: center; gap: 0.6rem; margin-bottom: 0.8rem; }
.write-header .avatar { width: 26px; height: 26px; border-radius: 50%; }
.write-header strong { font-size: 0.95em; }
.logout-btn { margin-left: auto; display: flex; align-items: center; justify-content: center; width: 28px; height: 28px; border-radius: 50%; border: none; background: rgba(0,0,0,0.05); color: var(--text-secondary); cursor: pointer; }
.logout-btn:hover { background: rgba(229,62,62,0.12); color: #e53e3e; }

.category-row { display: flex; gap: 0.4rem; margin-bottom: 0.8rem; }
.category-row button {
  padding: 0.35rem 1rem; border: 1.5px solid #e8dcc8; border-radius: 20px;
  background: transparent; color: #8b7355; cursor: pointer; font-size: 0.85em;
  transition: all 0.15s;
}
body.dark .category-row button { border-color: #4a4a3f; color: #a3967e; }
.category-row button.active {
  background: #b12d6c; color: #fff; border-color: #b12d6c;
}
body.dark .category-row button.active { background: #c892e7; border-color: #c892e7; color: #1c1c20; }

.write-row { display: flex; gap: 0.6rem; align-items: flex-end; }
.write-row textarea {
  flex: 1; padding: 0.7rem 0.9rem;
  border: 1.5px solid #e8dcc8; border-radius: 8px;
  background: #fffef8; color: var(--text);
  font-family: 'Ma Shan Zheng', cursive; font-size: 1em;
  resize: vertical; outline: none;
  transition: border-color 0.15s;
  line-height: 1.6;
}
body.dark .write-row textarea { background: #33332c; border-color: #4a4a3f; }
.write-row textarea:focus { border-color: var(--link); }

.send-btn {
  display: inline-flex; align-items: center; gap: 6px;
  padding: 0.45rem 1.1rem; background: var(--link); color: #fff;
  border: none; border-radius: 8px; font-size: 0.9em; cursor: pointer;
  white-space: nowrap; transition: opacity 0.15s;
}
.send-btn:hover { opacity: 0.85; }
.send-btn:disabled { opacity: 0.5; cursor: not-allowed; }

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
.sticky-body { font-family: 'Ma Shan Zheng', cursive; font-size: 1.05em; color: rgba(0,0,0,0.65); line-height: 1.6; }
.sticky-tag { position: absolute; bottom: 8px; right: 10px; padding: 1px 6px; border-radius: 8px; font-size: 0.65em; font-weight: 600; }
.tag-aa { background: rgba(177,45,108,0.15); color: #b12d6c; }
.tag-website { background: rgba(37,99,235,0.12); color: #2563eb; }
.sticky-foot { margin-top: 0.3rem; }
.sticky-author { font-size: 0.75em; color: rgba(0,0,0,0.35); }
.del-btn { position: absolute; top: 6px; right: 6px; width: 24px; height: 24px; border-radius: 50%; border: none; background: rgba(0,0,0,0.08); color: rgba(0,0,0,0.4); cursor: pointer; display: flex; align-items: center; justify-content: center; z-index: 5; }
.del-btn:hover { background: rgba(229,62,62,0.2); color: #e53e3e; }
</style>

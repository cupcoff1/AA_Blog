<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { GitFork, Send, X, MessageSquare, LogOut } from '@lucide/vue'
import { isAdmin } from '@/router/auth'
import { getGitHubAuthUrl, commenterLogout, checkCommenterStatus } from '@/api/auth'
import { listStickyNotes, createGuestNote, deleteGuestNote, deleteAdminNote } from '@/api/sticky-note'
import '@/assets/sticky.css'
import type { StickyNoteVO } from '@/models/types'
import { STICKY_COLORS, randomStickyRotation } from '@/models/constants'

const notes = ref<StickyNoteVO[]>([])
const loading = ref(true)
const error = ref(false)

const commenter = ref<{ name: string; avatar: string } | null>(null)

const newContent = ref('')
const newCategory = ref<'to_aa' | 'to_website'>('to_aa')
const submitting = ref(false)

const login = async () => {
  const { url } = await getGitHubAuthUrl()
  window.location.href = url
}
const logout = async () => {
  await commenterLogout()
  commenter.value = null
}

const fetchNotes = async () => {
  try { notes.value = await listStickyNotes('guest') || [] }
  catch { error.value = true }
  finally { loading.value = false }
}

const submit = async () => {
  if (!newContent.value.trim()) return
  submitting.value = true
  try {
    const color = STICKY_COLORS[Math.floor(Math.random() * STICKY_COLORS.length)]
    await createGuestNote({
      content: newContent.value.trim(),
      color,
      rotate: randomStickyRotation(),
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
    if (isAdmin.value) await deleteAdminNote(id)
    else await deleteGuestNote(id)
    notes.value = notes.value.filter(n => n.id !== id)
  } catch (e: unknown) {
    const msg = e instanceof Error ? e.message : ''
    alert(msg.includes('无权') ? msg : '删除失败，请重试')
  }
}

const fetchAuth = async () => {
  try {
    const data = await checkCommenterStatus()
    if (data.authenticated && data.name) {
      commenter.value = { name: data.name, avatar: data.avatar || '' }
    }
  } catch { /* 未登录 */ }
}

onMounted(async () => {
  await fetchAuth()
  await fetchNotes()
})
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
            <img loading="lazy" :src="commenter.avatar" class="avatar" alt="" />
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
            <img loading="lazy" v-if="note.authorAvatar" :src="note.authorAvatar" class="sticky-avatar" alt="" />
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
.state.error { color: var(--color-error); }

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
.logout-btn:hover { background: rgba(229,62,62,0.12); color: var(--color-error); }

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
  font-family: var(--font-sticky); font-size: 1em;
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


.sticky-tag { position: absolute; bottom: 8px; right: 10px; padding: 1px 6px; border-radius: 8px; font-size: 0.65em; font-weight: 600; }
.tag-aa { background: rgba(177,45,108,0.15); color: #b12d6c; }
.tag-website { background: rgba(37,99,235,0.12); color: #2563eb; }
.sticky-foot { position: absolute; bottom: 8px; left: 10px; display: flex; align-items: center; gap: 4px; }
.sticky-avatar { width: 16px; height: 16px; border-radius: 50%; }
.sticky-author { font-size: 0.75em; color: rgba(0,0,0,0.35); }
.del-btn { position: absolute; top: 6px; right: 6px; width: 24px; height: 24px; border-radius: 50%; border: none; background: rgba(0,0,0,0.08); color: rgba(0,0,0,0.4); cursor: pointer; display: flex; align-items: center; justify-content: center; z-index: 5; }
.del-btn:hover { background: rgba(229,62,62,0.2); color: var(--color-error); }
</style>

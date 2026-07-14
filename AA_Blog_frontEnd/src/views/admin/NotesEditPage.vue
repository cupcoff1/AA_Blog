<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import TagEditor from '@/components/TagEditor.vue'
import { getAdminNote, createNote, updateNote } from '@/api/note'
import type { NoteVO, NoteCreateRequest, TagVO } from '@/models/types'

const route = useRoute(); const router = useRouter()
const editId = route.params.id ? Number(route.params.id) : null
const title = ref(''); const content = ref('')
const tagIds = ref<number[]>([]); const newTags = ref<string[]>([])
const loading = ref(false); const error = ref('')

const submit = async () => {
  if (!title.value || !content.value) { error.value = '标题和正文不能为空'; return }
  loading.value = true; error.value = ''
  try {
    const body: NoteCreateRequest = { title: title.value, content: content.value, tagIds: tagIds.value, newTags: newTags.value }
    editId ? await updateNote(editId, body) : await createNote(body)
    router.push('/notes')
  } catch (e: unknown) { error.value = e instanceof Error ? e.message : '保存失败' } finally { loading.value = false }
}
onMounted(async () => {
  if (!editId) return
  loading.value = true
  try {
    const n = await getAdminNote(editId)
    title.value = n.title; content.value = n.content
    tagIds.value = n.tags?.map((t: TagVO) => t.id) || []
  } catch {
    error.value = '加载笔记失败'
  } finally {
    loading.value = false
  }
})
</script>
<template>
  <div class="edit-page">
    <h1 class="page-title">{{ editId ? '编辑笔记' : '新建笔记' }}</h1>
    <div class="error" v-if="error">{{ error }}</div>
    <form @submit.prevent="submit">
      <label>标题</label><input v-model="title" type="text" placeholder="笔记标题" />
      <label>正文（Markdown）</label><textarea v-model="content" rows="12" placeholder="Markdown 内容" />
      <label>标签</label>
      <TagEditor v-model="newTags" />
      <button type="submit" class="submit-btn" :disabled="loading">{{ loading ? '保存中...' : '保存' }}</button>
    </form>
  </div>
</template>
<style scoped>
.edit-page { max-width: 800px; padding: 2rem 0; }
.page-title { font-size: 2rem; margin-bottom: 1.5rem; }
.error { background: #fed7d7; color: #c53030; padding: 0.6rem 1rem; border-radius: var(--radius); margin-bottom: 1rem; font-size: 0.9em; }
form { display: flex; flex-direction: column; gap: 0.8rem; }
label { font-weight: 600; font-size: 0.9em; margin-top: 0.3rem; color: var(--text-secondary); }
input, textarea { padding: 0.65rem 0.8rem; border: 1px solid var(--border); border-radius: var(--radius); font-size: 0.95em; font-family: inherit; background: var(--bg); color: var(--text); outline: none; }
input:focus, textarea:focus { border-color: var(--link); box-shadow: 0 0 0 2px rgba(177,45,108,0.08); }
.submit-btn { margin-top: 1rem; padding: 0.5rem 1.5rem; background: var(--text); color: var(--bg); border: none; border-radius: var(--radius); font-size: 0.9em; font-weight: 500; cursor: pointer; transition: opacity 0.2s; width: fit-content; }
.submit-btn:hover { opacity: 0.85; }
.submit-btn:disabled { opacity: 0.4; cursor: not-allowed; }
</style>

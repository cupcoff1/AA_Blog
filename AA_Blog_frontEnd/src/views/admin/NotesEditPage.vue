<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import api from '@/api/client'

const route = useRoute(); const router = useRouter()
const editId = route.params.id ? Number(route.params.id) : null
const title = ref(''); const content = ref('')
const tagIds = ref<number[]>([]); const newTagInput = ref(''); const newTags = ref<string[]>([])
const loading = ref(false); const error = ref('')

const addNewTag = () => { const n = newTagInput.value.trim(); if (n && !newTags.value.includes(n)) { newTags.value.push(n); newTagInput.value = '' } }
const removeNewTag = (n: string) => { newTags.value = newTags.value.filter(t => t !== n) }
const submit = async () => {
  if (!title.value || !content.value) { error.value = '标题和正文不能为空'; return }
  loading.value = true; error.value = ''
  try {
    const body = { title: title.value, content: content.value, tagIds: tagIds.value, newTags: newTags.value }
    editId ? await api.put(`/admin/notes/${editId}`, body) : await api.post('/admin/notes', body)
    router.push('/notes')
  } catch (e: any) { error.value = e.message || '保存失败' } finally { loading.value = false }
}
onMounted(async () => {
  if (editId) { const n = await api.get(`/admin/notes/${editId}`); title.value = n.title; content.value = n.content; tagIds.value = n.tags?.map((t: any) => t.id) || [] }
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
      <div class="tag-area">
        <span v-for="t in newTags" :key="t" class="tag-pill">{{ t }} <button type="button" @click="removeNewTag(t)">&times;</button></span>
        <div class="tag-input">
          <input v-model="newTagInput" @keyup.enter.prevent="addNewTag" placeholder="输入标签，回车添加" />
          <button type="button" @click="addNewTag" class="tag-add">+</button>
        </div>
      </div>
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
.tag-area { display: flex; flex-wrap: wrap; gap: 0.4rem; align-items: center; }
.tag-pill { display: flex; align-items: center; gap: 4px; background: var(--bg-secondary); padding: 3px 10px; border-radius: 20px; font-size: 0.85em; }
.tag-pill button { background: none; border: none; cursor: pointer; color: var(--text-secondary); font-size: 1em; padding: 0; line-height: 1; }
.tag-input { display: flex; }
.tag-input input { flex: 1; border-top-right-radius: 0; border-bottom-right-radius: 0; font-size: 0.85em; padding: 5px 8px; }
.tag-add { padding: 5px 12px; border: 1px solid var(--border); border-left: none; border-radius: 0 var(--radius) var(--radius) 0; background: var(--bg-secondary); cursor: pointer; color: var(--text-secondary); }
.submit-btn { margin-top: 1rem; padding: 0.5rem 1.5rem; background: var(--text); color: var(--bg); border: none; border-radius: var(--radius); font-size: 0.9em; font-weight: 500; cursor: pointer; transition: opacity 0.2s; width: fit-content; }
.submit-btn:hover { opacity: 0.85; }
.submit-btn:disabled { opacity: 0.4; cursor: not-allowed; }
</style>

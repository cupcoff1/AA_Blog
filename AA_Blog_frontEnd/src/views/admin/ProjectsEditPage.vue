<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import api from '@/api/client'

const route = useRoute(); const router = useRouter()
const editId = route.params.id ? Number(route.params.id) : null
const name = ref(''); const description = ref(''); const demoUrl = ref(''); const githubUrl = ref('')
const tagIds = ref<number[]>([]); const newTagInput = ref(''); const newTags = ref<string[]>([])
const loading = ref(false); const error = ref('')

const addNewTag = () => { const n = newTagInput.value.trim(); if (n && !newTags.value.includes(n)) { newTags.value.push(n); newTagInput.value = '' } }
const removeNewTag = (n: string) => { newTags.value = newTags.value.filter(t => t !== n) }
const submit = async () => {
  if (!name.value || !description.value) { error.value = '名称和描述不能为空'; return }
  loading.value = true; error.value = ''
  try {
    const body = { name: name.value, description: description.value, demoUrl: demoUrl.value, githubUrl: githubUrl.value, tagIds: tagIds.value, newTags: newTags.value }
    editId ? await api.put(`/admin/projects/${editId}`, body) : await api.post('/admin/projects', body)
    router.push('/admin/projects')
  } catch (e: any) { error.value = e.message || '保存失败' } finally { loading.value = false }
}
onMounted(async () => {
  if (editId) { const p = await api.get(`/admin/projects/${editId}`); name.value = p.name; description.value = p.description; demoUrl.value = p.demoUrl; githubUrl.value = p.githubUrl; tagIds.value = p.tags?.map((t: any) => t.id) || [] }
})
</script>
<template>
  <div class="edit-page">
    <h1>{{ editId ? '编辑项目' : '新建项目' }}</h1>
    <div class="error" v-if="error">{{ error }}</div>
    <form @submit.prevent="submit">
      <label>名称</label><input v-model="name" type="text" placeholder="项目名称" />
      <label>描述</label><input v-model="description" type="text" placeholder="简短描述" />
      <label>Demo 链接</label><input v-model="demoUrl" type="text" placeholder="https://..." />
      <label>GitHub 链接</label><input v-model="githubUrl" type="text" placeholder="https://github.com/..." />
      <label>标签</label>
      <div class="tag-mgr">
        <div class="tag-row" v-if="newTags.length"><span v-for="t in newTags" :key="t" class="tag">{{ t }} <button type="button" @click="removeNewTag(t)">&times;</button></span></div>
        <div class="tag-row"><input v-model="newTagInput" @keyup.enter.prevent="addNewTag" placeholder="新标签，回车添加" /><button type="button" @click="addNewTag">+</button></div>
      </div>
      <button type="submit" class="btn-submit" :disabled="loading">{{ loading ? '保存中...' : '保存' }}</button>
    </form>
  </div>
</template>
<style scoped>
.edit-page { max-width: 800px; padding: 1rem 0; }
.error { background: #fed7d7; color: #c53030; padding: 0.6rem 1rem; border-radius: 6px; margin-bottom: 1rem; font-size: 0.9em; }
form { display: flex; flex-direction: column; gap: 0.8rem; }
label { font-weight: 600; font-size: 0.95em; margin-top: 0.5rem; }
input { padding: 0.6rem 1rem; border: 1px solid var(--border); border-radius: 6px; font-size: 1em; background: var(--bg); color: var(--text); outline: none; }
input:focus { border-color: var(--link); }
.tag-mgr { display: flex; flex-direction: column; gap: 0.5rem; }
.tag-row { display: flex; gap: 0.5rem; flex-wrap: wrap; }
.tag { background: var(--bg-secondary); padding: 4px 10px; border-radius: 4px; font-size: 0.9em; display: flex; align-items: center; gap: 4px; }
.tag button { background: none; border: none; cursor: pointer; color: var(--text-secondary); font-size: 1.1em; }
.tag-row input { flex: 1; }
.tag-row button { padding: 0.5rem 1rem; background: var(--bg-secondary); border: 1px solid var(--border); border-radius: 6px; cursor: pointer; }
.btn-submit { margin-top: 1rem; padding: 0.7rem; background: var(--link); color: #fff; border: none; border-radius: 6px; font-size: 1em; cursor: pointer; }
.btn-submit:disabled { opacity: 0.6; cursor: not-allowed; }
</style>

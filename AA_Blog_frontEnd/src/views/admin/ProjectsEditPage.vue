<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import TagEditor from '@/components/TagEditor.vue'
import { getAdminProject, createProject, updateProject, updateProjectTags } from '@/api/project'
import type { ProjectCreateRequest, TagVO } from '@/models/types'

const route = useRoute(); const router = useRouter()
const editId = route.params.id ? Number(route.params.id) : null
const name = ref(''); const description = ref(''); const demoUrl = ref(''); const githubUrl = ref('')
const tagIds = ref<number[]>([]); const newTags = ref<string[]>([])
const loading = ref(false); const error = ref('')

const submit = async () => {
  if (!name.value || !description.value) { error.value = '名称和描述不能为空'; return }
  loading.value = true; error.value = ''
  try {
    const body: ProjectCreateRequest = { name: name.value, description: description.value, demoUrl: demoUrl.value, githubUrl: githubUrl.value, tagIds: tagIds.value, newTags: newTags.value }
    if (editId) {
      await updateProject(editId, body)
      await updateProjectTags(editId, tagIds.value, newTags.value)
    } else {
      await createProject(body)
    }
    router.push('/projects')
  } catch (e: unknown) { error.value = e instanceof Error ? e.message : '保存失败' } finally { loading.value = false }
}
onMounted(async () => {
  if (!editId) return
  loading.value = true
  try {
    const p = await getAdminProject(editId)
    name.value = p.name; description.value = p.description
    demoUrl.value = p.demoUrl; githubUrl.value = p.githubUrl
    tagIds.value = p.tags?.map((t: TagVO) => t.id) || []
  } catch {
    error.value = '加载项目失败'
  } finally {
    loading.value = false
  }
})
</script>
<template>
  <div class="edit-page">
    <h1 class="page-title">{{ editId ? '编辑项目' : '新建项目' }}</h1>
    <div class="error" v-if="error">{{ error }}</div>
    <form @submit.prevent="submit">
      <label>名称</label><input v-model="name" type="text" placeholder="项目名称" />
      <label>描述</label><input v-model="description" type="text" placeholder="简短描述" />
      <label>Demo 链接</label><input v-model="demoUrl" type="text" placeholder="https://..." />
      <label>GitHub 链接</label><input v-model="githubUrl" type="text" placeholder="https://github.com/..." />
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
input { padding: 0.65rem 0.8rem; border: 1px solid var(--border); border-radius: var(--radius); font-size: 0.95em; background: var(--bg); color: var(--text); outline: none; }
input:focus { border-color: var(--link); box-shadow: 0 0 0 2px rgba(177,45,108,0.08); }
.submit-btn { margin-top: 1rem; padding: 0.5rem 1.5rem; background: var(--text); color: var(--bg); border: none; border-radius: var(--radius); font-size: 0.9em; font-weight: 500; cursor: pointer; transition: opacity 0.2s; width: fit-content; }
.submit-btn:hover { opacity: 0.85; }
.submit-btn:disabled { opacity: 0.4; cursor: not-allowed; }
</style>

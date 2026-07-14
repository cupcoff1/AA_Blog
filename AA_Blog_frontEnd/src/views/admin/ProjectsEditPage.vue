<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import TagEditor from '@/components/TagEditor.vue'
import { getAdminProject, createProject, updateProject, updateProjectTags } from '@/api/project'
import '@/assets/editor.css'
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

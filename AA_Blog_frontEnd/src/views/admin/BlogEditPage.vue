<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import api from '@/api/client'
import TagEditor from '@/components/TagEditor.vue'
import type { BlogVO, BlogCreateRequest, TagVO } from '@/models/types'

const route = useRoute()
const router = useRouter()
const editId = route.params.id ? Number(route.params.id) : null

const title = ref('')
const summary = ref('')
const content = ref('')
const tagIds = ref<number[]>([])
const newTags = ref<string[]>([])
const loading = ref(false)
const error = ref('')

const textareaRef = ref<HTMLTextAreaElement | null>(null)
const uploading = ref(false)

const uploadImage = async (e: Event) => {
  const file = (e.target as HTMLInputElement).files?.[0]
  if (!file) return
  uploading.value = true
  try {
    const form = new FormData()
    form.append('file', file)
    const { url } = await api.post<{ url: string }>('/admin/upload?type=image', form, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
    const ta = textareaRef.value
    if (ta) {
      const pos = ta.selectionStart
      const md = `![](${url})`
      content.value = content.value.slice(0, pos) + md + content.value.slice(pos)
    }
  } catch { error.value = '图片上传失败' }
  finally { uploading.value = false; (e.target as HTMLInputElement).value = '' }
}

const submit = async () => {
  if (!title.value || !summary.value || !content.value) {
    error.value = '标题、摘要、正文不能为空'; return
  }
  loading.value = true; error.value = ''
  try {
    const body: BlogCreateRequest = { title: title.value, summary: summary.value, content: content.value, tagIds: tagIds.value, newTags: newTags.value }
    if (editId) await api.put(`/admin/blog/${editId}`, body)
    else await api.post('/admin/blog', body)
    router.push('/blog')
  } catch (e: unknown) { error.value = e instanceof Error ? e.message : '保存失败' }
  finally { loading.value = false }
}

onMounted(async () => {
  if (editId) {
    const blog = await api.get<BlogVO>(`/admin/blog/${editId}`)
    title.value = blog.title; summary.value = blog.summary; content.value = blog.content
    tagIds.value = blog.tags?.map((t: TagVO) => t.id) || []
  }
})
</script>

<template>
  <div class="edit-page">
    <h1 class="page-title">{{ editId ? '编辑文章' : '新建文章' }}</h1>

    <div class="error" v-if="error">{{ error }}</div>

    <form @submit.prevent="submit">
      <label>标题</label>
      <input v-model="title" type="text" placeholder="文章标题" />

      <label>摘要</label>
      <input v-model="summary" type="text" placeholder="简短描述" />

      <div class="label-row">
        <label>正文（Markdown）</label>
        <label class="upload-label">
          <input type="file" accept="image/*" @change="uploadImage" hidden />
          {{ uploading ? '上传中...' : '📷 插入图片' }}
        </label>
      </div>
      <textarea ref="textareaRef" v-model="content" rows="16" placeholder="Markdown 内容" />

      <label>新标签</label>
      <TagEditor v-model="newTags" />

      <button type="submit" class="submit-btn" :disabled="loading">
        {{ loading ? '保存中...' : '保存' }}
      </button>
    </form>
  </div>
</template>

<style scoped>
.edit-page { max-width: 800px; padding: 2rem 0; }
.page-title { font-size: 2rem; margin-bottom: 1.5rem; }
.error { background: #fed7d7; color: #c53030; padding: 0.6rem 1rem; border-radius: var(--radius); margin-bottom: 1rem; font-size: 0.9em; }
form { display: flex; flex-direction: column; gap: 0.8rem; }
.label-row { display: flex; justify-content: space-between; align-items: center; margin-top: 0.3rem; }
.upload-label { font-weight: 500; font-size: 0.85em; color: var(--link); cursor: pointer; }
.upload-label:hover { color: var(--link-hover); }
label { font-weight: 600; font-size: 0.9em; color: var(--text-secondary); }
input, textarea {
  padding: 0.65rem 0.8rem; border: 1px solid var(--border); border-radius: var(--radius);
  font-size: 0.95em; font-family: inherit; background: var(--bg); color: var(--text); outline: none;
}
input:focus, textarea:focus { border-color: var(--link); box-shadow: 0 0 0 2px rgba(177,45,108,0.08); }
.submit-btn { margin-top: 1rem; padding: 0.5rem 1.5rem; background: var(--text); color: var(--bg); border: none; border-radius: var(--radius); font-size: 0.9em; font-weight: 500; cursor: pointer; transition: opacity 0.2s; width: fit-content; }
.submit-btn:hover { opacity: 0.85; }
.submit-btn:disabled { opacity: 0.4; cursor: not-allowed; }
</style>

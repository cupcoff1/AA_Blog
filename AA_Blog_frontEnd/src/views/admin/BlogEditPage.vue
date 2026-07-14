<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import TagEditor from '@/components/TagEditor.vue'
import { getAdminBlog, createBlog, updateBlog, updateBlogTags } from '@/api/blog'
import { uploadImage } from '@/api/upload'
import '@/assets/editor.css'
import type { BlogCreateRequest, TagVO } from '@/models/types'

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

const handleUploadImage = async (e: Event) => {
  const file = (e.target as HTMLInputElement).files?.[0]
  if (!file) return
  uploading.value = true
  try {
    const { url } = await uploadImage(file)
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
    if (editId) {
      await updateBlog(editId, body)
      await updateBlogTags(editId, tagIds.value, newTags.value)
    } else {
      await createBlog(body)
    }
    router.push('/blog')
  } catch (e: unknown) { error.value = e instanceof Error ? e.message : '保存失败' }
  finally { loading.value = false }
}

onMounted(async () => {
  if (!editId) return
  loading.value = true
  try {
    const blog = await getAdminBlog(editId)
    title.value = blog.title; summary.value = blog.summary; content.value = blog.content
    tagIds.value = blog.tags?.map((t: TagVO) => t.id) || []
  } catch {
    error.value = '加载文章失败'
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <div class="edit-page">
    <h1 class="page-title">{{ editId ? '编辑文章' : '新建文章' }}</h1>

    <div class="error" v-if="error">{{ error }}</div>

    <form @submit.prevent="submit">
      <label for="blog-title">标题</label>
      <input id="blog-title" v-model="title" type="text" placeholder="文章标题" />

      <label for="blog-summary">摘要</label>
      <input id="blog-summary" v-model="summary" type="text" placeholder="简短描述" />

      <div class="label-row">
        <label for="blog-content">正文（Markdown）</label>
        <label class="upload-label">
          <input type="file" accept="image/*" @change="handleUploadImage" hidden />
          {{ uploading ? '上传中...' : '📷 插入图片' }}
        </label>
      </div>
      <textarea ref="textareaRef" id="blog-content" v-model="content" rows="16" placeholder="Markdown 内容" />

      <label>新标签</label>
      <TagEditor v-model="newTags" />

      <button type="submit" class="submit-btn" :disabled="loading">
        {{ loading ? '保存中...' : '保存' }}
      </button>
    </form>
  </div>
</template>



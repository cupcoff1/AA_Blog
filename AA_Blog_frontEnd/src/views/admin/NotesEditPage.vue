<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import TagEditor from '@/components/TagEditor.vue'
import { getAdminNote, createNote, updateNote, updateNoteTags } from '@/api/note'
import '@/assets/editor.css'
import type { NoteCreateRequest, TagVO } from '@/models/types'

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
    if (editId) {
      await updateNote(editId, body)
      await updateNoteTags(editId, tagIds.value, newTags.value)
    } else {
      await createNote(body)
    }
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

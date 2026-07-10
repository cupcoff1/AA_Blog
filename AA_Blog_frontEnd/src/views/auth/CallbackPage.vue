<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import api from '@/api/client'

const route = useRoute()
const router = useRouter()
const error = ref('')

onMounted(async () => {
  const code = route.query.code as string
  if (!code) { error.value = '缺少授权码'; return }
  try {
    const data = await api.get<{ name: string; avatar: string }>(`/auth/github/callback?code=${encodeURIComponent(code)}`)
    localStorage.setItem('commenter', JSON.stringify(data))
    router.replace('/guest')
  } catch (e: unknown) {
    error.value = e instanceof Error ? e.message : '授权失败'
  }
})
</script>

<template>
  <div class="callback-page">
    <p v-if="!error">登录中...</p>
    <p v-else class="err">{{ error }}</p>
  </div>
</template>

<style scoped>
.callback-page { display: flex; justify-content: center; align-items: center; min-height: 60vh; }
.err { color: #e53e3e; }
</style>

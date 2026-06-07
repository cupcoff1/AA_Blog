<script setup lang="ts">
import { ref, onMounted } from 'vue'
import api from '@/api/client'
import type { AboutVO } from '@/models/types'
import { marked } from 'marked'

const about = ref<AboutVO | null>(null)
const loading = ref(true)
const error = ref(false)

onMounted(async () => {
  try {
    about.value = await api.get('/about')
  } catch {
    error.value = true
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <div class="about-page">
    <div v-if="loading" class="state">加载中...</div>
    <div v-else-if="error" class="state error">加载失败</div>
    <div v-else-if="!about" class="state">暂无内容</div>

    <div v-else class="about-content">
      <div class="avatar" v-if="about.avatar">
        <img :src="about.avatar" :alt="about.nickname" />
      </div>
      <h1>{{ about.nickname }}</h1>
      <div class="bio" v-html="marked(about.bio)" />
    </div>
  </div>
</template>

<style scoped>
.about-page {
  padding: 3rem 0;
}

.state {
  text-align: center;
  color: var(--text-secondary);
  padding: 3rem 0;
}

.state.error {
  color: #e53e3e;
}

.about-content {
  text-align: center;
}

.avatar {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  overflow: hidden;
  margin: 0 auto 1.5rem;
}

.avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.bio {
  text-align: left;
  margin-top: 1rem;
  line-height: 1.9;
}
</style>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import api from '@/api/client'

const nickname = ref(''); const avatar = ref(''); const bio = ref('')
const skills = ref(''); const hobbies = ref(''); const location = ref(''); const socialLinks = ref('')
const loading = ref(false); const message = ref('')

const submit = async () => {
  loading.value = true; message.value = ''
  try {
    await api.put('/admin/about', {
      nickname: nickname.value, avatar: avatar.value, bio: bio.value,
      skills: skills.value, hobbies: hobbies.value, location: location.value,
      socialLinks: socialLinks.value
    })
    message.value = '保存成功'
  } catch (e: any) { message.value = e.message || '保存失败' }
  finally { loading.value = false }
}
onMounted(async () => {
  try {
    const a = await api.get('/about')
    nickname.value = a.nickname; avatar.value = a.avatar; bio.value = a.bio
    skills.value = a.skills || ''; hobbies.value = a.hobbies || ''
    location.value = a.location || ''; socialLinks.value = a.socialLinks || ''
  } catch {}
})
</script>
<template>
  <div class="edit-page">
    <h1>个人资料</h1>
    <div class="msg" v-if="message" :class="{ ok: message === '保存成功', err: message !== '保存成功' }">{{ message }}</div>
    <form @submit.prevent="submit">
      <label>昵称</label><input v-model="nickname" type="text" />
      <label>头像 URL</label><input v-model="avatar" type="text" placeholder="/uploads/avatars/xxx.jpg" />
      <label>位置</label><input v-model="location" type="text" placeholder="中国 · 学生" />
      <label>个人简介</label><textarea v-model="bio" rows="4" placeholder="简短介绍" />
      <label>技能（JSON 数组）</label><input v-model="skills" type="text" placeholder='["Java", "Spring Boot"]' />
      <label>爱好（JSON 数组）</label><input v-model="hobbies" type="text" placeholder='["coding", "music"]' />
      <label>社交链接（JSON）</label><input v-model="socialLinks" type="text" placeholder='{"github": "..."}' />
      <button type="submit" class="btn-submit" :disabled="loading">{{ loading ? '保存中...' : '保存' }}</button>
    </form>
  </div>
</template>
<style scoped>
.edit-page { max-width: 800px; padding: 1rem 0; }
.msg { padding: 0.6rem 1rem; border-radius: 6px; margin-bottom: 1rem; font-size: 0.9em; }
.msg.ok { background: #c6f6d5; color: #276749; }
.msg.err { background: #fed7d7; color: #c53030; }
form { display: flex; flex-direction: column; gap: 0.8rem; }
label { font-weight: 600; font-size: 0.95em; margin-top: 0.5rem; }
input, textarea { padding: 0.6rem 1rem; border: 1px solid var(--border); border-radius: 6px; font-size: 1em; font-family: inherit; background: var(--bg); color: var(--text); outline: none; }
input:focus, textarea:focus { border-color: var(--link); }
.btn-submit { margin-top: 1rem; padding: 0.7rem; background: var(--link); color: #fff; border: none; border-radius: 6px; font-size: 1em; cursor: pointer; }
.btn-submit:disabled { opacity: 0.6; cursor: not-allowed; }
</style>

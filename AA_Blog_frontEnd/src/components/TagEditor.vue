<script setup lang="ts">
import { ref } from 'vue'

const tags = defineModel<string[]>({ default: () => [] })
const input = ref('')

const add = () => {
  const name = input.value.trim()
  if (name && !tags.value.includes(name)) { tags.value.push(name); input.value = '' }
}
const remove = (name: string) => { tags.value = tags.value.filter(t => t !== name) }
</script>

<template>
  <div class="tag-area">
    <span v-for="t in tags" :key="t" class="tag-pill">
      {{ t }} <button type="button" @click="remove(t)">&times;</button>
    </span>
    <div class="tag-input">
      <input v-model="input" @keyup.enter.prevent="add" placeholder="输入标签，回车添加" />
      <button type="button" @click="add" class="tag-add">+</button>
    </div>
  </div>
</template>

<style scoped>
.tag-area { display: flex; flex-wrap: wrap; gap: 0.4rem; align-items: center; }
.tag-pill { display: flex; align-items: center; gap: 4px; background: var(--bg-secondary); padding: 3px 10px; border-radius: 20px; font-size: 0.85em; }
.tag-pill button { background: none; border: none; cursor: pointer; color: var(--text-secondary); font-size: 1em; padding: 0; line-height: 1; }
.tag-input { display: flex; gap: 0; }
.tag-input input { flex: 1; border-top-right-radius: 0; border-bottom-right-radius: 0; font-size: 0.85em; padding: 5px 8px; }
.tag-add { padding: 5px 12px; border: 1px solid var(--border); border-left: none; border-radius: 0 var(--radius) var(--radius) 0; background: var(--bg-secondary); cursor: pointer; color: var(--text-secondary); }
</style>

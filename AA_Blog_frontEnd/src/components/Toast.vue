<script setup lang="ts">
import { useToast } from '@/composables/toast'

const { toasts, remove } = useToast()
</script>

<template>
  <Teleport to="body">
    <div class="toast-container">
      <div
        v-for="t in toasts"
        :key="t.id"
        :class="['toast', t.type]"
        @click="remove(t.id)"
      >
        {{ t.message }}
      </div>
    </div>
  </Teleport>
</template>

<style scoped>
.toast-container {
  position: fixed; top: 1rem; right: 1rem; z-index: 9999;
  display: flex; flex-direction: column; gap: 0.5rem;
}
.toast {
  padding: 0.7rem 1.2rem; border-radius: var(--radius);
  font-size: 0.9em; color: #fff; cursor: pointer;
  animation: toast-in 0.3s ease;
  max-width: 360px; word-break: break-word;
}
.toast.error { background: var(--color-error); }
.toast.success { background: var(--color-success); }
@keyframes toast-in {
  from { opacity: 0; transform: translateY(-1rem); }
  to { opacity: 1; transform: translateY(0); }
}
</style>

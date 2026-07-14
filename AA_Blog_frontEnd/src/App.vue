<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import Sidebar from '@/components/Sidebar.vue'
import Toast from '@/components/Toast.vue'

const route = useRoute()
const isLogin = computed(() => route.path === '/admin/login')
</script>

<template>
  <div v-if="isLogin" class="layout-full">
    <router-view />
  </div>
  <div v-else class="layout">
    <Sidebar />
    <main class="main-content">
      <router-view v-slot="{ Component }">
        <keep-alive :include="['BlogListPage', 'NotesPage', 'ProjectsPage']">
          <component :is="Component" />
        </keep-alive>
      </router-view>
    </main>
  </div>
  <Toast />
</template>

<style scoped>
.layout-full { min-height: 100vh; }
.layout { min-height: 100vh; }
.main-content { padding: 1rem 1rem 0; max-width: 100%; overflow-x: hidden; }
@media screen and (min-width: 900px) {
  .layout {
    display: grid;
    grid-template-columns: var(--sidebar-width) 1fr;
    gap: 5rem;
    padding-left: 10rem;
  }
  .main-content { padding: 1rem 2rem 0; max-width: var(--max-width); }
}
</style>

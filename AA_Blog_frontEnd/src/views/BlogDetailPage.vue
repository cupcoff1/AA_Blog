<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute } from 'vue-router'
import { ChevronDown, ChevronRight } from '@lucide/vue'
import api from '@/api/client'
import type { BlogVO } from '@/models/types'
import { marked } from 'marked'
import { markedHighlight } from 'marked-highlight'
import hljs from 'highlight.js'
import DOMPurify from 'dompurify'
import 'highlight.js/styles/github-dark.min.css'

const route = useRoute()
const blog = ref<BlogVO | null>(null)
const loading = ref(true)
const error = ref(false)
const showToc = ref(true)
const renderedContent = computed(() =>
  blog.value ? DOMPurify.sanitize(marked(blog.value.content || '') as string) : ''
)

const loadUtterances = () => {
  const script = document.createElement('script')
  script.src = 'https://utteranc.es/client.js'
  script.setAttribute('repo', 'cupcoff1/AA_Blog')
  script.setAttribute('issue-term', 'pathname')
  script.setAttribute('theme', document.body.classList.contains('dark') ? 'github-dark' : 'github-light')
  script.setAttribute('crossorigin', 'anonymous')
  script.async = true
  const el = document.getElementById('utterances')
  if (el) { el.innerHTML = ''; el.appendChild(script) }
}
const headings = ref<{ id: string; text: string; level: number; open: boolean; children: { id: string; text: string }[] }[]>([])
const activeId = ref('')
let headingTimer: ReturnType<typeof setTimeout>

marked.use({ gfm: true, breaks: true })
marked.use(markedHighlight({
  langPrefix: 'hljs language-',
  highlight(code: string, lang: string) {
    if (lang && hljs.getLanguage(lang)) {
      return hljs.highlight(code, { language: lang }).value
    }
    return hljs.highlightAuto(code).value
  }
}))

const extractHeadings = () => {
  const els = document.querySelectorAll('.blog-content h2, .blog-content h3')
  const result: typeof headings.value = []
  let curH2: typeof headings.value[0] | null = null
  Array.from(els).forEach(el => {
    const id = (el.textContent || '').toLowerCase().replace(/[^\w一-鿿]+/g, '-').replace(/^-|-$/g, '')
    el.id = id
    if (el.tagName === 'H2') {
      curH2 = { id, text: el.textContent || '', level: 2, open: false, children: [] }
      result.push(curH2)
    } else if (curH2) {
      curH2.children.push({ id, text: el.textContent || '' })
    }
  })
  headings.value = result
}

const scrollTo = (id: string) => {
  document.getElementById(id)?.scrollIntoView({ behavior: 'smooth' })
}
const onScroll = () => {
  const hs = document.querySelectorAll('.blog-content h2, .blog-content h3')
  let current = ''
  hs.forEach(h => {
    const top = (h as HTMLElement).getBoundingClientRect().top
    if (top < 100) current = h.id
  })
  activeId.value = current
}

onMounted(async () => {
  try {
    blog.value = await api.get<BlogVO>(`/blog/${String(route.params.slug)}`)
    headingTimer = setTimeout(extractHeadings, 100)
    setTimeout(loadUtterances, 300)
  } catch {
    error.value = true
  } finally {
    loading.value = false
  }
  window.addEventListener('scroll', onScroll)
})
onUnmounted(() => {
  window.removeEventListener('scroll', onScroll)
  clearTimeout(headingTimer)
})


</script>

<template>
  <div v-if="loading" class="state">加载中...</div>
  <div v-else-if="error || !blog" class="state error">加载失败</div>

  <article v-else class="blog-detail">
    <div class="blog-layout">
      <div class="blog-main">
        <header class="blog-header">
          <h1>{{ blog.title }}</h1>
          <div class="blog-meta">
            <time>{{ blog.publishedAt?.split('T')[0] }}</time>
          </div>
        </header>

        <div class="blog-content" v-html="renderedContent" />

        <section id="comments" class="comments">
          <h2>Comments</h2>
          <div id="utterances" />
        </section>

      </div>

      <aside class="toc-sidebar" v-if="headings.length">
        <p class="toc-title" @click="showToc = !showToc">
          <ChevronDown v-if="showToc" :size="14" class="toc-chevron" />
          <ChevronRight v-else :size="14" class="toc-chevron" />
          目录
        </p>
        <nav v-show="showToc" class="toc-nav">
          <div v-for="h in headings" :key="h.id">
            <a :href="`#${h.id}`" class="toc-h2"
              :class="{ active: h.id === activeId }"
              @click.prevent="h.open = !h.open; scrollTo(h.id)">
              {{ h.open ? '▾' : '▸' }} {{ h.text }}
            </a>
            <template v-if="h.open">
              <a v-for="c in h.children" :key="c.id"
                :href="`#${c.id}`" class="toc-h3"
                :class="{ active: c.id === activeId }">
                {{ c.text }}
              </a>
            </template>
          </div>
        </nav>
      </aside>
    </div>
  </article>
</template>

<style scoped>
.blog-detail { padding: 2.5rem 0; }
@media screen and (max-width: 600px) {
  .blog-detail { padding: 1.5rem 0; }
  .blog-header h1 { font-size: 1.8rem; }
}

/* Layout */
.blog-layout { position: relative; }
.blog-main { max-width: var(--max-width); }

/* TOC */
.toc-sidebar {
  display: none;
}
@media screen and (min-width: 1400px) {
  .toc-sidebar {
    display: block;
    position: fixed; top: 4rem; right: 2rem;
    width: 180px; max-height: calc(100vh - 6rem);
    overflow-y: auto; scrollbar-width: none;
    -ms-overflow-style: none;
    border-left: 1px solid rgba(128,128,128,0.15);
    padding-left: 1rem;
  }
}
.toc-title { display: flex; align-items: center; gap: 4px; font-weight: 600; font-size: 0.85em; color: var(--text-secondary); text-transform: uppercase; letter-spacing: 0.05em; margin-bottom: 0.8em; cursor: pointer; user-select: none; }
.toc-chevron { flex-shrink: 0; }
.toc-nav { display: flex; flex-direction: column; gap: 2px; }
.toc-nav a { color: var(--text-secondary); font-size: 0.85em; line-height: 1.4; display: block; padding: 2px 0; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.toc-nav a:hover, .toc-nav a.active { color: var(--link); }
.toc-h2 { cursor: pointer; }
.toc-h3 { padding-left: 16px; font-size: 0.8em; }

/* Header */
.blog-header { margin-bottom: 2.5rem; }
.blog-header h1 { font-size: 2.8rem; line-height: 1.15; margin-bottom: 0.3em; }
.blog-meta {
  display: flex; align-items: center; gap: 0.6rem;
  color: var(--text-secondary); font-size: 0.95em; margin-bottom: 0.8em;
}
.blog-meta a { color: var(--text-secondary); }
.blog-meta a:hover { color: var(--link); }


/* Content */
.blog-content { line-height: 1.8; font-size: 1.05em; margin-bottom: 2.5rem; }
.blog-content :deep(a) { text-decoration: underline; text-decoration-thickness: 2px; text-decoration-style: dotted; text-underline-offset: 6px; }
.blog-content :deep(a:hover) { text-decoration-color: var(--link); }

/* Comments */
.comments { border-top: 1px solid var(--border); padding-top: 2rem; margin-top: 3rem; }
.comments h2 { font-size: 1.5em; margin-bottom: 1rem; font-weight: 600; color: var(--text); }

.state { text-align: center; color: var(--text-secondary); padding: 4rem 0; }
.state.error { color: #e53e3e; }
</style>

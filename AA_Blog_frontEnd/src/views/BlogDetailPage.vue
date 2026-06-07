<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import api from '@/api/client'
import type { BlogVO, CommentVO } from '@/models/types'
import { marked } from 'marked'
import hljs from 'highlight.js'
import 'highlight.js/styles/github-dark.min.css'

const route = useRoute()
const blog = ref<BlogVO | null>(null)
const comments = ref<CommentVO[]>([])
const loading = ref(true)
const error = ref(false)
const headings = ref<{ id: string; text: string; level: number }[]>([])
const activeId = ref('')

marked.setOptions({
  gfm: true,
  breaks: true,
  highlight(code: string, lang: string) {
    if (lang && hljs.getLanguage(lang)) {
      return hljs.highlight(code, { language: lang }).value
    }
    return hljs.highlightAuto(code).value
  }
})

const extractHeadings = () => {
  const els = document.querySelectorAll('.blog-content h2, .blog-content h3')
  headings.value = Array.from(els).map(el => {
    const id = (el.textContent || '').toLowerCase().replace(/[^\w一-鿿]+/g, '-').replace(/^-|-$/g, '')
    el.id = id
    return { id, text: el.textContent || '', level: el.tagName === 'H2' ? 2 : 3 }
  })
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
    const slug = route.params.slug as string
    const [blogData, commentData] = await Promise.all([
      api.get(`/blog/${slug}`),
      api.get(`/blog/${slug}/comments`)
    ])
    blog.value = blogData
    comments.value = commentData
    setTimeout(extractHeadings, 100)
  } catch {
    error.value = true
  } finally {
    loading.value = false
  }
  window.addEventListener('scroll', onScroll)
})

const scrollToComments = () => {
  document.getElementById('comments')?.scrollIntoView({ behavior: 'smooth' })
}
</script>

<template>
  <div v-if="loading" class="state">加载中...</div>
  <div v-else-if="error" class="state error">加载失败</div>
  <div v-else-if="!blog" class="state">文章不存在</div>

  <article v-else class="blog-detail">
    <div class="blog-layout">
      <div class="blog-main">
        <header class="blog-header">
          <h1>{{ blog.title }}</h1>
          <div class="blog-meta">
            <time>{{ blog.publishedAt?.split('T')[0] }}</time>
            <span class="divider">|</span>
            <a href="#comments" @click.prevent="scrollToComments">
              {{ comments.length ? `${comments.length} 条评论` : '暂无评论' }}
            </a>
          </div>
        </header>

        <div class="blog-content" v-html="marked(blog.content || '')" />

      </div>

      <aside class="toc-sidebar" v-if="headings.length">
        <p class="toc-title">目录</p>
        <nav class="toc-nav">
          <a v-for="h in headings" :key="h.id"
            :href="`#${h.id}`"
            :class="{ active: h.id === activeId, 'toc-h3': h.level === 3 }"
            >
            {{ h.text }}
          </a>
        </nav>
      </aside>
    </div>

    <section id="comments" class="comments">
      <h2>Comments</h2>
      <div v-if="comments.length">
        <div v-for="comment in comments" :key="comment.id" class="comment">
          <img :src="comment.author_avatar" class="comment-avatar" alt="" />
          <div class="comment-body">
            <div class="comment-header">
              <strong>{{ comment.author_name }}</strong>
              <time>{{ comment.created_at }}</time>
            </div>
            <div v-html="marked(comment.content)" />
            <div v-if="comment.children?.length" class="replies">
              <div v-for="reply in comment.children" :key="reply.id" class="comment reply">
                <img :src="reply.author_avatar" class="comment-avatar" alt="" />
                <div class="comment-body">
                  <div class="comment-header">
                    <strong>{{ reply.author_name }}</strong>
                    <time>{{ reply.created_at }}</time>
                  </div>
                  <div v-html="marked(reply.content)" />
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <p v-else class="no-comments">还没有评论 (」&#65439;ロ&#65439;)」</p>
    </section>
  </article>
</template>

<style scoped>
.blog-detail { padding: 2.5rem 0; }

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
    position: fixed; top: 6rem; right: 2rem;
    width: 180px;
    border-left: 1px solid rgba(128,128,128,0.15);
    padding-left: 1rem;
  }
}
.toc-title { font-weight: 600; font-size: 0.85em; color: var(--text-secondary); text-transform: uppercase; letter-spacing: 0.05em; margin-bottom: 0.8em; }
.toc-nav { display: flex; flex-direction: column; gap: 4px; }
.toc-nav a { color: var(--text-secondary); font-size: 0.85em; line-height: 1.4; display: block; padding: 2px 0; }
.toc-nav a:hover, .toc-nav a.active { color: var(--link); }
.toc-nav a.toc-h3 { padding-left: 12px; font-size: 0.8em; }

/* Header */
.blog-header { margin-bottom: 2.5rem; }
.blog-header h1 { font-size: 2.8rem; line-height: 1.15; margin-bottom: 0.3em; }
.blog-meta {
  display: flex; align-items: center; gap: 0.6rem;
  color: var(--text-secondary); font-size: 0.95em; margin-bottom: 0.8em;
}
.blog-meta a { color: var(--text-secondary); }
.blog-meta a:hover { color: var(--link); }
.divider { opacity: 0.4; }

/* Tags as buttons */
.tags { display: flex; gap: 0.4rem; flex-wrap: wrap; }
/* Content */
.blog-content { line-height: 1.8; font-size: 1.05em; margin-bottom: 2.5rem; }
.blog-content :deep(a) { text-decoration: underline; text-decoration-thickness: 2px; text-decoration-style: dotted; text-underline-offset: 6px; }
.blog-content :deep(a:hover) { text-decoration-color: var(--link); }

/* Comments */
.comments { border-top: 1px solid var(--border); padding-top: 1.5rem; }
.comments h2 { font-size: 1.5em; margin-bottom: 1rem; }
.comment { display: flex; gap: 0.8rem; margin-bottom: 1.2rem; }
.comment-avatar { width: 36px; height: 36px; border-radius: 50%; }
.comment-body { flex: 1; }
.comment-header { display: flex; gap: 0.8rem; align-items: center; margin-bottom: 0.2em; font-size: 0.92em; }
.comment-header time { font-size: 0.85em; color: var(--text-secondary); }
.comment-body :deep(p) { margin-bottom: 0.3em; font-size: 0.95em; }
.replies { margin-top: 0.8rem; margin-left: 1rem; padding-left: 1rem; border-left: 2px solid var(--border); }

.state { text-align: center; color: var(--text-secondary); padding: 4rem 0; }
.state.error { color: #e53e3e; }
.no-comments { color: var(--text-secondary); font-size: 0.95em; }
</style>

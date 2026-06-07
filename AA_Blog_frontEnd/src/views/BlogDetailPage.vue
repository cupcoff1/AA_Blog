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

marked.setOptions({
  highlight(code: string, lang: string) {
    if (lang && hljs.getLanguage(lang)) {
      return hljs.highlight(code, { language: lang }).value
    }
    return hljs.highlightAuto(code).value
  }
})

onMounted(async () => {
  try {
    const slug = route.params.slug as string
    const [blogData, commentData] = await Promise.all([
      api.get(`/blog/${slug}`),
      api.get(`/blog/${slug}/comments`)
    ])
    blog.value = blogData
    comments.value = commentData
  } catch {
    error.value = true
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <div v-if="loading" class="state">加载中...</div>
  <div v-else-if="error" class="state error">加载失败</div>
  <div v-else-if="!blog" class="state">文章不存在</div>

  <article v-else class="blog-detail">
    <header class="blog-header">
      <h1>{{ blog.title }}</h1>
      <div class="blog-meta">
        <time>{{ blog.publishedAt?.split('T')[0] }}</time>
        <div class="tags" v-if="blog.tags?.length">
          <RouterLink v-for="tag in blog.tags" :key="tag.id"
            :to="`/blog?tag=${tag.slug}`" class="tag">{{ tag.name }}</RouterLink>
        </div>
      </div>
    </header>

    <div class="blog-content" v-html="marked(blog.content || '')" />

    <nav class="post-nav">
      <RouterLink v-if="blog.prev" :to="`/blog/${blog.prev.slug}`" class="prev">
        &larr; {{ blog.prev.title }}
      </RouterLink>
      <span v-else class="prev" />
      <RouterLink v-if="blog.next" :to="`/blog/${blog.next.slug}`" class="next">
        {{ blog.next.title }} &rarr;
      </RouterLink>
      <span v-else class="next" />
    </nav>

    <section class="comments">
      <h2>评论 ({{ comments.length }})</h2>
      <div v-if="comments.length">
        <div v-for="comment in comments" :key="comment.id" class="comment">
          <div class="comment-avatar">
            <img :src="comment.author_avatar" alt="" />
          </div>
          <div class="comment-body">
            <div class="comment-header">
              <strong>{{ comment.author_name }}</strong>
              <time>{{ comment.created_at }}</time>
            </div>
            <div class="comment-content" v-html="marked(comment.content)" />
            <div v-if="comment.children?.length" class="replies">
              <div v-for="reply in comment.children" :key="reply.id" class="comment reply">
                <div class="comment-avatar">
                  <img :src="reply.author_avatar" alt="" />
                </div>
                <div class="comment-body">
                  <div class="comment-header">
                    <strong>{{ reply.author_name }}</strong>
                    <time>{{ reply.created_at }}</time>
                  </div>
                  <div class="comment-content" v-html="marked(reply.content)" />
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <p v-else class="no-comments">暂无评论</p>
    </section>
  </article>
</template>

<style scoped>
.blog-detail {
  padding: 2rem 0;
}

.blog-header {
  margin-bottom: 2rem;
}

.blog-header h1 {
  margin-bottom: 0.5rem;
}

.blog-meta {
  display: flex;
  gap: 1rem;
  align-items: center;
  color: var(--text-secondary);
  font-size: 0.95em;
}

.tags {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
}

.tag {
  font-size: 0.8em;
  color: var(--text-secondary);
  background: var(--bg-secondary);
  padding: 2px 8px;
  border-radius: 4px;
}

.blog-content {
  line-height: 1.9;
  margin-bottom: 2rem;
}

.post-nav {
  display: flex;
  justify-content: space-between;
  padding: 1.5rem 0;
  border-top: 1px solid var(--border);
  margin-bottom: 2rem;
}

.state {
  text-align: center;
  color: var(--text-secondary);
  padding: 3rem 0;
}

.state.error {
  color: #e53e3e;
}

/* 评论 */
.comments {
  border-top: 1px solid var(--border);
  padding-top: 1.5rem;
}

.comment {
  display: flex;
  gap: 0.8rem;
  margin-bottom: 1.2rem;
}

.comment-avatar img {
  width: 36px;
  height: 36px;
  border-radius: 50%;
}

.comment-body {
  flex: 1;
}

.comment-header {
  display: flex;
  gap: 0.8rem;
  align-items: center;
  margin-bottom: 0.3rem;
}

.comment-header time {
  font-size: 0.85em;
  color: var(--text-secondary);
}

.comment-content {
  font-size: 0.95em;
}

.replies {
  margin-top: 0.8rem;
  margin-left: 1rem;
  padding-left: 1rem;
  border-left: 2px solid var(--border);
}

.no-comments {
  color: var(--text-secondary);
  font-size: 0.95em;
  margin-top: 0.5rem;
}
</style>

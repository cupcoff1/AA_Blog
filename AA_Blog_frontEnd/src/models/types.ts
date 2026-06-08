// ==================== 通用 ====================
export interface Result<T> {
  code: number
  message: string
  data: T
}

// ==================== Blog ====================
export interface BlogListVO {
  id: number
  title: string
  slug: string
  summary: string
  publishedAt: string
  tags: TagVO[]
}

export interface BlogVO extends BlogListVO {
  content: string
  prev: { title: string; slug: string } | null
  next: { title: string; slug: string } | null
}

export interface BlogCreateRequest {
  title: string
  summary: string
  content: string
  tagIds?: number[]
  newTags?: string[]
}

// ==================== Notes ====================
export interface NotesVO {
  id: number
  title: string
  slug: string
  content: string
  publishedAt: string
  tags: TagVO[]
}

export interface NotesCreateRequest {
  title: string
  content: string
  tagIds?: number[]
  newTags?: string[]
}

// ==================== Projects ====================
export interface ProjectsVO {
  id: number
  name: string
  slug: string
  description: string
  demoUrl: string
  githubUrl: string
  tags: TagVO[]
}

export interface ProjectsCreateRequest {
  name: string
  description: string
  demoUrl?: string
  githubUrl?: string
  tagIds?: number[]
  newTags?: string[]
}

// ==================== Tags ====================
export interface TagVO {
  id: number
  name: string
  slug: string
}

// ==================== Comments ====================
export interface CommentVO {
  id: number
  content: string
  parent_id: number | null
  author_name: string
  author_avatar: string
  created_at: string
  children: CommentVO[]
}

export interface CommentAdminVO {
  id: number
  content: string
  parent_id: number | null
  author_name: string
  blog_id: number
  blog_title: string
  created_at: string
}

// ==================== About ====================
export interface AboutVO {
  nickname: string
  avatar: string
  bio: string
  skills: string
  hobbies: string
  location: string
  socialLinks: string
}

// ==================== StickyNote ====================
export interface StickyNoteVO {
  id: number
  content: string
  color: string
  rotate: number
  custom: boolean
}

// ==================== Hero ====================
export interface HeroQuoteVO {
  id: number
  content: string
  author: string
  source: string
}

export interface HeroConfigVO {
  heroLight: string
  heroDark: string
}

// ==================== Auth ====================
export interface GitHubUser {
  name: string
  avatar: string
}

// ==================== Dashboard ====================
export interface HomeVO {
  about: AboutVO
  latestBlogs: BlogListVO[]
  latestNotes: NotesVO[]
  latestProjects: ProjectsVO[]
}

export interface DashboardVO {
  blogCount: number
  noteCount: number
  projectCount: number
  commentCount: number
  tags: TagStatVO[]
  recentComments: CommentAdminVO[]
}

export interface TagStatVO {
  name: string
  slug: string
  blogCount: number
  noteCount: number
  projectCount: number
}

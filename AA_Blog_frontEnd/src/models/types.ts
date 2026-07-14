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
export interface NoteVO {
  id: number
  title: string
  slug: string
  content: string
  publishedAt: string
  tags: TagVO[]
}

export interface NoteCreateRequest {
  title: string
  content: string
  tagIds?: number[]
  newTags?: string[]
}

// ==================== Projects ====================
export interface ProjectVO {
  id: number
  name: string
  slug: string
  description: string
  demoUrl: string
  githubUrl: string
  tags: TagVO[]
}

export interface ProjectCreateRequest {
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

// ==================== StickyNote ====================
export interface StickyNoteVO {
  id: number
  content: string
  color: string
  rotate: number
  category: string
  authorName: string
  authorAvatar: string
  own: boolean
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
export interface LoginRequest {
  username: string
  password: string
}

export interface ChangePasswordRequest {
  oldPassword: string
  newPassword: string
}

export interface GitHubUser {
  name: string
  avatar: string
}

// ==================== Home ====================
export interface HomeVO {
  latestBlogs: BlogListVO[]
  latestNotes: NoteVO[]
  latestProjects: ProjectVO[]
}



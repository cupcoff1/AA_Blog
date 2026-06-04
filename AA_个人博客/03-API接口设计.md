# 个人博客 - API 接口设计

## 1. 约定

### 1.1 统一响应格式

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

| code | 含义 |
|---|---|
| 200 | 成功 |
| 400 | 参数错误 |
| 401 | 未认证 |
| 403 | 无权限 |
| 404 | 资源不存在 |
| 500 | 服务器错误 |

### 1.2 认证方式

| 角色 | 方式 |
|---|---|
| 评论者 | Header `Authorization: Bearer <github_token>` |
| 管理员 | Header `Authorization: Bearer <jwt_token>` |

### 1.3 接口前缀

| 类型 | 前缀 |
|---|---|
| 前台公开 | `/api` |
| 管理员后台 | `/api/admin` |

---

## 2. 前台接口（无需认证）

### 2.1 首页

**GET** `/api/home`

返回：

```json
{
  "about": {
    "nickname": "AA",
    "avatar": "/uploads/avatar.jpg",
    "bio": "A passionate developer...",
    "skills": ["Java", "Spring Boot", "Vue"],
    "social_links": {
      "github": "https://github.com/cupcoff1",
      "email": "aa@example.com"
    }
  },
  "latest_blogs": [
    {
      "id": 1,
      "title": "...",
      "slug": "...",
      "summary": "...",
      "published_at": "2026-06-01",
      "tags": [{"id": 1, "name": "Java", "slug": "java"}]
    }
  ],
  "latest_notes": [
    {
      "id": 1,
      "title": "...",
      "slug": "...",
      "published_at": "2026-06-03",
      "tags": [{"id": 1, "name": "Spring", "slug": "spring"}]
    }
  ],
  "latest_projects": [
    {
      "id": 1,
      "name": "...",
      "slug": "...",
      "description": "...",
      "demo_url": "...",
      "github_url": "...",
      "tags": [{"id": 1, "name": "Vue", "slug": "vue"}]
    }
  ]
}
```

### 2.2 Blog

**GET** `/api/blog?q={keyword}&tag={tag_slug}`

查询参数可选，返回数组：

```json
{
  "list": [
    {
      "id": 1,
      "title": "...",
      "slug": "...",
      "summary": "...",
      "published_at": "2026-06-01",
      "tags": [{"id": 1, "name": "Java", "slug": "java"}]
    }
  ]
}
```

**GET** `/api/blog/{slug}`

```json
{
  "id": 1,
  "title": "...",
  "slug": "...",
  "summary": "...",
  "content": "# Markdown content...",
  "published_at": "2026-06-01",
  "tags": [{"id": 1, "name": "Java", "slug": "java"}],
  "prev": {"title": "...", "slug": "..."},
  "next": {"title": "...", "slug": "..."}
}
```

### 2.3 Blog 评论

**GET** `/api/blog/{slug}/comments`

返回嵌套结构：

```json
{
  "list": [
    {
      "id": 1,
      "content": "Great post!",
      "parent_id": null,
      "author_name": "cupcoff1",
      "author_avatar": "https://avatars.githubusercontent.com/...",
      "created_at": "2026-06-05 10:30:00",
      "children": [
        {
          "id": 2,
          "content": "Thanks!",
          "parent_id": 1,
          "author_name": "AA",
          "author_avatar": "...",
          "created_at": "2026-06-05 10:35:00",
          "children": []
        }
      ]
    }
  ]
}
```

### 2.4 Notes

**GET** `/api/notes?q={keyword}&tag={tag_slug}`

```json
{
  "list": [
    {
      "id": 1,
      "title": "...",
      "slug": "...",
      "content": "# Full markdown content...",
      "published_at": "2026-06-03",
      "tags": [{"id": 1, "name": "Spring", "slug": "spring"}]
    }
  ]
}
```

### 2.5 Projects

**GET** `/api/projects`

按 id DESC 排序，返回：

```json
{
  "list": [
    {
      "id": 1,
      "name": "...",
      "slug": "...",
      "description": "...",
      "demo_url": "...",
      "github_url": "...",
      "tags": [{"id": 1, "name": "Vue", "slug": "vue"}]
    }
  ]
}
```

**GET** `/api/projects/{slug}`

```json
{
  "id": 1,
  "name": "...",
  "slug": "...",
  "description": "...",
  "content": "# Full markdown...",
  "demo_url": "...",
  "github_url": "...",
  "tags": [{"id": 1, "name": "Vue", "slug": "vue"}]
}
```

### 2.6 About Me

**GET** `/api/about`

```json
{
  "nickname": "AA",
  "avatar": "/uploads/avatar.jpg",
  "bio": "...",
  "skills": ["Java", "Spring Boot"],
  "social_links": {"github": "...", "email": "..."}
}
```

### 2.7 标签列表

**GET** `/api/tags`

```json
{
  "list": [
    {"id": 1, "name": "Java", "slug": "java"}
  ]
}
```

---

## 3. GitHub OAuth 认证

### 3.1 获取授权 URL

**GET** `/api/auth/github/url`

```json
{
  "url": "https://github.com/login/oauth/authorize?client_id=xxx&..."
}
```

### 3.2 授权回调

**GET** `/api/auth/github/callback?code={code}`

后端用 code 换取 access_token，获取用户信息，签发评论者 token。

```json
{
  "token": "github_xxx...",
  "user": {
    "name": "cupcoff1",
    "avatar": "https://avatars.githubusercontent.com/..."
  }
}
```

---

## 4. 评论者接口

> Header: `Authorization: Bearer <token>`

### 4.1 发表评论

**POST** `/api/blog/{slug}/comments`

```json
{
  "content": "Markdown content...",
  "parent_id": null
}
```

返回创建的评论对象。

### 4.2 删除自己的评论

**DELETE** `/api/comments/{id}`

返回 200。code 403 如评论不属于当前用户。如有子回复则级联删除。

---

## 5. 管理员接口

> Header: `Authorization: Bearer <jwt_token>`

### 5.1 认证

**POST** `/api/admin/login`

```json
{
  "username": "admin",
  "password": "password123"
}
```

```json
{
  "token": "eyJ...",
  "refresh_token": "eyJ..."
}
```

**POST** `/api/admin/refresh`

```json
{
  "refresh_token": "eyJ..."
}
```

```json
{
  "token": "eyJ..."
}
```

### 5.2 仪表盘

**GET** `/api/admin/dashboard`

```json
{
  "blog_count": 10,
  "note_count": 25,
  "project_count": 6,
  "comment_count": 15,
  "recent_comments": [
    {
      "id": 1,
      "content": "...",
      "author_name": "...",
      "blog_title": "...",
      "created_at": "..."
    }
  ]
}
```

### 5.3 Blog 管理

**GET** `/api/admin/blog`

返回所有文章（id DESC）：

```json
{
  "list": [
    {
      "id": 1,
      "title": "...",
      "slug": "...",
      "summary": "...",
      "published_at": "2026-06-01",
      "tags": [{"id": 1, "name": "Java"}]
    }
  ]
}
```

**POST** `/api/admin/blog`

```json
{
  "title": "...",
  "summary": "...",
  "content": "# Markdown...",
  "tag_ids": [1, 2],
  "new_tags": ["新标签名"]
}
```

slug 后端自动生成，published_at 自动取当前时间。

**PUT** `/api/admin/blog/{id}`

参数同 POST。

**DELETE** `/api/admin/blog/{id}`

> 同时删除关联的 blog_tags 记录和该文章下的所有评论。

**GET** `/api/admin/blog/{id}`

返回单篇文章完整数据，供编辑页填充表单。

```json
{
  "id": 1,
  "title": "...",
  "summary": "...",
  "content": "# Markdown...",
  "tags": [{"id": 1, "name": "Java"}]
}
```

### 5.4 Notes 管理

**GET** `/api/admin/notes`

返回所有笔记（id DESC）：

```json
{
  "list": [
    {
      "id": 1,
      "title": "...",
      "slug": "...",
      "published_at": "2026-06-03",
      "tags": [{"id": 1, "name": "Spring"}]
    }
  ]
}
```

**POST** `/api/admin/notes`

```json
{
  "title": "...",
  "content": "# Markdown...",
  "tag_ids": [],
  "new_tags": []
}
```

**PUT** `/api/admin/notes/{id}`

参数同 POST。

**DELETE** `/api/admin/notes/{id}`

> 同时删除关联的 notes_tags 记录。

**GET** `/api/admin/notes/{id}`

返回单条笔记完整数据，供编辑页填充表单。

```json
{
  "id": 1,
  "title": "...",
  "content": "# Markdown...",
  "tags": [{"id": 1, "name": "Spring"}]
}
```

### 5.5 Projects 管理

**GET** `/api/admin/projects`

返回所有项目（id DESC）：

```json
{
  "list": [
    {
      "id": 1,
      "name": "...",
      "slug": "...",
      "description": "...",
      "demo_url": "...",
      "github_url": "...",
      "tags": [{"id": 1, "name": "Vue"}]
    }
  ]
}
```

**POST** `/api/admin/projects`

```json
{
  "name": "...",
  "description": "...",
  "content": "# Markdown...",
  "demo_url": "...",
  "github_url": "...",
  "tag_ids": [],
  "new_tags": []
}
```

**PUT** `/api/admin/projects/{id}`

参数同 POST。

**DELETE** `/api/admin/projects/{id}`

> 同时删除关联的 projects_tags 记录。

**GET** `/api/admin/projects/{id}`

返回单个项目完整数据，供编辑页填充表单。

```json
{
  "id": 1,
  "name": "...",
  "description": "...",
  "content": "# Markdown...",
  "demo_url": "...",
  "github_url": "...",
  "tags": [{"id": 1, "name": "Vue"}]
}
```

### 5.6 标签管理

**GET** `/api/admin/tags`

**POST** `/api/admin/tags`

```json
{
  "name": "..."
}
```

**PUT** `/api/admin/tags/{id}`

```json
{
  "name": "..."
}
```

**DELETE** `/api/admin/tags/{id}`

> 删除前校验：如果标签有关联内容，拒绝删除，返回 400。

### 5.7 评论管理

**GET** `/api/admin/comments`

```json
{
  "list": [
    {
      "id": 1,
      "content": "...",
      "parent_id": null,
      "author_name": "...",
      "blog_id": 1,
      "blog_title": "...",
      "created_at": "..."
    }
  ]
}
```

**DELETE** `/api/admin/comments/{id}`

> 级联删除所有子回复。

### 5.8 About Me 管理

**PUT** `/api/admin/about`

```json
{
  "nickname": "AA",
  "avatar": "/uploads/avatar.jpg",
  "bio": "...",
  "skills": ["Java", "Spring Boot"],
  "social_links": {"github": "..."}
}
```

### 5.9 文件上传

**POST** `/api/admin/upload`

> Content-Type: multipart/form-data

| 字段 | 说明 |
|---|---|
| file | 图片文件（jpg/png/webp，≤ 2MB） |

```json
{
  "url": "/uploads/avatar_20260605.jpg"
}
```

---

## 6. 接口清单汇总

| 方法 | 路径 | 认证 | 说明 |
|---|---|---|---|
| GET | `/api/home` | 无 | 首页数据 |
| GET | `/api/blog` | 无 | Blog 列表 + 搜索 |
| GET | `/api/blog/{slug}` | 无 | Blog 详情 |
| GET | `/api/blog/{slug}/comments` | 无 | 文章评论列表 |
| GET | `/api/notes` | 无 | Notes 列表 + 搜索 |
| GET | `/api/projects` | 无 | 项目列表 |
| GET | `/api/projects/{slug}` | 无 | 项目详情 |
| GET | `/api/about` | 无 | 个人资料 |
| GET | `/api/tags` | 无 | 标签列表 |
| GET | `/api/auth/github/url` | 无 | GitHub 授权 URL |
| GET | `/api/auth/github/callback` | 无 | GitHub 回调 |
| POST | `/api/blog/{slug}/comments` | 评论者 | 发表评论 |
| DELETE | `/api/comments/{id}` | 评论者 | 删除自己的评论 |
| POST | `/api/admin/login` | 无 | 管理员登录 |
| POST | `/api/admin/refresh` | 无 | 刷新 Token |
| GET | `/api/admin/dashboard` | 管理员 | 仪表盘统计 |
| GET | `/api/admin/blog` | 管理员 | Blog 管理列表 |
| POST | `/api/admin/blog` | 管理员 | 创建文章 |
| PUT | `/api/admin/blog/{id}` | 管理员 | 编辑文章 |
| DELETE | `/api/admin/blog/{id}` | 管理员 | 删除文章 |
| GET | `/api/admin/blog/{id}` | 管理员 | 获取单篇文章 |
| GET | `/api/admin/notes` | 管理员 | Notes 管理列表 |
| POST | `/api/admin/notes` | 管理员 | 创建笔记 |
| PUT | `/api/admin/notes/{id}` | 管理员 | 编辑笔记 |
| DELETE | `/api/admin/notes/{id}` | 管理员 | 删除笔记 |
| GET | `/api/admin/notes/{id}` | 管理员 | 获取单条笔记 |
| GET | `/api/admin/projects` | 管理员 | 项目管理列表 |
| POST | `/api/admin/projects` | 管理员 | 创建项目 |
| PUT | `/api/admin/projects/{id}` | 管理员 | 编辑项目 |
| DELETE | `/api/admin/projects/{id}` | 管理员 | 删除项目 |
| GET | `/api/admin/projects/{id}` | 管理员 | 获取单个项目 |
| GET | `/api/admin/tags` | 管理员 | 标签管理列表 |
| POST | `/api/admin/tags` | 管理员 | 创建标签 |
| PUT | `/api/admin/tags/{id}` | 管理员 | 编辑标签 |
| DELETE | `/api/admin/tags/{id}` | 管理员 | 删除标签 |
| GET | `/api/admin/comments` | 管理员 | 评论管理列表 |
| DELETE | `/api/admin/comments/{id}` | 管理员 | 删除评论 |
| PUT | `/api/admin/about` | 管理员 | 编辑个人资料 |
| POST | `/api/admin/upload` | 管理员 | 上传文件 |

# 个人博客 - API 接口设计

## 1. 约定

### 1.1 统一响应格式

```json
{
  "code": 200,
  "message": "操作成功",
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
| 评论者 | Header `Authorization: Bearer <commenter_token>` |
| 管理员 | Header `Authorization: Bearer <admin_token>` |

管理员接口 `/api/admin/**` 由 JwtInterceptor 统一拦截（排除 `/api/admin/login` 和 `/api/admin/refresh`）。

### 1.3 接口前缀

| 类型 | 前缀 |
|---|---|
| 前台公开 | `/api` |
| 管理员后台 | `/api/admin` |

---

## 2. 前台接口（无需认证）

### 2.1 首页

**GET** `/api/home`

返回 `HomeVO`：

```json
{
  "about": { "nickname": "AA_", "avatar": "...", "bio": "...", "skills": "...", "hobbies": "...", "location": "...", "socialLinks": "..." },
  "latestBlogs": [ { "id": 1, "title": "...", "slug": "...", "summary": "...", "publishedAt": "...", "tags": [...] } ],
  "latestNotes": [ { "id": 1, "title": "...", "slug": "...", "content": "...", "publishedAt": "...", "tags": [...] } ],
  "latestProjects": [ { "id": 1, "name": "...", "slug": "...", "description": "...", "demoUrl": "...", "githubUrl": "...", "tags": [...] } ]
}
```

### 2.2 引语

**GET** `/api/hero-quotes`

返回 `List<HeroQuote>`：

```json
[
  { "id": 1, "content": "真正重要的东西，眼睛是看不见的", "author": "安托万·德·圣-埃克苏佩里", "source": "小王子" }
]
```

### 2.3 Hero 图片配置

**GET** `/api/hero-config`

返回 `{ heroLight, heroDark }`：

```json
{
  "heroLight": "/uploads/hero/hero-light.png",
  "heroDark": "/hero.jpg"
}
```

有自定义上传图则返回 uploads 路径，否则返回默认路径。

### 2.4 Blog

**GET** `/api/blog?q={keyword}&tag={tag_slug}`

查询参数可选，返回 `List<BlogListVO>`。

**GET** `/api/blog/{slug}`

返回 `BlogVO`（含 content、prev、next 导航）。

### 2.5 Blog 评论

**GET** `/api/blog/{slug}/comments`

返回 `List<CommentVO>`，嵌套 `children` 结构。

### 2.6 Notes

**GET** `/api/notes?q={keyword}&tag={tag_slug}`

返回 `List<NotesVO>`（含全文 content）。

### 2.7 Projects

**GET** `/api/projects`

返回 `List<ProjectsVO>`，按 id DESC。

### 2.8 About Me

**GET** `/api/about`

返回 `AboutVO`。

### 2.9 标签列表

**GET** `/api/tags`

返回 `List<TagVO>`。

### 2.10 便签

**GET** `/api/sticky-notes`

返回 `List<StickyNoteVO>`。

---

## 3. GitHub OAuth 认证

### 3.1 获取授权 URL

**GET** `/api/auth/github/url`

```json
{ "url": "https://github.com/login/oauth/authorize?..." }
```

### 3.2 授权回调

**GET** `/api/auth/github/callback?code={code}`

```json
{
  "token": "eyJ...",
  "user": { "name": "cupcoff1", "avatar": "https://avatars.githubusercontent.com/..." }
}
```

---

## 4. 评论者接口

> Header: `Authorization: Bearer <commenter_token>`

### 4.1 发表评论

**POST** `/api/blog/{slug}/comments`

```json
{ "content": "...", "parentId": null }
```

### 4.2 删除自己的评论

**DELETE** `/api/comments/{id}`

---

## 5. 管理员接口

> Header: `Authorization: Bearer <admin_token>`

### 5.1 认证

**POST** `/api/admin/login`

```json
{ "username": "AA_", "password": "123456" }
→ { "token": "eyJ..." }
```

**POST** `/api/admin/refresh`

> Header: `Authorization: Bearer <old_token>`
→ `{ "token": "eyJ..." }`

**PUT** `/api/admin/password`

```json
{ "oldPassword": "...", "newPassword": "..." }
```

### 5.2 Blog 管理

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/admin/blog` | 列表 |
| GET | `/api/admin/blog/{id}` | 详情 |
| POST | `/api/admin/blog` | 创建 |
| PUT | `/api/admin/blog/{id}` | 更新 |
| DELETE | `/api/admin/blog/{id}` | 删除（含关联标签和评论） |

POST/PUT body：`BlogCreateRequest { title, summary, content, tagIds?, newTags? }`

### 5.3 Notes 管理

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/admin/notes` | 列表 |
| GET | `/api/admin/notes/{id}` | 详情 |
| POST | `/api/admin/notes` | 创建 |
| PUT | `/api/admin/notes/{id}` | 更新 |
| DELETE | `/api/admin/notes/{id}` | 删除（含关联标签） |

POST/PUT body：`NotesCreateRequest { title, content, tagIds?, newTags? }`

### 5.4 Projects 管理

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/admin/projects` | 列表 |
| GET | `/api/admin/projects/{id}` | 详情 |
| POST | `/api/admin/projects` | 创建 |
| PUT | `/api/admin/projects/{id}` | 更新 |
| DELETE | `/api/admin/projects/{id}` | 删除（含关联标签） |

POST/PUT body：`ProjectsCreateRequest { name, description, demoUrl?, githubUrl?, tagIds?, newTags? }`

### 5.5 评论管理

**GET** `/api/admin/comments` — 列表（含 blog_title）  
**DELETE** `/api/admin/comments/{id}` — 删除（含级联子回复）

### 5.6 About Me 管理

**PUT** `/api/admin/about`

```json
{ "nickname": "...", "avatar": "...", "bio": "...", "skills": "...", "hobbies": "...", "location": "...", "socialLinks": "..." }
```

### 5.7 文件上传

**POST** `/api/admin/upload?type=image|avatar`

> Content-Type: multipart/form-data  
> 字段：file（jpg/png/webp，≤ 2MB）

```json
{ "url": "/uploads/images/20260608_143022.jpg" }
```

### 5.8 Hero 图片上传

**POST** `/api/admin/hero-image?type=light|dark`

```json
{ "url": "/uploads/hero/hero-light.png" }
```

### 5.9 引语管理

**POST** `/api/admin/hero-quotes`

```json
{ "content": "...", "author": "...", "source": "..." }
```

**DELETE** `/api/admin/hero-quotes/{id}`

### 5.10 便签管理

**POST** `/api/admin/sticky-notes`

```json
{ "content": "...", "color": "#fff3cd", "rotate": 2 }
```

**DELETE** `/api/admin/sticky-notes/{id}`

---

## 6. 接口清单汇总

| 方法 | 路径 | 认证 | 说明 |
|---|---|---|---|
| GET | `/api/home` | 无 | 首页数据 |
| GET | `/api/hero-quotes` | 无 | 引语列表 |
| GET | `/api/hero-config` | 无 | Hero 图片配置 |
| GET | `/api/blog` | 无 | Blog 列表 + 搜索 |
| GET | `/api/blog/{slug}` | 无 | Blog 详情 |
| GET | `/api/blog/{slug}/comments` | 无 | 文章评论 |
| GET | `/api/notes` | 无 | Notes 列表 + 搜索 |
| GET | `/api/projects` | 无 | 项目列表 |
| GET | `/api/about` | 无 | 个人资料 |
| GET | `/api/tags` | 无 | 标签列表 |
| GET | `/api/sticky-notes` | 无 | 便签列表 |
| GET | `/api/auth/github/url` | 无 | GitHub 授权 URL |
| GET | `/api/auth/github/callback` | 无 | GitHub 回调 |
| POST | `/api/blog/{slug}/comments` | 评论者 | 发表评论 |
| DELETE | `/api/comments/{id}` | 评论者 | 删除自己的评论 |
| POST | `/api/admin/login` | 无 | 管理员登录 |
| POST | `/api/admin/refresh` | 无 | 刷新 Token |
| PUT | `/api/admin/password` | 管理员 | 修改密码 |
| GET | `/api/admin/blog` | 管理员 | Blog 列表 |
| POST | `/api/admin/blog` | 管理员 | 创建文章 |
| GET | `/api/admin/blog/{id}` | 管理员 | 文章详情 |
| PUT | `/api/admin/blog/{id}` | 管理员 | 编辑文章 |
| DELETE | `/api/admin/blog/{id}` | 管理员 | 删除文章 |
| GET | `/api/admin/notes` | 管理员 | Notes 列表 |
| POST | `/api/admin/notes` | 管理员 | 创建笔记 |
| GET | `/api/admin/notes/{id}` | 管理员 | 笔记详情 |
| PUT | `/api/admin/notes/{id}` | 管理员 | 编辑笔记 |
| DELETE | `/api/admin/notes/{id}` | 管理员 | 删除笔记 |
| GET | `/api/admin/projects` | 管理员 | 项目列表 |
| POST | `/api/admin/projects` | 管理员 | 创建项目 |
| GET | `/api/admin/projects/{id}` | 管理员 | 项目详情 |
| PUT | `/api/admin/projects/{id}` | 管理员 | 编辑项目 |
| DELETE | `/api/admin/projects/{id}` | 管理员 | 删除项目 |
| GET | `/api/admin/comments` | 管理员 | 评论列表 |
| DELETE | `/api/admin/comments/{id}` | 管理员 | 删除评论 |
| PUT | `/api/admin/about` | 管理员 | 编辑个人资料 |
| POST | `/api/admin/upload` | 管理员 | 上传文件 |
| POST | `/api/admin/hero-image` | 管理员 | 上传 Hero 图 |
| POST | `/api/admin/hero-quotes` | 管理员 | 创建引语 |
| DELETE | `/api/admin/hero-quotes/{id}` | 管理员 | 删除引语 |
| POST | `/api/admin/sticky-notes` | 管理员 | 创建便签 |
| DELETE | `/api/admin/sticky-notes/{id}` | 管理员 | 删除便签 |

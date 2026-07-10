# 个人博客 - API 接口设计

## 1. 约定

### 1.1 统一响应格式

```json
{ "code": 200, "message": "操作成功", "data": {} }
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
| 游客（留言、便签） | httpOnly Cookie `commenter_token`（浏览器自动发送） |
| 管理员 | httpOnly Cookie `admin_token`（浏览器自动发送） |

管理员接口 `/api/admin/**` 由 JwtInterceptor 统一拦截（排除 `/api/admin/login`、`/api/admin/refresh`、`/api/admin/status`、`/api/admin/logout`）。

### 1.3 接口前缀

| 类型 | 前缀 |
|---|---|
| 前台公开 | `/api` |
| 管理员后台 | `/api/admin` |

---

## 2. 前台接口（无需认证 / 评论者认证）

### 2.1 首页

**GET** `/api/home` — 返回 `HomeVO`（latestBlogs, latestNotes, latestProjects）

### 2.2 引语

**GET** `/api/hero-quotes` — 返回 `List<HeroQuoteVO>`（id, content, author, source）

### 2.3 Hero 图片配置

**GET** `/api/hero-config` — 返回 `{ heroLight, heroDark }`

### 2.4 Blog

**GET** `/api/blog?q={keyword}&tag={tag_slug}` — 返回 `List<BlogListVO>`

**GET** `/api/blog/{slug}` — 返回 `BlogVO`（含 content、prev、next 导航）

> 评论使用 Utterances 第三方 iframe，存储在 GitHub Issues 中，不走后端 API。

### 2.5 Notes

**GET** `/api/notes?q={keyword}&tag={tag_slug}` — 返回 `List<NotesVO>`

### 2.6 Projects

**GET** `/api/projects` — 返回 `List<ProjectsVO>`

### 2.7 标签列表

**GET** `/api/tags` — 返回 `List<TagVO>`

### 2.8 便签

**GET** `/api/sticky-notes?source=admin|guest`

- `source=admin`：返回管理员创建的便签（About 页用）
- `source=guest`：返回游客创建的便签（Leave a Note 页用）
- 返回 `List<StickyNoteVO>`（含 category、authorName、authorAvatar、own 标识）

**POST** `/api/sticky-notes`（需评论者 token）

```json
{ "content": "...", "color": "#fff3cd", "rotate": 2, "category": "to_aa|to_website" }
```

**DELETE** `/api/sticky-notes/{id}`（需评论者 token，仅可删自己的）

---

## 3. GitHub OAuth 认证

### 3.1 获取授权 URL

**GET** `/api/auth/github/url` → `{ "url": "https://github.com/login/oauth/authorize?..." }`

### 3.2 授权回调

**GET** `/api/auth/github/callback?code={code}` → `{ "name": "...", "avatar": "..." }`

回调后 `commenter_token` 写入 httpOnly Cookie，body 只返回用户信息。前端存储 `commenter` 信息到 localStorage，跳转至 `/guest` 页。

---

## 4. 管理员接口

> httpOnly Cookie `admin_token` 自动携带

### 4.1 认证

| 方法 | 路径 | 说明 |
|---|---|---|
| POST | `/api/admin/login` | 登录 `{ username, password }` → 设 httpOnly Cookie，body 为空 |
| POST | `/api/admin/refresh` | 刷新 Cookie 中的 Token |
| GET | `/api/admin/status` | 检查登录状态 → `{ authenticated: true/false }` |
| POST | `/api/admin/logout` | 退出登录（清除 Cookie） |
| PUT | `/api/admin/password` | 修改密码 `{ oldPassword, newPassword }` |

### 4.2 Blog 管理

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/admin/blog` | 列表 |
| GET | `/api/admin/blog/{id}` | 详情 |
| POST | `/api/admin/blog` | 创建 |
| PUT | `/api/admin/blog/{id}` | 更新 |
| DELETE | `/api/admin/blog/{id}` | 删除（含关联标签） |

POST/PUT body：`BlogCreateRequest { title, summary, content, tagIds?, newTags? }`

### 4.3 Notes 管理

同 Blog（不含 summary），body：`NotesCreateRequest { title, content, tagIds?, newTags? }`

### 4.4 Projects 管理

同 Blog，body：`ProjectsCreateRequest { name, description, demoUrl?, githubUrl?, tagIds?, newTags? }`

### 4.5 文件上传

**POST** `/api/admin/upload?type=image|avatar` — multipart/form-data，≤2MB

**POST** `/api/admin/hero-image?type=light|dark` — Hero 图片上传

### 4.6 首页管理

| 方法 | 路径 | 说明 |
|---|---|---|
| POST | `/api/admin/hero-quotes` | 创建引语 `{ content (必填), author, source }` |
| DELETE | `/api/admin/hero-quotes/{id}` | 删除引语 |
| POST | `/api/admin/sticky-notes` | 创建便签（About 页） |
| DELETE | `/api/admin/sticky-notes/{id}` | 删除便签 |

---

### 4.7 Auth（GitHub OAuth）

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/auth/github/url` | 获取 GitHub OAuth 登录 URL |
| GET | `/api/auth/github/callback` | GitHub OAuth 回调（设 httpOnly Cookie `commenter_token`） |
| GET | `/api/auth/status` | 检查评论者登录状态 → `{ authenticated, name?, avatar? }` |
| POST | `/api/auth/logout` | 退出评论者登录（清除 Cookie） |

---

## 5. SPA 路由处理

`SpaForwardFilter` 实现 Vue Router history mode 刷新不 404：

| 请求 | 处理 |
|------|------|
| `/api/**` | 放行（后端 API） |
| `/uploads/**` | 放行（静态文件） |
| 包含 `.` 的路径 | 放行（JS/CSS/图片等静态资源） |
| 其他 GET 请求 | 转发 `/index.html`（Vue Router 接管） |
| 其他 POST/PUT/DELETE | 放行（由 Spring 返回 405） |

> 仅 `GET` 请求转发 SPA，避免 POST 请求收到 HTML。

---

## 6. 接口清单汇总

| 方法 | 路径 | 认证 | 说明 |
|---|---|---|---|
| GET | `/api/home` | 无 | 首页数据 |
| GET | `/api/hero-quotes` | 无 | 引语列表 |
| GET | `/api/hero-config` | 无 | Hero 图片配置 |
| GET | `/api/blog` | 无 | Blog 列表 |
| GET | `/api/blog/{slug}` | 无 | Blog 详情 |
| GET | `/api/notes` | 无 | Notes 列表 |
| GET | `/api/projects` | 无 | 项目列表 |
| GET | `/api/tags` | 无 | 标签列表 |
| GET | `/api/sticky-notes` | 无 | 便签列表 |
| POST | `/api/sticky-notes` | 评论者 Cookie | 创建便签 |
| DELETE | `/api/sticky-notes/{id}` | 评论者 Cookie | 删除便签（仅自己的） |
| GET | `/api/auth/github/url` | 无 | GitHub 授权 URL |
| GET | `/api/auth/github/callback` | 无 | GitHub 回调 |
| GET | `/api/auth/status` | 无 | 评论者登录状态 |
| POST | `/api/auth/logout` | 评论者 | 退出评论者登录 |
| POST | `/api/admin/login` | 无 | 管理员登录 |
| POST | `/api/admin/refresh` | 无 | 刷新 Token |
| GET | `/api/admin/status` | 无 | 管理员登录状态 |
| POST | `/api/admin/logout` | 管理员 | 退出管理员登录 |
| PUT | `/api/admin/password` | 管理员 Cookie | 修改密码 |
| GET/POST/PUT/DELETE | `/api/admin/blog` | 管理员 Cookie | Blog CRUD |
| GET/POST/PUT/DELETE | `/api/admin/notes` | 管理员 Cookie | Notes CRUD |
| GET/POST/PUT/DELETE | `/api/admin/projects` | 管理员 Cookie | Projects CRUD |
| POST/DELETE | `/api/admin/sticky-notes` | 管理员 Cookie | 便签管理 |
| POST/DELETE | `/api/admin/hero-quotes` | 管理员 Cookie | 引语管理 |
| POST | `/api/admin/upload` | 管理员 Cookie | 上传文件（UUID 重命名） |
| POST | `/api/admin/hero-image` | 管理员 Cookie | 上传 Hero 图 |

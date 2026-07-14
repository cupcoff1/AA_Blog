# AA_Blog

仿 [tania.dev](https://tania.dev) 风格的极简个人博客，前后端分离架构。

## 技术栈

| 层 | 技术 |
|---|---|
| 前端 | Vue 3 + TypeScript + Vue Router + Axios + Vite |
| 后端 | Spring Boot 3.2.5 + MyBatis-Plus 3.5.6 |
| 数据库 | MySQL 8.0 |
| 数据库迁移 | Liquibase |
| 认证 | JWT（管理员）+ GitHub OAuth（游客） |
| 评论 | Utterances（GitHub Issues 评论 iframe） |
| Markdown | marked + marked-highlight + highlight.js |
| CI | GitHub Actions |

## 项目结构

```
AA_Blog/
├── AA_个人博客/              # Obsidian 设计文档
├── AA_Blog_backEnd/          # Spring Boot 后端
│   └── src/main/java/com/javaee/blog/
│       ├── common/           # Result、ResultCode、GlobalExceptionHandler、AppConstants
│       ├── config/           # WebMvcConfig、DataInitializer、AppConfig、PasswordConfig
│       ├── interceptor/      # JwtInterceptor、CommenterInterceptor
│       ├── controller/       # 13 个控制器
│       ├── entity/           # 实体类
│       │   ├── base/         # BaseEntity（id、createdAt、updatedAt）
│       │   └── association/  # 多对多关联（BlogTags、NoteTags、ProjectTags）
│       ├── dto/              # request / VO
│       ├── mapper/           # MyBatis-Plus Mapper
│       ├── util/             # JwtUtil、SlugUtil
│       └── service/          # 服务接口 + 实现
├── AA_Blog_frontEnd/         # Vue 3 前端
│   └── src/
│       ├── api/              # API 模块（blog、note、project、auth 等 9 个）
│       ├── models/types.ts   # TypeScript 类型
│       ├── router/routes.ts  # 路由配置
│       ├── components/       # 共享组件（Sidebar、LogoIcon、TagEditor）
│       ├── views/            # 前台页面（8 个）
│       └── views/admin/      # 后台页面（5 个）
├── .github/workflows/ci.yml  # CI 配置
└── .gitignore
```

## 功能

- **Blog** — 长文技术文章，Markdown 渲染 + 代码高亮，Utterances 评论
- **Notes** — 短笔记，列表页展示全文
- **Projects** — 个人项目展示
- **About Me** — 管理员便签墙
- **Leave a Note** — 游客便签墙（GitHub 登录后留言，分类 To AA_ / To Website）
- **首页引语** — Hero 区循环展示引语，管理员可管理
- **Hero 图片** — 亮色/暗色主题 Hero 图片上传
- **暗色/亮色模式** — CSS 变量 + localStorage
- **侧边栏导航** — Logo + 导航 + 主题切换
- **内联管理** — 管理员登录后，增删按钮直接出现在对应区域

## 本地运行

```bash
# 1. 创建数据库（Liquibase 启动时自动建表）
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS aa_blog"

# 2. 配置 .env
cp AA_Blog_backEnd/.env.example AA_Blog_backEnd/.env

# 3. 启动后端
cd AA_Blog_backEnd && mvn spring-boot:run

# 4. 启动前端
cd AA_Blog_frontEnd && npm install && npm run dev
```

## 默认管理员

首次启动自动创建。用户名默认 `AA_`，密码通过环境变量 `ADMIN_INIT_PASSWORD` 设置。

**未设置时**：自动生成随机密码，启动日志输出（首次登录后请立即修改）。

## 部署（Nginx 反向代理）

```bash
# 1. 构建前端（产物在 dist/）
cd AA_Blog_frontEnd && npm run build

# 2. 启动后端
cd AA_Blog_backEnd && mvn spring-boot:run

# 3. 修改 nginx.conf 中的 root/alias 路径指向实际位置

# 4. 启动 Nginx
nginx
```

```
浏览器 → Nginx :80
           ├── /            → 前端 dist/index.html（try_files 处理 SPA 路由）
           ├── /api/*       → proxy_pass :8080（后端 API）
           └── /uploads/*   → alias 上传目录
```

公网暴露推荐 ngrok（开发测试）或 Nginx 反代 + 域名（生产）。

国内环境部署需配 Cloudflare WARP 或代理让 JVM 能访问 GitHub API（用于 GitHub OAuth 登录）。

## GitHub OAuth 配置

1. 在 [GitHub Developer Settings](https://github.com/settings/developers) 创建 OAuth App
2. 回调 URL 填 `https://你的域名/auth/callback`
3. `.env` 中 `GITHUB_REDIRECT_URI` 保持一致
4. GitHub OAuth App 设置和设备注册表与 `.env` 中 `client_id`/`client_secret` 一致

## 环境变量

| 变量 | 说明 | 必填 |
|---|---|---|
| DB_PASSWORD | MySQL 密码 | ✅ |
| JWT_SECRET | JWT 签名密钥（≥ 32 字节） | ✅ |
| ADMIN_INIT_USERNAME | 初始管理员用户名（默认 AA_） | |
| ADMIN_INIT_PASSWORD | 初始管理员密码（不设则随机生成） | |
| GITHUB_CLIENT_ID | GitHub OAuth Client ID | |
| GITHUB_CLIENT_SECRET | GitHub OAuth Client Secret | |
| GITHUB_REDIRECT_URI | GitHub OAuth 回调地址 | |

# AA_Blog

仿 [tania.dev](https://tania.dev) 风格的极简个人博客，前后端分离架构。

## 技术栈

| 层 | 技术 |
|---|---|
| 前端 | Vue 3 + TypeScript + Vue Router + Axios + Vite |
| 后端 | Spring Boot 3.2.5 + MyBatis-Plus 3.5.6 |
| 数据库 | MySQL 8.0 |
| 认证 | JWT（管理员）+ GitHub OAuth（评论者） |
| Markdown | marked + marked-highlight + highlight.js |

## 项目结构

```
AA_Blog/
├── AA_个人博客/              # Obsidian 设计文档
│   ├── 01-需求文档.md
│   ├── 02-数据库设计.md
│   ├── 03-API接口设计.md
│   └── 04-前端设计.md
├── AA_Blog_backEnd/          # Spring Boot 后端
│   └── src/main/java/com/javaee/blog/
│       ├── common/           # Result、ResultCode、GlobalExceptionHandler
│       ├── config/           # JwtInterceptor、WebMvcConfig、DataInitializer
│       ├── controller/       # 15 个控制器，35+ 端点
│       ├── dto/request/      # 请求 DTO（8 个）
│       ├── dto/vo/           # 视图 VO（12 个）
│       ├── entity/           # 实体类（12 个，映射 12 张表）
│       ├── mapper/           # MyBatis-Plus Mapper（12 个）
│       ├── service/          # 服务接口（12 个）
│       ├── service/impl/     # 服务实现（12 个）
│       └── util/             # JwtUtil、SlugUtil
├── AA_Blog_frontEnd/         # Vue 3 前端
│   └── src/
│       ├── api/client.ts     # Axios 封装（泛型 + Token 拦截）
│       ├── models/types.ts   # TypeScript 类型定义（16 个接口）
│       ├── router/routes.ts  # 路由配置（18 条）
│       ├── components/       # 共享组件（Sidebar、LogoIcon、TagEditor）
│       ├── views/            # 前台页面（7 个）
│       └── views/admin/      # 后台页面（6 个）
└── .gitignore
```

## 功能

- **Blog** — 长文技术文章，Markdown 渲染 + 代码高亮，评论区（GitHub 登录、嵌套回复）
- **Notes** — 短笔记，列表页展示全文
- **Projects** — 个人项目展示
- **About Me** — 个人介绍页
- **便签** — About 页便签墙，管理员可增删
- **首页引语** — Hero 区循环展示引语（作者 + 作品名），管理员可增删
- **Hero 图片** — 亮色/暗色主题 Hero 图片，管理员可上传替换
- **暗色/亮色模式** — CSS 变量 + localStorage 持久化
- **侧边栏导航** — Logo + 导航链接 + 主题切换
- **内联管理** — 管理员登录后，增删按钮直接出现在对应区域，无需单独后台布局

## 本地运行

```bash
# 1. 创建数据库
mysql -u root -p < AA_Blog_backEnd/src/main/resources/db/schema.sql

# 2. 配置 .env
cp AA_Blog_backEnd/.env.example AA_Blog_backEnd/.env
# 编辑 .env 填入数据库密码和 JWT 密钥

# 3. 启动后端
cd AA_Blog_backEnd
mvn spring-boot:run

# 4. 启动前端
cd AA_Blog_frontEnd
npm install && npm run dev
```

前端 http://localhost:5173，后端 http://localhost:8080。

## 默认管理员

| 用户名 | 密码 |
|---|---|
| AA_ | 123456 |

项目首次启动自动初始化管理员账号和 About 空行。登录后侧边栏 Logo 可点击进入首页管理。

## 环境变量

| 变量 | 说明 |
|---|---|
| DB_PASSWORD | MySQL 密码 |
| JWT_SECRET | JWT 签名密钥（≥ 32 字节） |
| GITHUB_CLIENT_ID | GitHub OAuth Client ID |
| GITHUB_CLIENT_SECRET | GitHub OAuth Client Secret |
| GITHUB_REDIRECT_URI | GitHub OAuth 回调地址 |

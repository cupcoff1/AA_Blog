# AA_Blog

仿 [tania.dev](https://tania.dev) 风格的极简个人博客，前后端分离架构。

## 技术栈

| 层 | 技术 |
|---|---|
| 前端 | Vue 3 + Vue Router + Axios |
| 后端 | Spring Boot 3 + MyBatis-Plus |
| 数据库 | MySQL 8.0 |
| 认证 | JWT（管理员）+ GitHub OAuth（评论者） |

## 项目结构

```
AA_Blog/
├── AA_个人博客/           # 设计文档
│   ├── 01-需求文档.md
│   ├── 02-数据库设计.md
│   └── 03-API接口设计.md
├── AA_Blog_backEnd/      # Spring Boot 后端
└── AA_Blog_frontEnd/     # Vue 3 前端（待创建）
```

## 功能

- **Blog** — 长文技术文章，Markdown 渲染 + 代码高亮，评论区（GitHub 登录、嵌套回复）
- **Notes** — 短笔记，列表页展示全文
- **Projects** — 个人项目展示
- **About Me** — 个人介绍页
- **暗色/亮色模式** — 主题切换

## 本地运行

```bash
# 后端
cd AA_Blog_backEnd
mvn spring-boot:run

# 前端（待创建）
cd AA_Blog_frontEnd
npm install && npm run dev
```

## 默认管理员

| 用户名 | 密码 |
|---|---|
| AA_ | 123456 |

项目首次启动自动初始化管理员账号和 About 空行。

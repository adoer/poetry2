## Context

构建新闻网站第一版，覆盖从后台管理到前台展示的核心闭环。采用前后端分离架构，Java Spring Boot 3.x 提供 RESTful API，Vue 3 + TypeScript 构建前端 SPA。传统部署方式：后端运行 jar 包，前端构建后的静态文件由 Nginx 托管。

## Goals / Non-Goals

**Goals:**
- 建立 monorepo 项目结构，后端与前端代码分离
- 实现 JWT 认证的管理员登录
- 实现新闻 CRUD 和分类管理
- 实现前台新闻列表（分页+分类筛选）和详情展示
- 路由权限控制，管理员和普通用户访问不同页面

**Non-Goals:**
- 不包含用户注册（仅管理员由系统初始化）
- 不包含评论、点赞等互动功能
- 不包含搜索引擎集成
- 不包含多语言
- 不包含 Docker 容器化部署

## Decisions

### 项目结构

```
news-project/
├── backend/              # Spring Boot 3.x + Maven
│   ├── src/main/java/
│   │   └── com/news/
│   │       ├── config/        # Spring Security, JWT, CORS 配置
│   │       ├── controller/    # REST 控制器
│   │       ├── entity/        # JPA 实体
│   │       ├── repository/    # JPA Repository
│   │       ├── service/       # 业务逻辑
│   │       ├── dto/           # 数据传输对象
│   │       ├── exception/     # 统一异常处理
│   │       └── util/          # JWT 工具类等
│   └── src/main/resources/
│       ├── application.yml
│       └── data.sql           # 初始化管理员账号
├── frontend/             # Vue 3 + Vite + TypeScript
│   ├── src/
│   │   ├── api/              # Axios 请求封装
│   │   ├── assets/           # 静态资源
│   │   ├── components/       # 通用组件
│   │   ├── layouts/          # 布局组件
│   │   ├── router/           # 路由配置 + 权限守卫
│   │   ├── store/            # Pinia 状态管理
│   │   ├── types/            # TypeScript 类型定义
│   │   ├── utils/            # 工具函数
│   │   └── views/            # 页面组件
│   │       ├── admin/        # 后台页面
│   │       ├── auth/         # 登录页
│   │       └── public/       # 前台页面
│   └── vite.config.ts
└── docs/                 # 项目文档
```

### API 设计

```
GET    /api/news                 # 新闻列表（分页+分类筛选）
GET    /api/news/{id}            # 新闻详情
GET    /api/categories           # 分类列表

POST   /api/auth/login           # 管理员登录
POST   /api/auth/logout          # 管理员登出

POST   /api/admin/news           # 创建新闻
PUT    /api/admin/news/{id}      # 编辑新闻
DELETE /api/admin/news/{id}      # 删除新闻
GET    /api/admin/news           # 后台新闻列表

POST   /api/admin/categories     # 创建分类
PUT    /api/admin/categories/{id}# 编辑分类
DELETE /api/admin/categories/{id}# 删除分类
```

### 数据模型

```
Category
  id          Long (PK, auto)
  name        String (unique, not null)
  createdAt   LocalDateTime
  updatedAt   LocalDateTime

News
  id          Long (PK, auto)
  title       String (not null)
  content     Text (not null)
  summary     String
  coverImage  String
  categoryId  Long (FK -> Category)
  status      Enum: DRAFT / PUBLISHED
  createdAt   LocalDateTime
  updatedAt   LocalDateTime

User (管理员)
  id          Long (PK, auto)
  username    String (unique, not null)
  password    String (BCrypt encrypted)
  role        Enum: ADMIN / USER
  createdAt   LocalDateTime
```

### 认证方案

- JWT 登录：用户名密码 → 校验 → 返回 JWT token（含 userId, role, 过期时间）
- Token 存放：前端存储在 localStorage，请求时放入 Authorization header
- 后端拦截：Spring Security + JwtAuthenticationFilter 对 `/api/admin/**` 路径做鉴权
- 密码加密：BCryptPasswordEncoder
- Redis：用于存储黑名单 token（登出后失效）

### 权限控制

- 前端路由守卫：根据 store 中用户角色动态添加后台路由
- 后端接口守卫：Spring Security 配置，`/api/admin/**` 需要 ADMIN 角色

### 前端架构

- 状态管理（Pinia）：user store（token、用户信息、角色）
- 路由设计：
  - `/` → 首页（新闻列表）
  - `/news/:id` → 新闻详情
  - `/login` → 登录页
  - `/admin` → 后台入口（需要 ADMIN 角色）
  - `/admin/news` → 新闻管理
  - `/admin/categories` → 分类管理
- HTTP 请求：Axios 实例封装，请求拦截器自动注入 token，响应拦截器统一处理 401

## Risks / Trade-offs

- **[风险] token 存储在 localStorage 存在 XSS 攻击风险** → 在本阶段可接受，后续可迁移至 httpOnly Cookie
- **[风险] JPA N+1 查询问题** → 新闻列表查询使用 @EntityGraph 或 @Query 做 JOIN FETCH
- **[风险] 图片上传未纳入 MVP** → 封面图使用 URL 字段，后续再实现文件上传
- **[风险] 无用户注册功能** → 管理员账号通过 data.sql 初始化或手动插入

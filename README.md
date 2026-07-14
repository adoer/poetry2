# 拾光古诗文

基于 Spring Boot 3.4 + Vue 3 的古诗文内容管理与展示平台，支持浏览、搜索、收藏古诗文、作者、名句等。

## 技术栈

| 层级 | 技术 |
|------|------|
| 后端 | Spring Boot 3.4, Spring Data JPA, Spring Security, JWT, MariaDB, Undertow |
| 前端 | Vue 3, TypeScript, Vite, Pinia, Vue Router, Element Plus, Axios, Tailwind CSS |
| 部署 | Docker Compose (MariaDB + Backend + Nginx/Brotli), Java 17+, Maven |

## 项目结构

```
poetry2/
├── backend/          # Spring Boot 后端
├── frontend/         # Vue 3 前端
├── docker-compose.yml
├── nginx/            # Nginx 配置
└── docs/             # 文档与部署脚本
```

## 功能概览

- **古诗文**：按分类、朝代、作者浏览和搜索古诗文，支持原文、译文、赏析、音频
- **作者**：作者生平介绍及作品列表
- **名句**：经典名句浏览
- **推荐**：首页内容推荐
- **搜索**：全局统一搜索（诗文、作者、名句）
- **用户系统**：注册、登录（验证码）、邮箱验证、密码找回
- **收藏**：用户收藏管理
- **管理后台**：管理员仪表盘

## API 端点

| 路径 | 说明 |
|------|------|
| `/api/poesy/**` | 古诗文相关 |
| `/api/authors/**` | 作者相关 |
| `/api/quotes/**` | 名句相关 |
| `/api/category/**` | 分类相关 |
| `/api/writers` | 热门作家 |
| `/api/recommend` | 首页推荐 |
| `/api/searchAll` | 全局搜索 |
| `/api/favorite/**` | 收藏管理 |
| `/api/login`, `/api/signup` | 认证 |

## 环境要求

- JDK 17+
- Maven 3.8+
- Node.js 18+
- MariaDB 10.11+

## 快速启动

### 1. 配置环境变量

```bash
cp .env.example .env
# 编辑 .env，配置数据库、JWT 密钥等
```

### 2. 启动后端

```bash
cd backend
mvn spring-boot:run
```

### 3. 启动前端

```bash
cd frontend
npm install
npm run dev
```

### 4. 访问

- 前端开发: http://localhost:3000
- 后端 API: http://localhost:8080

## Docker 部署

```bash
docker-compose up -d
```

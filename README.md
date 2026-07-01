# 新闻网站

基于 Spring Boot 3.x + Vue 3 的新闻内容管理系统。

## 技术栈

| 层级 | 技术 |
|------|------|
| 后端 | Spring Boot 3.x, Spring Data JPA, Spring Security, MySQL, Redis |
| 前端 | Vue 3, TypeScript, Vite, Pinia, Vue Router, Element Plus, Axios |
| 部署 | Java 17+, Maven, Nginx |

## 项目结构

```
news-project/
├── backend/          # Spring Boot 后端
├── frontend/         # Vue 3 前端
└── docs/             # 文档与部署配置
```

## 环境要求

- JDK 17+
- Maven 3.8+
- Node.js 18+
- MySQL 8.x
- Redis 6.x

## 快速启动

### 1. 初始化数据库

```sql
CREATE DATABASE news DEFAULT CHARACTER SET utf8mb4;
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

## 部署

参见 `docs/nginx.conf` 和 `docs/start.sh`。

## 默认管理员

- 用户名: admin
- 密码: admin123

## Why

构建一个轻量级新闻内容管理系统，覆盖从后台发布到前台展示的完整闭环。作为项目的第一阶段，确立前后端分离架构和核心业务流程，为后续功能扩展奠定基础。

## What Changes

- 新建 `backend/` Spring Boot 3.x 项目，提供 RESTful API
- 新建 `frontend/` Vue 3 + Vite + TypeScript 前端项目，包含用户端和管理后台（路由权限控制）
- 支持管理员通过 JWT 登录认证
- 支持新闻的分类管理、发布、编辑、删除
- 支持前台按分类浏览新闻列表（分页）和查看新闻详情
- 使用传统部署方式：后端 jar + 前端 nginx

## Capabilities

### New Capabilities
- `user-auth`: 用户认证与权限管理，基于 JWT 的登录/登出，角色（管理员/普通用户）控制
- `news-public`: 前台新闻展示，包括新闻列表（分页+分类筛选）和新闻详情页
- `news-management`: 后台新闻管理，包括新闻 CRUD 和分类管理

### Modified Capabilities

无

## Impact

- 新增 `news-project/` 根目录，包含 `backend/`、`frontend/` 两个子项目
- 后端新增依赖：Spring Boot 3.x、Spring Data JPA、Spring Security、MySQL Driver、Redis、jjwt
- 前端新增依赖：Vue 3、Vue Router、Pinia、Axios、Element Plus、TypeScript
- 需要 MySQL 数据库和 Redis 服务

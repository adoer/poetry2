## Context

拾光古诗文 是一个 Spring Boot 3 + Vue 3 + Element Plus 的古诗文展示平台。目前已有完整的 JWT 认证体系和 ADMIN/USER 角色分离，AdminLayout 和路由守卫已就绪，但缺乏实际的管理功能。本次设计为管理端增加 5 个模块：诗词管理、名句管理、作者管理、用户管理、统计概览。

## Goals / Non-Goals

**Goals:**
- 为管理员提供诗词、名句、作者的 CRUD 操作能力
- 提供用户信息只读查看
- 基于现有数据展示统计概览
- 复用现有的 JWT + ADMIN role 机制和 AdminLayout

**Non-Goals:**
- 不新增数据埋点或用户行为追踪
- 不修改现有公开 API 的行为
- 不修改数据库 schema
- 不引入新的外部依赖

## Decisions

### 1. Controller 分层设计
将管理端 API 放在独立的 `controller/admin/` 包下，与公开 Controller 物理隔离。所有路径以 `/api/admin/` 开头，利用已有的 `SecurityConfig` 保护。

**Alternatives considered:**
- 在现有 Controller 中通过注解判断角色 → 耦合度高，不利于维护
- `controller/admin/` 包 → 职责清晰，安全策略集中控制

### 2. ID 生成策略
Poesy、Quotes、Authors 均使用 Integer 手动 ID。新增时查询 `SELECT MAX(id)` 后加 1。

**Why not auto-increment:** 现有数据的 ID 是手动导入的，统一采用 max+1 策略最简单，无需改表结构。

### 3. 统计查询方式
直接在 Service 层写 JPQL 聚合查询（COUNT + GROUP BY），通过 `AdminStatsService` 统一提供。

**Why not a separate stats table:** 数据量不大，实时查询性能足够，避免引入额外的 ETL 流程。

### 4. 前端 Type 字段编辑
使用 Element Plus `el-select` 的 `multiple` + `filterable` + `allow-create` 组合，加载 `/api/category` 作为选项来源，允许管理员选择已有分类或输入新分类。

### 5. 删除确认
所有删除操作使用 `el-popconfirm` 包裹删除按钮，确认后才发送 DELETE 请求。

## Risks / Trade-offs

- [Risk] `max(id)+1` 在并发下可能产生冲突 → Mitigation: 管理端使用人数极少，冲突概率极低；可后续改为数据库序列
- [Risk] 统计查询在数据量大时可能变慢 → Mitigation: 数据量当前在万级，COUNT 查询性能足够
- [Trade-off] 用户管理只读 → 管理员无法在前台修改用户密码或提升权限，需要时需直接操作数据库

## API Design

```
Backend:
  GET    /api/admin/poesy?page=&size=&keyword=     → 诗词分页列表
  GET    /api/admin/poesy/{id}                     → 诗词详情
  POST   /api/admin/poesy                          → 新增诗词
  PUT    /api/admin/poesy/{id}                     → 编辑诗词
  DELETE /api/admin/poesy/{id}                     → 删除诗词

  GET    /api/admin/quotes?page=&size=&keyword=     → 名句分页列表
  GET    /api/admin/quotes/{id}                    → 名句详情
  POST   /api/admin/quotes                         → 新增名句
  PUT    /api/admin/quotes/{id}                    → 编辑名句
  DELETE /api/admin/quotes/{id}                    → 删除名句

  GET    /api/admin/authors?page=&size=&keyword=   → 作者分页列表
  GET    /api/admin/authors/{id}                   → 作者详情
  POST   /api/admin/authors                        → 新增作者
  PUT    /api/admin/authors/{id}                   → 编辑作者
  DELETE /api/admin/authors/{id}                   → 删除作者

  GET    /api/admin/users?page=&size=&keyword=     → 用户分页列表
  GET    /api/admin/users/{id}                     → 用户详情

  GET    /api/admin/stats/overview                 → 概览统计 (4 个 count)
  GET    /api/admin/stats/registration             → 注册趋势 (近30天)
  GET    /api/admin/stats/favorites                → 收藏分布

Frontend routes:
  /admin                      → 仪表盘 (Dashboard)
  /admin/poesy                → 诗词列表
  /admin/poesy/create         → 新增诗词
  /admin/poesy/:id/edit       → 编辑诗词
  /admin/quotes               → 名句列表
  /admin/quotes/create        → 新增名句
  /admin/quotes/:id/edit      → 编辑名句
  /admin/authors              → 作者列表
  /admin/authors/create       → 新增作者
  /admin/authors/:id/edit     → 编辑作者
  /admin/users                → 用户列表
  /admin/stats                → 使用统计
```

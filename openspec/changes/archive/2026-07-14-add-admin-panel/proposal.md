## Why

拾光古诗文 目前完全缺乏管理端。诗词、名句、作者等核心数据无法在后台进行查看、编辑或删除，用户信息和使用情况也无法追踪。随着内容量的增长，运营者需要一个管理后台来维护数据质量和了解平台状况。

## What Changes

- 新增管理端侧边栏导航，在现有 AdminLayout 基础上扩展菜单
- 新增 5 个管理功能模块：仪表盘、诗词管理、名句管理、作者管理、用户管理
- 新增使用情况统计页（基于现有数据，不新增埋点）
- 后端新增 `/api/admin/**` 控制器群组（已有安全保护，仅 ADMIN 角色可访问）
- 前端新增对应的管理页面和 API 封装
- 增强现有仪表盘页面，展示关键统计指标

## Capabilities

### New Capabilities
- `poesy-admin`: 诗词的管理 CRUD — 列表搜索、查看详情、新增、编辑、删除
- `quotes-admin`: 名句的管理 CRUD — 列表搜索、查看详情、新增、编辑、删除
- `authors-admin`: 作者的管理 CRUD — 列表搜索、查看详情、新增、编辑、删除
- `user-admin`: 用户信息查看 — 列表搜索、详情查看（只读）
- `stats-overview`: 使用统计 — 基于现有数据的概览指标、注册趋势、收藏分布

### Modified Capabilities

None.

## Impact

- **后端**: 新增 5 个 Controller（在 `controller/admin/` 下）、5 个 Service；无需修改 Entity/Repository
- **前端**: 新增约 10 个 Vue 页面、1 个 API 封装文件；修改 router 和 AdminLayout
- **安全**: 复用现有 JWT + ADMIN role 机制，无需改动 SecurityConfig
- **数据库**: 无 schema 变更

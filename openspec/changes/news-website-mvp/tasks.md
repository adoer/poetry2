## 1. 项目初始化

- [x] 1.1 创建 monorepo 根目录结构（backend/、frontend/、docs/）
- [x] 1.2 使用 Spring Initializr 初始化 Spring Boot 3.x + Maven 项目，添加 JPA、Security、Web、MySQL Driver、Redis 依赖
- [x] 1.3 配置 application.yml（MySQL 数据源、Redis 连接、JWT secret 等）
- [x] 1.4 使用 Vite 创建 Vue 3 + TypeScript 项目，安装 Vue Router、Pinia、Axios、Element Plus
- [x] 1.5 配置 Vite 开发代理（proxy 到后端 8080 端口）

## 2. 后端：数据模型与 Repository

- [x] 2.1 创建 User、Category、News 实体类（JPA Entity），定义字段映射和关联关系
- [x] 2.2 创建对应的 Repository 接口（继承 JpaRepository）
- [x] 2.3 创建实体枚举类型（NewsStatus、UserRole）
- [x] 2.4 初始化数据（data.sql 插入默认管理员账号）

## 3. 后端：认证模块

- [x] 3.1 实现 JWT 工具类（生成 token、解析 token、校验过期）
- [x] 3.2 实现 JwtAuthenticationFilter（从请求头提取 token，校验后设置 SecurityContext）
- [x] 3.3 实现 AuthController（POST /api/auth/login，POST /api/auth/logout）
- [x] 3.4 配置 Spring Security（放行 /api/auth/login、/api/news、/api/categories，保护 /api/admin/**）
- [x] 3.5 集成 Redis 存储 token 黑名单（登出处理）

## 4. 后端：分类管理 API

- [x] 4.1 实现 CategoryService（CRUD 业务逻辑，含删除校验）
- [x] 4.2 实现 CategoryController（CRUD 接口，管理端 /api/admin/categories）
- [x] 4.3 实现分类公共接口（GET /api/categories 返回所有分类列表）

## 5. 后端：新闻管理 API

- [x] 5.1 实现 NewsService（CRUD 业务逻辑，分页查询，分类筛选）
- [x] 5.2 实现 AdminNewsController（后台接口 /api/admin/news）
- [x] 5.3 实现 NewsController（前台接口 /api/news，分页+分类筛选+详情）

## 6. 后端：通用能力

- [x] 6.1 统一响应体封装（Result<T>：code、message、data）
- [x] 6.2 全局异常处理（@RestControllerAdvice）
- [x] 6.3 DTO 对象（LoginRequest、LoginResponse、NewsDTO）
- [x] 6.4 CORS 配置

## 7. 前端：基础框架

- [x] 7.1 项目结构搭建（api/、router/、store/、views/、components/、layouts/、types/、utils/）
- [x] 7.2 Axios 实例封装（baseURL、请求拦截器注入 token、响应拦截器处理 401）
- [x] 7.3 Pinia user store（token 持久化、用户信息、角色判断）
- [x] 7.4 路由配置（公开路由、后台路由、路由守卫 permisson guard）
- [x] 7.5 布局组件（前台 Layout、后台 Layout、404 页面）

## 8. 前端：认证页面

- [x] 8.1 登录页面（用户名/密码表单，调用登录 API，存 token，跳转后台）
- [x] 8.2 导航守卫处理（未登录访问 /admin 重定向到 /login）

## 9. 前端：前台页面

- [x] 9.1 首页（新闻列表展示，分页组件，分类侧边栏/导航筛选）
- [x] 9.2 新闻详情页（展示标题、分类、发布时间、正文内容）
- [x] 9.3 分类列表获取与缓存

## 10. 前端：后台管理页面

- [x] 10.1 后台首页/概览页面
- [x] 10.2 新闻管理列表页（表格展示、分页、搜索）
- [x] 10.3 新闻发布/编辑页（表单：标题、正文编辑器、分类选择、封面图 URL）
- [x] 10.4 分类管理页（表格展示、增删改对话框）
- [x] 10.5 Element Plus 全局导入配置

## 11. 集成与部署

- [ ] 11.1 端到端流程测试（创建分类 → 发布新闻 → 前台查看）
- [x] 11.2 前端构建（vite build），Nginx 配置编写
- [x] 11.3 后端打包（mvn package），启动脚本编写
- [x] 11.4 项目 README（启动方式、环境要求）

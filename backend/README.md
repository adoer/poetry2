# 新闻管理系统后端

## 一、技术架构

| 层次 | 技术栈 | 说明 |
|------|--------|------|
| **框架** | Spring Boot 3.4.1 | 应用基础框架，简化配置与部署 |
| **安全** | Spring Security + JWT (jjwt 0.12.6) | 无状态认证，基于令牌的权限控制 |
| **ORM** | Spring Data JPA (Hibernate) | 对象关系映射，自动建表与 CRUD |
| **数据库** | MySQL 8.x | 关系型数据库，存储业务数据 |
| **缓存** | Redis | 存储 JWT 黑名单（用户注销时令牌失效） |
| **参数校验** | Jakarta Validation | @NotBlank、@Valid 等注解式校验 |
| **构建工具** | Maven | 依赖管理与项目构建 |
| **JDK** | Java 17 | 语言版本 |
| **API 风格** | RESTful JSON | 前后端分离，统一响应格式 |

### 依赖清单

| 依赖 | 用途 |
|------|------|
| spring-boot-starter-web | Web 框架，REST 接口支持 |
| spring-boot-starter-data-jpa | JPA/Hibernate 数据持久化 |
| spring-boot-starter-security | 安全认证与授权 |
| spring-boot-starter-data-redis | Redis 客户端操作 |
| spring-boot-starter-validation | 参数校验注解 |
| mysql-connector-j | MySQL 数据库驱动 |
| jjwt-api / jjwt-impl / jjwt-jackson | JWT 令牌生成与解析 |
| lombok | 编译期代码简化 |

---

## 二、目录结构

```
backend/
├── pom.xml                              # Maven 项目配置，声明依赖和构建插件
├── src/
│   ├── main/
│   │   ├── java/com/news/
│   │   │   ├── NewsApplication.java     # Spring Boot 启动入口
│   │   │   ├── config/                  # 配置层
│   │   │   │   ├── CorsConfig.java
│   │   │   │   ├── JwtAuthenticationFilter.java
│   │   │   │   └── SecurityConfig.java
│   │   │   ├── controller/              # 控制器层（REST 接口）
│   │   │   │   ├── AdminNewsController.java
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── CategoryController.java
│   │   │   │   └── NewsController.java
│   │   │   ├── dto/                     # 数据传输对象
│   │   │   │   ├── LoginRequest.java
│   │   │   │   ├── LoginResponse.java
│   │   │   │   ├── NewsDTO.java
│   │   │   │   └── Result.java
│   │   │   ├── entity/                  # 实体类（数据库映射）
│   │   │   │   ├── Category.java
│   │   │   │   ├── News.java
│   │   │   │   ├── NewsStatus.java
│   │   │   │   ├── User.java
│   │   │   │   └── UserRole.java
│   │   │   ├── exception/               # 异常处理
│   │   │   │   ├── BusinessException.java
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   └── ResourceNotFoundException.java
│   │   │   ├── repository/              # 数据访问层（Repository）
│   │   │   │   ├── CategoryRepository.java
│   │   │   │   ├── NewsRepository.java
│   │   │   │   └── UserRepository.java
│   │   │   ├── service/                 # 服务接口层
│   │   │   │   ├── AuthService.java
│   │   │   │   ├── CategoryService.java
│   │   │   │   └── NewsService.java
│   │   │   ├── service/impl/            # 服务实现层
│   │   │   │   ├── AuthServiceImpl.java
│   │   │   │   ├── CategoryServiceImpl.java
│   │   │   │   └── NewsServiceImpl.java
│   │   │   └── util/                    # 工具类
│   │   │       └── JwtUtil.java
│   │   └── resources/
│   │       └── application.yml          # 配置文件（数据库、Redis、JWT）
│   └── test/                            # 单元测试
|       └── java/com/news/
└── target/                              # Maven 构建产物
```

---

## 三、分层架构说明

### 1. 实体层（entity）

| 文件 | 表名 | 说明 |
|------|------|------|
| `User.java` | `users` | 系统用户，含 username、password（BCrypt 加密）、role |
| `News.java` | `news` | 新闻，含 title、content、summary、categoryId、status |
| `Category.java` | `categories` | 新闻分类，含 name（唯一） |
| `UserRole.java` | - | 角色枚举：ADMIN（管理员）、USER（普通用户） |
| `NewsStatus.java` | - | 状态枚举：DRAFT（草稿）、PUBLISHED（已发布） |

实体均通过 JPA 注解与数据库表映射，主键采用自增策略，createAt/updatedAt 通过 `@PrePersist` / `@PreUpdate` 生命周期回调自动维护。

### 2. 数据传输层（dto）

| 文件 | 说明 |
|------|------|
| `Result<T>` | 统一 API 响应封装，含 code、message、data，提供 success() / error() 静态工厂方法 |
| `NewsDTO` | 新闻数据传输对象，与 News 实体对应，增加 categoryName 冗余字段、时间字符串格式 |
| `LoginRequest` | 登录请求体，含 @NotBlank 校验注解 |
| `LoginResponse` | 登录响应体，含 token、username、role |

### 3. 异常处理层（exception）

| 文件 | 说明 |
|------|------|
| `BusinessException` | 业务规则违反异常（如名称重复、有关联数据无法删除），返回 HTTP 400 |
| `ResourceNotFoundException` | 资源不存在异常，返回 HTTP 404 |
| `GlobalExceptionHandler` | 全局异常处理器，通过 `@RestControllerAdvice` 统一拦截 7 类异常并转为标准 Result 格式 |

异常处理覆盖：
- `MethodArgumentNotValidException` → 400（参数校验失败，逐字段拼接错误信息）
- `HttpMessageNotReadableException` → 400（请求体格式错误）
- `MissingRequestHeaderException` → 400（缺少请求头）
- `AccessDeniedException` → 403（权限不足）
- `ResourceNotFoundException` → 404（资源不存在）
- `BusinessException` → 400（业务逻辑错误）
- `Exception`（兜底）→ 500，记录 error 日志

### 4. 控制器层（controller）

| 文件 | 路由前缀 | 说明 |
|------|---------|------|
| `AuthController` | `/api/auth` | 登录（POST /login）和注销（POST /logout） |
| `NewsController` | `/api/news` | 公开新闻列表与详情（仅已发布） |
| `AdminNewsController` | `/api/admin/news` | 新闻 CRUD（需 ADMIN 角色） |
| `CategoryController` | `/api` | 分类查询（GET /api/categories）+ CRUD（/api/admin/categories） |

### 5. 服务层（service）

| 接口 | 实现类 | 核心功能 |
|------|--------|---------|
| `AuthService` | `AuthServiceImpl` | 用户登录认证（BCrypt 密码校验 + JWT 生成）、注销令牌黑名单 |
| `NewsService` | `NewsServiceImpl` | 新闻分页查询（组合筛选：分类+关键词+状态）、增删改、实体转 DTO |
| `CategoryService` | `CategoryServiceImpl` | 分类 CRUD、名称唯一性校验、删除前关联检查 |

### 6. 数据访问层（repository）

| 文件 | 说明 |
|------|------|
| `UserRepository` | 提供 findByUsername 方法 |
| `CategoryRepository` | 提供 existsByName 方法 |
| `NewsRepository` | 提供 7 种组合查询方法，支持分类、标题模糊、状态的自由组合筛选 |

所有 Repository 继承 `JpaRepository`，Spring Data JPA 根据方法命名规范自动生成 SQL 实现。Repository 方法命名与对应的 SQL 关系：

```
findByCategoryIdAndTitleContainingAndStatus
  → WHERE category_id = ? AND title LIKE %?% AND status = ?
```

### 7. 配置层（config）

| 文件 | 说明 |
|------|------|
| `CorsConfig` | CORS 跨域配置，允许所有来源/请求头/方法，支持凭证携带 |
| `JwtAuthenticationFilter` | JWT 认证过滤器，从 Authorization 头提取 Bearer token，验证后设置 SecurityContext |
| `SecurityConfig` | Security 安全规则，无状态会话，公开接口放行，管理接口需 ADMIN 角色 |

JWT 认证流程：
```
请求 → JwtAuthenticationFilter
  1. 从 Authorization 头提取 Bearer token
  2. Redis 黑名单检查 → 已注销则放行（不认证）
  3. JWT 签名+过期验证 → 无效则放行（不认证）
  4. 解析 userId + role → 设置 SecurityContext
→ 后续请求可直接通过 SecurityContextHolder 获取用户信息
```

### 8. 工具层（util）

| 文件 | 说明 |
|------|------|
| `JwtUtil` | JWT 令牌生成（含 userId/username/role 声明）、解析、验证、提取用户 ID 和角色 |

---

## 四、关键代码说明

### 4.1 认证与授权流程

```
登录: POST /api/auth/login
  → LoginRequest (username, password)
  → AuthServiceImpl.login()
    → UserRepository.findByUsername() 查找用户
    → PasswordEncoder.matches() BCrypt 校验密码
    → JwtUtil.generateToken() 生成 JWT
  ← LoginResponse (token, username, role)

后续请求:
  → JwtAuthenticationFilter.doFilterInternal()
    → extractToken() 提取 Bearer token
    → Redis 黑名单检查
    → JwtUtil.validateToken() 验证签名+过期
    → 设置 UsernamePasswordAuthenticationToken 到 SecurityContext
  → SecurityConfig 权限校验（ADMIN 角色才可访问 /api/admin/**）
```

### 4.2 统一响应格式

所有接口统一使用 `Result<T>` 返回：

```json
// 成功
{ "code": 200, "message": "成功", "data": { ... } }
// 失败
{ "code": 400, "message": "分类名称不能为空", "data": null }
```

### 4.3 新闻查询组合筛选

`NewsServiceImpl.getPublicNews()` 根据参数组合，动态选择 4 种查询路径：

| 分类 ID | 关键词 | 使用的 Repository 方法 |
|---------|--------|----------------------|
| 有 | 有 | `findByCategoryIdAndTitleContainingAndStatus` |
| 有 | 无 | `findByCategoryIdAndStatus` |
| 无 | 有 | `findByTitleContainingAndStatus` |
| 无 | 无 | `findByStatus` |

结果按创建时间倒序排列，仅返回 PUBLISHED 状态的新闻。

### 4.4 令牌黑名单注销机制

```java
// 注销时将令牌存入 Redis，24 小时过期
redisTemplate.opsForValue().set("blacklist:" + token, "1", 24, TimeUnit.HOURS);

// 过滤器每次请求先查黑名单
String blacklisted = redisTemplate.opsForValue().get("blacklist:" + token);
```

依赖 Redis 自动过期清理，无需手动维护黑名单数据。

### 4.5 安全规则（SecurityConfig）

| 路由 | 访问权限 |
|------|---------|
| `POST /api/auth/login` | 完全公开 |
| `GET /api/news/**` | 完全公开 |
| `GET /api/categories` | 完全公开 |
| `/api/admin/**` | 仅 ADMIN 角色 |
| 其余 | 需认证 |

CSRF 禁用（JWT 无状态认证不需要），会话策略为 STATELESS，在 `UsernamePasswordAuthenticationFilter` 之前插入 JWT 过滤器。

---

## 五、配置文件说明

### application.yml

| 配置项 | 值 | 说明 |
|--------|-----|------|
| `server.port` | 8080 | 服务端口 |
| `spring.datasource.url` | `jdbc:mysql://localhost:3306/news` | MySQL 数据库连接 |
| `spring.jpa.hibernate.ddl-auto` | update | 自动建表/更新表结构 |
| `spring.data.redis.host` | localhost | Redis 地址 |
| `spring.data.redis.port` | 6379 | Redis 端口 |
| `jwt.secret` | Base64 密钥 | HMAC-SHA 签名密钥（至少 256 位） |
| `jwt.expiration` | 86400000 | 令牌过期时间（毫秒，24 小时） |

---

## 六、启动方式

```bash
# 确保 MySQL 和 Redis 已启动

cd backend

# 编译
mvn clean package -DskipTests

# 启动
java -jar target/news-backend-1.0.0.jar

# 或直接 Maven 启动
mvn spring-boot:run
```

启动后访问：`http://localhost:8080`

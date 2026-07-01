## ADDED Requirements

### Requirement: 管理员登录
系统 SHALL 提供基于 JWT 的管理员登录功能，登录成功后返回 JWT token。

#### Scenario: 成功登录
- **WHEN** 管理员提交正确的用户名和密码
- **THEN** 系统返回 JWT token，状态码 200

#### Scenario: 密码错误
- **WHEN** 管理员提交错误的密码
- **THEN** 系统返回 401 状态码和错误提示

#### Scenario: 用户名不存在
- **WHEN** 管理员提交不存在的用户名
- **THEN** 系统返回 401 状态码和错误提示

### Requirement: Token 鉴权
系统 SHALL 要求所有受保护的 API 请求携带有效的 JWT token。

#### Scenario: 请求携带有效 Token
- **WHEN** 客户端在 Authorization header 中携带有效 JWT token
- **THEN** 系统正常处理请求

#### Scenario: 请求未携带 Token
- **WHEN** 客户端未在 Authorization header 中携带 JWT token
- **THEN** 系统返回 401 状态码

#### Scenario: 请求携带过期 Token
- **WHEN** 客户端携带已过期的 JWT token
- **THEN** 系统返回 401 状态码

### Requirement: 角色权限控制
系统 SHALL 提供管理员和普通用户两种角色，管理员角色可以访问后台管理接口。

#### Scenario: 管理员访问后台接口
- **WHEN** 具有管理员角色的用户访问后台管理接口
- **THEN** 系统允许访问

#### Scenario: 普通用户访问后台接口
- **WHEN** 不具备管理员角色的用户访问后台管理接口
- **THEN** 系统返回 403 状态码

### Requirement: 管理员登出
系统 SHALL 支持管理员登出，使当前 token 失效。

#### Scenario: 成功登出
- **WHEN** 管理员发起登出请求
- **THEN** 系统使当前 token 失效，返回 200 状态码

// 声明当前类所属的包路径
package com.news.dto;

/**
 * 登录响应 DTO
 * 登录成功后返回给客户端的信息，包含 JWT 令牌、用户名和角色
 */
// 登录响应数据类定义
public class LoginResponse {

    // 身份认证令牌，客户端需要在后续请求的 Header 中附带此令牌
    private String token;
    // 当前登录用户的用户名
    private String username;
    // 当前登录用户的角色，前端可据此控制界面功能显示
    private String role;

    // 全参构造方法，方便在 Service 层一行代码构建响应对象
    public LoginResponse(String token, String username, String role) {
        // 初始化令牌字段
        this.token = token;
        // 初始化用户名字段
        this.username = username;
        // 初始化角色字段
        this.role = role;
    }

    // 获取 JWT 令牌
    public String getToken() { return token; }
    // 设置 JWT 令牌
    public void setToken(String token) { this.token = token; }

    // 获取用户名
    public String getUsername() { return username; }
    // 设置用户名
    public void setUsername(String username) { this.username = username; }

    // 获取角色
    public String getRole() { return role; }
    // 设置角色
    public void setRole(String role) { this.role = role; }
}

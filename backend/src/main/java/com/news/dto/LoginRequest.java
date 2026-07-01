// 声明当前类所属的包路径
package com.news.dto;

// 导入 Jakarta Validation 的 @NotBlank 注解，用于参数非空校验
import jakarta.validation.constraints.NotBlank;

/**
 * 登录请求 DTO
 * 封装用户登录时提交的用户名和密码，包含基本的参数校验
 */
// 登录请求数据类定义
public class LoginRequest {

    // 参数校验注解：字段值不能为 null 且不能是空字符串或纯空格
    @NotBlank
    // 用户登录名
    private String username;

    // 参数校验注解：字段值不能为 null 且不能是空字符串或纯空格
    @NotBlank
    // 用户登录密码（明文传输，服务端加密校验）
    private String password;

    // 获取用户名
    public String getUsername() { return username; }
    // 设置用户名
    public void setUsername(String username) { this.username = username; }

    // 获取密码
    public String getPassword() { return password; }
    // 设置密码
    public void setPassword(String password) { this.password = password; }
}

// 声明当前类所属的包路径
package com.news.controller;

// 导入登录请求 DTO
import com.news.dto.LoginRequest;
// 导入登录响应 DTO
import com.news.dto.LoginResponse;
// 导入统一响应结果封装类
import com.news.dto.Result;
// 导入认证服务接口
import com.news.service.AuthService;
// 导入 Jakarta Validation 的 @Valid 注解，用于触发参数校验
import jakarta.validation.Valid;
// 导入 Spring Web MVC 注解
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 * 提供用户登录和注销的 REST 接口
 */
// Spring 注解：标记此类为 RESTful 控制器，所有方法默认返回 JSON 格式
@RestController
// 类级别请求路径映射：所有该控制器的接口都以 /api/auth 开头
@RequestMapping("/api/auth")
// 认证控制器类定义
public class AuthController {

    // 认证服务依赖
    private final AuthService authService;

    // 构造函数，通过依赖注入获取 AuthService 实例
    public AuthController(AuthService authService) {
        // 赋值认证服务
        this.authService = authService;
    }

    /**
     * 用户登录
     * 校验用户名和密码，登录成功返回 JWT 令牌
     * @param request 登录请求体（需包含 username 和 password）
     * @return 登录结果，包含 token、用户名和角色
     */
    // 映射 HTTP POST 请求到 /api/auth/login 路径
    @PostMapping("/login")
    // 登录接口方法
    // @Valid：触发 LoginRequest 中的 @NotBlank 等校验注解
    // @RequestBody：将请求体 JSON 自动反序列化为 LoginRequest 对象
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        // 调用认证服务登录，将结果封装为标准成功响应返回
        return Result.success(authService.login(request));
    }

    /**
     * 用户注销
     * 从 Authorization 头中提取 Bearer token 并加入黑名单使其失效
     * @param token Authorization 请求头的完整值（含 "Bearer " 前缀）
     * @return 操作成功提示
     */
    // 映射 HTTP POST 请求到 /api/auth/logout 路径
    @PostMapping("/logout")
    // 注销接口方法
    // @RequestHeader("Authorization")：从请求头中获取 Authorization 的值
    public Result<Void> logout(@RequestHeader("Authorization") String token) {
        // 如果请求头不存在或格式不正确
        if (token == null || !token.startsWith("Bearer ")) {
            // 直接返回成功（幂等处理）
            return Result.success();
        }
        // 截取 "Bearer " 后的实际令牌，调用服务层将其加入黑名单
        authService.logout(token.substring(7));
        // 返回成功响应
        return Result.success();
    }
}

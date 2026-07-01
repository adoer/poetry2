// 声明当前类所属的包路径
package com.news.service;

// 导入登录请求 DTO
import com.news.dto.LoginRequest;
// 导入登录响应 DTO
import com.news.dto.LoginResponse;

/**
 * 认证服务接口
 * 定义用户登录和注销的核心业务方法
 */
// 认证服务接口定义
public interface AuthService {

    /**
     * 用户登录
     * @param request 登录请求，包含用户名和密码
     * @return 登录成功后的响应，包含 JWT 令牌、用户名和角色
     */
    // 登录方法：接收登录请求，返回包含 JWT 令牌的登录响应
    LoginResponse login(LoginRequest request);

    /**
     * 用户注销
     * @param token 当前用户的 JWT 令牌，将被加入黑名单
     */
    // 注销方法：将令牌加入黑名单使其失效
    void logout(String token);
}

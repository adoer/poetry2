// 声明当前类所属的包路径
package com.news.config;

// 导入 Spring @Bean 注解，用于声明 Bean 实例
import org.springframework.context.annotation.Bean;
// 导入 Spring @Configuration 注解，标记此类为配置类
import org.springframework.context.annotation.Configuration;
// 导入 Spring Security 的 HttpSecurity 配置类，用于定义安全规则
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// 导入启用 Web 安全注解
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
// 导入会话创建策略枚举
import org.springframework.security.config.http.SessionCreationPolicy;
// 导入 BCrypt 密码编码器
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// 导入密码编码器接口
import org.springframework.security.crypto.password.PasswordEncoder;
// 导入安全过滤器链
import org.springframework.security.web.SecurityFilterChain;
// 导入用户名密码认证过滤器的位置常量
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 安全配置
 * 配置无状态会话管理、JWT 过滤器、接口权限规则和密码编码器
 */
// Spring 注解：标记此类为配置类
@Configuration
// Spring Security 注解：启用 Web 安全功能
@EnableWebSecurity
// 安全配置类定义
public class SecurityConfig {

    // JWT 过滤器的依赖
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // 构造函数，通过依赖注入获取 JwtAuthenticationFilter 实例
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        // 保存注入的 JWT 过滤器
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    /**
     * 配置安全过滤器链
     * - 启用 CORS、禁用 CSRF（基于 Token 的认证不需要）
     * - 无状态会话（SessionCreationPolicy.STATELESS）
     * - 公开接口：登录、新闻列表、分类列表允许匿名访问
     * - 管理接口：/api/admin/** 需要 ADMIN 角色
     * - 其余接口需认证
     * - 在 UsernamePasswordAuthenticationFilter 之前添加 JWT 过滤器
     * @param http HttpSecurity 配置对象
     * @return 构建好的 SecurityFilterChain
     */
    // Spring 注解：将此方法的返回值注册为一个 Bean
    @Bean
    // 配置安全过滤器链的方法
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // 开始配置 HttpSecurity
        http
            // 启用 CORS（跨域）支持，使用 CorsConfig 中定义的配置
            .cors(cors -> {})
            // 禁用 CSRF 保护（JWT 基于 Token 认证，不需要 CSRF 防护）
            .csrf(csrf -> csrf.disable())
            // 设置会话创建策略为无状态（STATELESS），服务器不创建或使用 HTTP 会话
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // 开始配置 URL 授权规则
            .authorizeHttpRequests(auth -> auth
                // 登录接口允许所有用户匿名访问（无需认证）
                .requestMatchers("/api/auth/login").permitAll()
                // 公开新闻查询接口允许所有用户匿名访问
                .requestMatchers("/api/news/**").permitAll()
                // 分类列表接口允许所有用户匿名访问
                .requestMatchers("/api/categories").permitAll()
                // 管理后台接口仅允许具有 ADMIN 角色的用户访问
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                // 其余所有请求都需要先通过认证
                .anyRequest().authenticated()
            )
            // 在 Spring Security 内置的 UsernamePasswordAuthenticationFilter 之前插入 JWT 认证过滤器
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
            // 这样 JWT 过滤器可以先解析令牌并设置安全上下文

        // 构建并返回 SecurityFilterChain 实例
        return http.build();
    }

    /**
     * 密码编码器
     * 使用 BCrypt 强哈希算法对密码进行加密和验证
     * @return BCryptPasswordEncoder 实例
     */
    // Spring 注解：将此方法的返回值注册为一个 Bean
    @Bean
    // 创建密码编码器 Bean 的方法
    public PasswordEncoder passwordEncoder() {
        // 返回 BCrypt 密码编码器，它使用 salt + 哈希算法对密码进行加密
        return new BCryptPasswordEncoder();
    }
}

// 声明当前类所属的包路径
package com.news.config;

// 导入 Spring @Bean 注解，用于声明 Bean 实例
import org.springframework.context.annotation.Bean;
// 导入 Spring @Configuration 注解，标记此类为配置类
import org.springframework.context.annotation.Configuration;
// 导入 Spring CORS 配置类，用于设置跨域规则
import org.springframework.web.cors.CorsConfiguration;
// 导入基于 URL 路径的 CORS 配置源，支持按路径模式配置
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
// 导入 Spring CORS 过滤器，用于拦截和处理跨域请求
import org.springframework.web.filter.CorsFilter;

/**
 * 跨域资源共享（CORS）配置
 * 允许所有来源、请求头和方法的前端跨域访问，用于支持前后端分离开发
 */
// Spring 注解：标记此类为配置类，Spring 会将其中的 @Bean 方法注册到容器中
@Configuration
// CORS 配置类定义
public class CorsConfig {

    /**
     * 配置 CORS 过滤器
     * - 允许携带凭证（Cookie、Authorization 头等）
     * - 允许所有来源（生产环境可收紧）
     * - 允许所有请求头
     * - 允许所有 HTTP 方法（GET、POST、PUT、DELETE 等）
     * - 对所有路径生效
     * @return 配置好的 CorsFilter 实例
     */
    // Spring 注解：将此方法的返回值注册为一个 Bean，默认方法名即为 Bean 名称
    @Bean
    // 创建并配置 CORS 过滤器的方法
    public CorsFilter corsFilter() {
        // 创建 CORS 配置对象
        CorsConfiguration config = new CorsConfiguration();
        // 允许跨域请求携带凭证（如 Cookie、Authorization 请求头）
        config.setAllowCredentials(true);
        // 允许所有来源的跨域请求（生产环境建议替换为具体的前端域名）
        config.addAllowedOriginPattern("*");
        // 允许所有自定义请求头
        config.addAllowedHeader("*");
        // 允许所有 HTTP 方法（GET、POST、PUT、DELETE、PATCH 等）
        config.addAllowedMethod("*");

        // 创建基于 URL 路径的 CORS 配置源
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 对所有路径（/**）应用上面的 CORS 配置
        source.registerCorsConfiguration("/**", config);
        // 创建并返回配置好的 CORS 过滤器实例
        return new CorsFilter(source);
    }
}

// 声明当前类所属的包路径，与项目目录结构对应
package com.news;

// 导入 Spring Boot 启动类，用于启动应用
import org.springframework.boot.SpringApplication;
// 导入 Spring Boot 自动配置注解，启用自动化配置
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 新闻管理系统的 Spring Boot 启动入口类
 * 负责初始化整个应用上下文，启动嵌入式 Web 服务器
 */
// 组合注解：@EnableAutoConfiguration + @ComponentScan + @Configuration，自动扫描并配置整个 Spring 应用
@SpringBootApplication
// 主应用类定义
public class NewsApplication {

    /**
     * 应用主入口方法
     * @param args 命令行参数，可传递 Spring Boot 配置参数（如 --server.port=8081）
     */
    // JVM 启动入口方法，整个应用的起点
    public static void main(String[] args) {
        // 调用 SpringApplication 的静态 run 方法，初始化 Spring 容器并启动内置 Web 服务器
        SpringApplication.run(NewsApplication.class, args);
    }
}

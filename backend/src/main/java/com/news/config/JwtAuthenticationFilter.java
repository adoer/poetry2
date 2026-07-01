// 声明当前类所属的包路径
package com.news.config;

// 导入 JWT 工具类，用于令牌的解析和验证
import com.news.util.JwtUtil;
// 导入 Jakarta Servlet 过滤器链接口
import jakarta.servlet.FilterChain;
// 导入 Jakarta Servlet 异常类
import jakarta.servlet.ServletException;
// 导入 Jakarta Servlet HTTP 请求接口
import jakarta.servlet.http.HttpServletRequest;
// 导入 Jakarta Servlet HTTP 响应接口
import jakarta.servlet.http.HttpServletResponse;
// 导入 Spring Data Redis 模板，用于操作 Redis 中的字符串数据
import org.springframework.data.redis.core.StringRedisTemplate;
// 导入 Spring Security 认证令牌类，用于存储认证信息
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// 导入 Spring Security 权限封装类
import org.springframework.security.core.authority.SimpleGrantedAuthority;
// 导入 Spring Security 安全上下文持有者，用于设置和获取当前用户认证信息
import org.springframework.security.core.context.SecurityContextHolder;
// 导入 Spring @Component 注解，声明此类为 Spring 管理的 Bean
import org.springframework.stereotype.Component;
// 导入 Spring 字符串工具类，用于空字符串判断
import org.springframework.util.StringUtils;
// 导入 Spring 过滤器基类，确保每个请求只执行一次过滤
import org.springframework.web.filter.OncePerRequestFilter;

// 导入 I/O 异常类
import java.io.IOException;
// 导入 List 集合类
import java.util.List;

/**
 * JWT 认证过滤器
 * 继承 OncePerRequestFilter，确保每个请求只执行一次过滤。
 * 从 Authorization 头中提取 JWT 令牌，校验其有效性并在通过后设置 Spring Security 安全上下文
 */
// Spring 注解：标记此类为组件，自动注册到 Spring 容器中
@Component
// 继承 OncePerRequestFilter，保证过滤器在请求生命周期中只被执行一次
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // JWT 工具类实例，不可变
    private final JwtUtil jwtUtil;
    // Redis 模板实例，用于操作黑名单数据
    private final StringRedisTemplate redisTemplate;

    // 构造函数，通过依赖注入获取 JwtUtil 和 StringRedisTemplate 实例
    public JwtAuthenticationFilter(JwtUtil jwtUtil, StringRedisTemplate redisTemplate) {
        // 保存注入的 JWT 工具类
        this.jwtUtil = jwtUtil;
        // 保存注入的 Redis 模板
        this.redisTemplate = redisTemplate;
    }

    /**
     * 核心过滤逻辑
     * 1. 从请求头中提取 Bearer token
     * 2. 检查令牌是否在黑名单中（已注销）
     * 3. 验证令牌的有效性（签名、过期时间等）
     * 4. 解析用户 ID 和角色，构建 Authentication 对象并设置到 SecurityContext
     * @param request     当前 HTTP 请求
     * @param response    当前 HTTP 响应
     * @param filterChain 过滤器链
     */
    // 重写父类 OncePerRequestFilter 的方法
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    // HTTP 请求对象
                                    HttpServletResponse response,
                                    // HTTP 响应对象
                                    FilterChain filterChain) throws ServletException, IOException {
                                    // 过滤器链，用于继续传递请求

        // 从 HTTP 请求头中提取 JWT 令牌字符串
        String token = extractToken(request);

        // 如果提取到的令牌不为空
        if (StringUtils.hasText(token)) {
            // 在 Redis 中查找以 "blacklist:" + token 为键的值
            String blacklisted = redisTemplate.opsForValue().get("blacklist:" + token);
            // 如果 Redis 中存在该黑名单记录，说明此令牌已被注销
            if (blacklisted != null) {
                // 直接放行请求（不设置认证信息，后续会被 Security 判定为未认证）
                filterChain.doFilter(request, response);
                // 提前返回，不继续执行下面的认证逻辑
                return;
            }

            // 调用 JWT 工具类验证令牌的签名和过期时间
            if (jwtUtil.validateToken(token)) {
                // 从令牌中解析出用户 ID
                Long userId = jwtUtil.getUserId(token);
                // 从令牌中解析出用户角色
                String role = jwtUtil.getRole(token);

                // 创建 Spring Security 的认证令牌对象
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                // principal 为用户 ID，credentials 为 null（无密码凭证）
                                userId, null,
                                // 根据角色创建权限列表，格式为 "ROLE_ADMIN" 或 "ROLE_USER"
                                List.of(new SimpleGrantedAuthority("ROLE_" + role))
                        );
                // 将认证令牌设置到 Spring Security 的安全上下文中
                SecurityContextHolder.getContext().setAuthentication(authentication);
                // 后续的请求可以通过 SecurityContextHolder 获取当前用户信息
            }
        }

        // 继续执行过滤链中的下一个过滤器或目标 Servlet
        filterChain.doFilter(request, response);
    }

    /**
     * 从 HTTP 请求头中提取 JWT 令牌
     * 期望格式："Authorization: Bearer <token>"
     * @param request HTTP 请求
     * @return 提取出的令牌字符串，如果头缺失或格式错误返回 null
     */
    // 从请求头中提取令牌的私有方法
    private String extractToken(HttpServletRequest request) {
        // 获取 Authorization 请求头的值
        String bearer = request.getHeader("Authorization");
        // 如果请求头存在且以 "Bearer " 开头
        if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
            // 截取 "Bearer " 之后的实际令牌内容（"Bearer " 长度为 7）
            return bearer.substring(7);
        }
        // 请求头不存在或格式不正确，返回 null
        return null;
    }
}

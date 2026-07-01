// 声明当前类所属的包路径
package com.news.util;

// 导入 JJWT 库的所有类，用于 JWT 令牌的创建和解析
import io.jsonwebtoken.*;
// 导入 JJWT 安全密钥工具类，用于生成 HMAC 密钥
import io.jsonwebtoken.security.Keys;
// 导入 Spring @Value 注解，用于注入配置文件中的属性值
import org.springframework.beans.factory.annotation.Value;
// 导入 Spring @Component 注解，声明此类为 Spring 管理的 Bean
import org.springframework.stereotype.Component;

// 导入 Java 加密扩展的 SecretKey 接口，用于表示 HMAC 密钥
import javax.crypto.SecretKey;
// 导入标准字符集常量，用于字符串与字节数组的转换
import java.nio.charset.StandardCharsets;
// 导入 Java 日期类，用于设置 JWT 的签发时间和过期时间
import java.util.Date;

/**
 * JWT（JSON Web Token）工具类
 * 负责令牌的生成、解析和验证，使用 HMAC-SHA 算法签名
 */
// Spring 注解：标记此类为组件，会自动扫描并注册到 Spring 容器中
@Component
// JWT 工具类定义
public class JwtUtil {

    // 不可变的签名密钥，用于令牌的签名和验证
    private final SecretKey key;
    // 不可变的过期时间配置，单位为毫秒
    private final long expiration;

    // 通过构造函数注入 application.yml 中的 jwt.secret 配置项
    public JwtUtil(@Value("${jwt.secret}") String secret,
                   // 通过构造函数注入 application.yml 中的 jwt.expiration 配置项
                   @Value("${jwt.expiration}") long expiration) {
        // 将字符串密钥通过 UTF-8 编码转为字节数组，再生成符合 HMAC-SHA 算法要求的 SecretKey 对象
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        // 保存令牌过期时间配置
        this.expiration = expiration;
    }

    /**
     * 生成 JWT 令牌
     * 令牌中包含用户 ID（作为 subject）、用户名和角色等声明信息
     * @param userId   用户 ID
     * @param username 用户名
     * @param role     用户角色 ADMIN / USER
     * @return 签名的 JWT 字符串
     */
    // 生成 JWT 令牌的公开方法
    public String generateToken(Long userId, String username, String role) {
        // 获取当前系统时间作为令牌签发时间
        Date now = new Date();
        // 使用 JJWT 建造者模式创建 JWT
        return Jwts.builder()
                // 设置主题（sub 声明）为用户 ID 的字符串形式
                .subject(userId.toString())
                // 设置自定义声明：用户名
                .claim("username", username)
                // 设置自定义声明：用户角色
                .claim("role", role)
                // 设置签发时间（iat 声明）为当前时间
                .issuedAt(now)
                // 设置过期时间（exp 声明）为当前时间 + 配置的过期毫秒数
                .expiration(new Date(now.getTime() + expiration))
                // 使用 HMAC 密钥对令牌进行签名
                .signWith(key)
                // 完成构建并压缩为 JWT 字符串格式（Base64 编码的三段式 token）
                .compact();
    }

    /**
     * 解析 JWT 令牌并获取其 Claims（载荷）
     * @param token JWT 字符串
     * @return 解析后的 Claims 对象，包含所有声明信息
     */
    // 解析 JWT 令牌的公开方法
    public Claims parseToken(String token) {
        // 使用 JJWT 解析器建造者创建解析器
        return Jwts.parser()
                // 设置验证签名所需的密钥
                .verifyWith(key)
                // 构建解析器实例
                .build()
                // 解析 JWT 令牌，同时验证签名和过期时间
                .parseSignedClaims(token)
                // 获取令牌的载荷部分（Claims 对象），包含所有声明信息
                .getPayload();
    }

    /**
     * 验证 JWT 令牌是否有效
     * 校验签名是否正确以及令牌是否已过期
     * @param token JWT 字符串
     * @return 有效返回 true，否则返回 false
     */
    // 验证 JWT 令牌有效性的公开方法
    public boolean validateToken(String token) {
        // 尝试解析令牌
        try {
            // 调用解析方法，如果签名无效或令牌已过期，会抛出异常
            parseToken(token);
            // 解析成功，令牌有效，返回 true
            return true;
        // 捕获 JWT 相关异常（签名错误、过期等）或参数异常
        } catch (JwtException | IllegalArgumentException e) {
            // 令牌无效，返回 false
            return false;
        }
    }

    /**
     * 从令牌中提取用户 ID
     * @param token JWT 字符串
     * @return 用户 ID（存储在 subject 字段中）
     */
    // 从令牌中提取用户 ID 的方法
    public Long getUserId(String token) {
        // 解析令牌，获取 subject 字段（用户 ID 字符串），再转为 Long 类型
        return Long.parseLong(parseToken(token).getSubject());
    }

    /**
     * 从令牌中提取用户角色
     * @param token JWT 字符串
     * @return 用户角色字符串（ADMIN 或 USER）
     */
    // 从令牌中提取用户角色的方法
    public String getRole(String token) {
        // 解析令牌，获取自定义声明 "role" 的值，并指定返回类型为 String
        return parseToken(token).get("role", String.class);
    }
}

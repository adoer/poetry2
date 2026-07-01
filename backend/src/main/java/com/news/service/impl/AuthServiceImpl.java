// 声明实现类所属的包路径
package com.news.service.impl;

// 导入登录请求 DTO
import com.news.dto.LoginRequest;
// 导入登录响应 DTO
import com.news.dto.LoginResponse;
// 导入用户实体类
import com.news.entity.User;
// 导入业务异常类
import com.news.exception.BusinessException;
// 导入用户数据访问接口
import com.news.repository.UserRepository;
// 导入认证服务接口
import com.news.service.AuthService;
// 导入 JWT 工具类
import com.news.util.JwtUtil;
// 导入 Redis 模板，用于令牌黑名单存储
import org.springframework.data.redis.core.StringRedisTemplate;
// 导入密码编码器
import org.springframework.security.crypto.password.PasswordEncoder;
// 导入 Spring @Service 注解
import org.springframework.stereotype.Service;

// 导入时间单位枚举，用于设置黑名单过期时长
import java.util.concurrent.TimeUnit;

/**
 * 认证服务实现类
 * 处理用户登录认证和 JWT 令牌注销，集成 Redis 实现令牌黑名单机制
 */
// Spring 注解：标记此类为服务层组件，自动注册到 Spring 容器中
@Service
// 实现 AuthService 接口
public class AuthServiceImpl implements AuthService {

    // 用户 Repository 依赖
    private final UserRepository userRepository;
    // 密码编码器依赖
    private final PasswordEncoder passwordEncoder;
    // JWT 工具类依赖
    private final JwtUtil jwtUtil;
    // Redis 模板依赖
    private final StringRedisTemplate redisTemplate;

    // 通过构造注入所有依赖
    public AuthServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           JwtUtil jwtUtil,
                           StringRedisTemplate redisTemplate) {
        // 赋值用户 Repository
        this.userRepository = userRepository;
        // 赋值密码编码器
        this.passwordEncoder = passwordEncoder;
        // 赋值 JWT 工具类
        this.jwtUtil = jwtUtil;
        // 赋值 Redis 模板
        this.redisTemplate = redisTemplate;
    }

    /**
     * 用户登录流程
     * 1. 根据用户名查找用户，不存在则抛出业务异常（模糊提示避免信息泄露）
     * 2. 校验密码是否匹配，不匹配同样抛出模糊的业务异常
     * 3. 生成 JWT 令牌并封装登录响应返回
     * @param request 登录请求（用户名 + 密码）
     * @return 包含 JWT 令牌、用户名和角色的登录响应
     */
    // 重写接口方法
    @Override
    // 登录业务方法实现
    public LoginResponse login(LoginRequest request) {
        // 根据用户名查询用户信息
        User user = userRepository.findByUsername(request.getUsername())
                // 如果未找到用户，抛出业务异常（不区分"用户不存在"和"密码错误"，提高安全性）
                .orElseThrow(() -> new BusinessException("用户名或密码错误"));

        // 使用 BCrypt 校验明文密码是否与加密密码匹配
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            // 密码不匹配，抛出相同的模糊提示，防止通过错误信息枚举有效用户名
            throw new BusinessException("用户名或密码错误");
        }

        // 登录成功，生成包含用户 ID、用户名和角色的 JWT 令牌
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole().name());
        // 封装并返回登录响应（令牌 + 用户名 + 角色）
        return new LoginResponse(token, user.getUsername(), user.getRole().name());
    }

    /**
     * 用户注销 - 将令牌加入 Redis 黑名单
     * 后续请求携带此令牌时，JwtAuthenticationFilter 会检测到黑名单记录并拒绝认证
     * @param token 需要失效的 JWT 令牌
     */
    // 重写接口方法
    @Override
    // 注销业务方法实现
    public void logout(String token) {
        // 将令牌以 "blacklist:{token}" 为键存入 Redis，值为 "1"，过期时间为 24 小时
        // 后续 JWT 过滤器在解析令牌前会先查询 Redis 黑名单，若命中则拒绝认证
        redisTemplate.opsForValue().set("blacklist:" + token, "1", 24, TimeUnit.HOURS);
    }
}

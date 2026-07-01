// 声明当前类所属的包路径
package com.news.entity;

// 导入 Jakarta Persistence (JPA) 注解和类，用于对象关系映射（ORM）
import jakarta.persistence.*;
// 导入 Java 8 日期时间类，用于存储创建时间
import java.time.LocalDateTime;

/**
 * 用户实体类
 * 映射数据库 users 表，存储系统用户的账户信息和角色权限
 */
// JPA 注解：标记此类为 JPA 实体，与数据库表对应
@Entity
// JPA 注解：指定映射的数据库表名为 "users"
@Table(name = "users")
// 用户实体类定义
public class User {

    // JPA 注解：标记该字段为表的主键
    @Id
    // JPA 注解：主键生成策略为数据库自增（IDENTITY），依赖数据库 auto_increment
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // 用户 ID，包装类型 Long 可区分值为 null 和 0
    private Long id;

    // JPA 注解：映射数据库列，not null + unique 约束
    @Column(nullable = false, unique = true)
    // 用户登录名
    private String username;

    // JPA 注解：映射数据库列，not null 约束
    @Column(nullable = false)
    // 用户密码，数据库中存储的是 BCrypt 加密后的哈希值
    private String password;

    // JPA 注解：枚举映射方式为字符串（存储枚举常量的名称而非序号）
    @Enumerated(EnumType.STRING)
    // JPA 注解：映射数据库列，not null 约束
    @Column(nullable = false)
    // 用户角色，控制可访问的接口和功能
    private UserRole role;

    // JPA 注解：该列不允许被更新（只在插入时设置值）
    @Column(updatable = false)
    // 账户创建时间戳
    private LocalDateTime createdAt;

    // JPA 生命周期注解：在实体持久化（INSERT）之前自动调用此方法
    @PrePersist
    // 实体插入前的自动回调方法
    protected void onCreate() {
        // 将当前系统时间赋给 createdAt 字段
        this.createdAt = LocalDateTime.now();
    }

    // 获取用户 ID
    public Long getId() { return id; }
    // 设置用户 ID
    public void setId(Long id) { this.id = id; }

    // 获取用户名
    public String getUsername() { return username; }
    // 设置用户名
    public void setUsername(String username) { this.username = username; }

    // 获取加密密码
    public String getPassword() { return password; }
    // 设置加密密码
    public void setPassword(String password) { this.password = password; }

    // 获取用户角色
    public UserRole getRole() { return role; }
    // 设置用户角色
    public void setRole(UserRole role) { this.role = role; }

    // 获取创建时间
    public LocalDateTime getCreatedAt() { return createdAt; }
    // 设置创建时间
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

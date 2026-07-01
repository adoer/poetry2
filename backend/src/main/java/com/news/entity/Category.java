// 声明当前类所属的包路径
package com.news.entity;

// 导入 Jackson 注解 @JsonFormat，用于控制日期序列化格式
import com.fasterxml.jackson.annotation.JsonFormat;
// 导入 Jakarta Persistence (JPA) 注解和类
import jakarta.persistence.*;
// 导入 Java 8 日期时间类
import java.time.LocalDateTime;

/**
 * 新闻分类实体类
 * 映射数据库 categories 表，用于对新闻进行归类管理
 */
// JPA 注解：标记此类为 JPA 实体，与数据库表对应
@Entity
// JPA 注解：指定映射的数据库表名为 "categories"
@Table(name = "categories")
// 分类实体类定义
public class Category {

    // JPA 注解：标记该字段为表的主键
    @Id
    // JPA 注解：主键生成策略为数据库自增
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // 分类 ID，自增主键
    private Long id;

    // JPA 注解：映射数据库列，not null + unique 约束
    @Column(nullable = false, unique = true)
    // 分类名称（如 "科技"、"体育"、"娱乐" 等）
    private String name;

    // Jackson 注解：指定日期序列化为 JSON 时的格式
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    // JPA 注解：该列不允许被更新（只在插入时设置值）
    @Column(updatable = false)
    // 分类创建时间戳
    private LocalDateTime createdAt;

    // Jackson 注解：指定日期序列化为 JSON 时的格式
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    // 分类最后更新时间戳
    private LocalDateTime updatedAt;

    // JPA 生命周期注解：在实体持久化（INSERT）之前自动调用此方法
    @PrePersist
    // 实体插入前的自动回调方法
    protected void onCreate() {
        // 将当前系统时间赋给 createdAt 字段
        this.createdAt = LocalDateTime.now();
        // 将当前系统时间赋给 updatedAt 字段
        this.updatedAt = LocalDateTime.now();
    }

    // JPA 生命周期注解：在实体更新（UPDATE）之前自动调用此方法
    @PreUpdate
    // 实体更新前的自动回调方法
    protected void onUpdate() {
        // 将当前系统时间刷新到 updatedAt 字段
        this.updatedAt = LocalDateTime.now();
    }

    // 获取分类 ID
    public Long getId() { return id; }
    // 设置分类 ID
    public void setId(Long id) { this.id = id; }

    // 获取分类名称
    public String getName() { return name; }
    // 设置分类名称
    public void setName(String name) { this.name = name; }

    // 获取创建时间
    public LocalDateTime getCreatedAt() { return createdAt; }
    // 设置创建时间
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    // 获取更新时间
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    // 设置更新时间
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

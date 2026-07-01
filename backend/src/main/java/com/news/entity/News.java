// 声明当前类所属的包路径
package com.news.entity;

// 导入 Jakarta Persistence (JPA) 注解和类，用于对象关系映射
import jakarta.persistence.*;
// 导入 Java 8 日期时间类，用于存储时间戳
import java.time.LocalDateTime;

/**
 * 新闻实体类
 * 映射数据库 news 表，存储新闻的完整信息，包括标题、正文、分类和发布状态
 */
// JPA 注解：标记此类为 JPA 实体，与数据库表对应
@Entity
// JPA 注解：指定映射的数据库表名为 "news"
@Table(name = "news")
// 新闻实体类定义
public class News {

    // JPA 注解：标记该字段为表的主键
    @Id
    // JPA 注解：主键生成策略为数据库自增
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // 新闻 ID，自增主键
    private Long id;

    // JPA 注解：映射数据库列，not null 约束
    @Column(nullable = false)
    // 新闻标题
    private String title;

    // JPA 注解：映射数据库列，not null 约束，列类型定义为 TEXT（支持大文本存储）
    @Column(nullable = false, columnDefinition = "TEXT")
    // 新闻正文，富文本格式
    private String content;

    // 新闻摘要，简短介绍文字
    private String summary;

    // 新闻封面图 URL 链接
    private String coverImage;

    // JPA 注解：映射数据库列名为 "category_id"，not null 约束
    @Column(name = "category_id", nullable = false)
    // 分类 ID，外键逻辑关联 Category 表
    private Long categoryId;

    // JPA 注解：枚举映射方式为字符串
    @Enumerated(EnumType.STRING)
    // JPA 注解：映射数据库列，not null 约束
    @Column(nullable = false)
    // 新闻发布状态，决定是否对普通用户可见
    private NewsStatus status;

    // JPA 注解：该列不允许被更新（只在插入时设置值）
    @Column(updatable = false)
    // 新闻创建时间戳
    private LocalDateTime createdAt;

    // 新闻最后更新时间戳
    private LocalDateTime updatedAt;

    // JPA 生命周期注解：在实体持久化（INSERT）之前自动调用此方法
    @PrePersist
    // 实体插入前的自动回调方法
    protected void onCreate() {
        // 将当前系统时间赋给 createdAt 字段
        this.createdAt = LocalDateTime.now();
        // 将当前系统时间赋给 updatedAt 字段（首次创建时两个时间相同）
        this.updatedAt = LocalDateTime.now();
    }

    // JPA 生命周期注解：在实体更新（UPDATE）之前自动调用此方法
    @PreUpdate
    // 实体更新前的自动回调方法
    protected void onUpdate() {
        // 将当前系统时间刷新到 updatedAt 字段
        this.updatedAt = LocalDateTime.now();
    }

    // 获取新闻 ID
    public Long getId() { return id; }
    // 设置新闻 ID
    public void setId(Long id) { this.id = id; }

    // 获取新闻标题
    public String getTitle() { return title; }
    // 设置新闻标题
    public void setTitle(String title) { this.title = title; }

    // 获取新闻正文
    public String getContent() { return content; }
    // 设置新闻正文
    public void setContent(String content) { this.content = content; }

    // 获取新闻摘要
    public String getSummary() { return summary; }
    // 设置新闻摘要
    public void setSummary(String summary) { this.summary = summary; }

    // 获取封面图 URL
    public String getCoverImage() { return coverImage; }
    // 设置封面图 URL
    public void setCoverImage(String coverImage) { this.coverImage = coverImage; }

    // 获取分类 ID
    public Long getCategoryId() { return categoryId; }
    // 设置分类 ID
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    // 获取新闻状态
    public NewsStatus getStatus() { return status; }
    // 设置新闻状态
    public void setStatus(NewsStatus status) { this.status = status; }

    // 获取创建时间
    public LocalDateTime getCreatedAt() { return createdAt; }
    // 设置创建时间
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    // 获取更新时间
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    // 设置更新时间
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

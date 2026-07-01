// 声明当前类所属的包路径
package com.news.dto;

// 导入新闻状态枚举，用于传输新闻状态信息
import com.news.entity.NewsStatus;

/**
 * 新闻数据传输对象
 * 用于前后端之间的新闻数据传递，包含新闻的完整信息和格式化后的时间字符串
 */
// DTO 类定义，与实体类 News 对应但字段更灵活
public class NewsDTO {

    // 新闻 ID，与实体类一致
    private Long id;
    // 新闻标题文本
    private String title;
    // 新闻正文，可能包含 HTML 标签
    private String content;
    // 简要概述，用于列表页展示
    private String summary;
    // 封面图片链接地址
    private String coverImage;
    // 新闻关联的分类 ID
    private Long categoryId;
    // 冗余字段，由 Service 层根据 categoryId 查询并填充
    private String categoryName;
    // 新闻发布状态枚举
    private NewsStatus status;
    // 创建时间的格式化字符串（实体中是 LocalDateTime）
    private String createdAt;
    // 更新时间的格式化字符串（实体中是 LocalDateTime）
    private String updatedAt;

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

    // 获取分类名称
    public String getCategoryName() { return categoryName; }
    // 设置分类名称
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    // 获取新闻状态
    public NewsStatus getStatus() { return status; }
    // 设置新闻状态
    public void setStatus(NewsStatus status) { this.status = status; }

    // 获取格式化后的创建时间
    public String getCreatedAt() { return createdAt; }
    // 设置格式化后的创建时间
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    // 获取格式化后的更新时间
    public String getUpdatedAt() { return updatedAt; }
    // 设置格式化后的更新时间
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
}

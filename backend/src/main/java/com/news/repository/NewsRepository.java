// 声明当前类所属的包路径
package com.news.repository;

// 导入新闻实体类
import com.news.entity.News;
// 导入 Spring Data 的分页对象接口
import org.springframework.data.domain.Page;
// 导入 Spring Data 的分页参数接口
import org.springframework.data.domain.Pageable;
// 导入 Spring Data JPA 的 JpaRepository 接口
import org.springframework.data.jpa.repository.JpaRepository;
// 导入新闻状态枚举
import com.news.entity.NewsStatus;

/**
 * 新闻数据访问层
 * 继承 JpaRepository 提供对 news 表的基础 CRUD 操作，并定义按分类、标题、状态的组合查询方法
 */
// 继承 JpaRepository<News, Long>：实体类型为 News，主键类型为 Long
public interface NewsRepository extends JpaRepository<News, Long> {

    // 按分类 ID 分页查询所有状态的新闻，自动生成：WHERE category_id = ?
    Page<News> findByCategoryId(Long categoryId, Pageable pageable);

    // 按标题模糊分页查询所有状态的新闻，自动生成：WHERE title LIKE %?%
    Page<News> findByTitleContaining(String title, Pageable pageable);

    // 按状态分页查询新闻（如仅查询已发布的新闻），自动生成：WHERE status = ?
    Page<News> findByStatus(NewsStatus status, Pageable pageable);

    // 按分类 ID 和状态分页查询新闻，自动生成：WHERE category_id = ? AND status = ?
    Page<News> findByCategoryIdAndStatus(Long categoryId, NewsStatus status, Pageable pageable);

    // 按标题模糊匹配和状态分页查询新闻，自动生成：WHERE title LIKE %?% AND status = ?
    Page<News> findByTitleContainingAndStatus(String title, NewsStatus status, Pageable pageable);

    // 按分类 ID、标题模糊匹配和状态分页查询新闻（多条件组合），自动生成：WHERE category_id = ? AND title LIKE %?% AND status = ?
    Page<News> findByCategoryIdAndTitleContainingAndStatus(Long categoryId, String title, NewsStatus status, Pageable pageable);

    // 统计指定分类下的新闻数量，用于删除分类前的关联检查，自动生成：SELECT COUNT(*) WHERE category_id = ?
    long countByCategoryId(Long categoryId);
}

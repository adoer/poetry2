// 声明当前类所属的包路径
package com.news.repository;

// 导入分类实体类
import com.news.entity.Category;
// 导入 Spring Data JPA 的 JpaRepository 接口
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 分类数据访问层
 * 继承 JpaRepository 提供对 categories 表的基础 CRUD 操作
 */
// 继承 JpaRepository<Category, Long>：实体类型为 Category，主键类型为 Long
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * 判断指定名称的分类是否已存在
     * Spring Data JPA 根据方法名自动生成：SELECT COUNT(*) WHERE name = ?
     * @param name 分类名称
     * @return 存在返回 true，否则返回 false
     */
    // 根据分类名称查询是否存在，用于创建和更新时的名称唯一性校验
    boolean existsByName(String name);
}

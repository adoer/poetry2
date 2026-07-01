// 声明当前类所属的包路径
package com.news.service;

// 导入分类实体类
import com.news.entity.Category;
// 导入 List 集合类
import java.util.List;

/**
 * 分类服务接口
 * 定义新闻分类的增删改查业务方法
 */
// 分类服务接口定义
public interface CategoryService {

    // 查询所有分类，返回列表
    List<Category> findAll();

    // 根据主键查询单个分类
    Category findById(Long id);

    // 创建分类，需校验名称唯一性
    Category create(String name);

    // 更新分类名称
    Category update(Long id, String name);

    // 删除分类，需先检查关联新闻
    void delete(Long id);
}

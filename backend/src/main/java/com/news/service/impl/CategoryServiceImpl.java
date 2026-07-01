// 声明实现类所属的包路径
package com.news.service.impl;

// 导入分类实体类
import com.news.entity.Category;
// 导入业务异常类
import com.news.exception.BusinessException;
// 导入资源不存在异常类
import com.news.exception.ResourceNotFoundException;
// 导入分类数据访问接口
import com.news.repository.CategoryRepository;
// 导入新闻数据访问接口（用于删除分类前的关联检查）
import com.news.repository.NewsRepository;
// 导入分类服务接口
import com.news.service.CategoryService;
// 导入 Spring @Service 注解
import org.springframework.stereotype.Service;

// 导入 List 集合类
import java.util.List;

/**
 * 分类服务实现类
 * 提供分类的增删改查功能，包含名称唯一性校验和删除前的关联检查
 */
// Spring 注解：标记此类为服务层组件，自动注册到 Spring 容器中
@Service
// 实现 CategoryService 接口
public class CategoryServiceImpl implements CategoryService {

    // 分类 Repository 依赖
    private final CategoryRepository categoryRepository;
    // 新闻 Repository 依赖（用于关联检查）
    private final NewsRepository newsRepository;

    // 构造函数，通过依赖注入获取两个 Repository
    public CategoryServiceImpl(CategoryRepository categoryRepository, NewsRepository newsRepository) {
        // 赋值分类 Repository
        this.categoryRepository = categoryRepository;
        // 赋值新闻 Repository
        this.newsRepository = newsRepository;
    }

    // 重写接口方法
    @Override
    // 查询所有分类
    public List<Category> findAll() {
        // 调用 JPA 的 findAll 方法，返回全部分类记录
        return categoryRepository.findAll();
    }

    /**
     * 根据 ID 获取分类
     * @param id 分类 ID
     * @return 分类实体
     * @throws ResourceNotFoundException 分类不存在时抛出
     */
    // 重写接口方法
    @Override
    // 根据 ID 查询分类
    public Category findById(Long id) {
        // 调用 JPA 的 findById 方法
        return categoryRepository.findById(id)
                // 如果未找到，抛出资源不存在异常
                .orElseThrow(() -> new ResourceNotFoundException("分类不存在"));
    }

    /**
     * 创建新分类
     * @param name 分类名称
     * @return 创建后的分类实体（含自动生成的 ID）
     * @throws BusinessException 名称已存在时抛出
     */
    // 重写接口方法
    @Override
    // 创建新分类
    public Category create(String name) {
        // 先检查分类名称是否已被占用
        if (categoryRepository.existsByName(name)) {
            // 名称已存在，抛出业务异常
            throw new BusinessException("分类名称已存在");
        }
        // 创建新的分类实体实例
        Category category = new Category();
        // 设置分类名称
        category.setName(name);
        // 保存到数据库并返回（save 方法会返回包含自动生成 ID 的实体）
        return categoryRepository.save(category);
    }

    /**
     * 更新分类名称
     * 如果名称实际未变更则跳过重复检查；如果变更后与其他分类冲突则抛异常
     * @param id   分类 ID
     * @param name 新的分类名称
     * @return 更新后的分类实体
     * @throws BusinessException 新名称与其他分类冲突时抛出
     */
    // 重写接口方法
    @Override
    // 更新分类名称
    public Category update(Long id, String name) {
        // 先查询分类是否存在（不存在会直接抛出 ResourceNotFoundException）
        Category category = findById(id);
        // 如果名称发生了变更（新旧不同），且新名称已被其他分类占用
        if (!category.getName().equals(name) && categoryRepository.existsByName(name)) {
            // 抛出名称冲突异常
            throw new BusinessException("分类名称已存在");
        }
        // 设置新的分类名称
        category.setName(name);
        // 保存更新后的实体并返回
        return categoryRepository.save(category);
    }

    /**
     * 删除分类
     * 删除前检查该分类下是否还有新闻，若有则阻止删除
     * @param id 分类 ID
     * @throws BusinessException 分类下存在关联新闻时抛出
     */
    // 重写接口方法
    @Override
    // 删除分类
    public void delete(Long id) {
        // 先查询分类是否存在（不存在会直接抛出 ResourceNotFoundException）
        Category category = findById(id);
        // 统计该分类下的新闻数量
        if (newsRepository.countByCategoryId(id) > 0) {
            // 如果存在关联新闻，阻止删除并抛出业务异常
            throw new BusinessException("该分类下存在新闻，无法删除");
        }
        // 无关联新闻，执行删除操作
        categoryRepository.delete(category);
    }
}

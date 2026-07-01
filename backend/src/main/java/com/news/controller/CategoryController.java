// 声明当前类所属的包路径
package com.news.controller;

// 导入统一响应结果封装类
import com.news.dto.Result;
// 导入分类实体类
import com.news.entity.Category;
// 导入业务异常类
import com.news.exception.BusinessException;
// 导入分类服务接口
import com.news.service.CategoryService;
// 导入 Spring Web MVC 注解
import org.springframework.web.bind.annotation.*;

// 导入 List 集合类
import java.util.List;
// 导入 Map 集合类，用于接收简单的请求体数据
import java.util.Map;

/**
 * 分类控制器
 * 提供分类的查询（公开接口）和增删改（管理后台接口）
 */
// Spring 注解：标记此类为 RESTful 控制器
@RestController
// 类级别请求路径映射：以 /api 开头
@RequestMapping("/api")
// 分类控制器类定义
public class CategoryController {

    // 分类服务依赖
    private final CategoryService categoryService;

    // 构造函数，通过依赖注入获取 CategoryService 实例
    public CategoryController(CategoryService categoryService) {
        // 赋值分类服务
        this.categoryService = categoryService;
    }

    /**
     * 获取全部分类列表（公开接口，无需登录）
     * @return 分类列表
     */
    // 映射 HTTP GET 请求到 /api/categories 路径（公开接口，无需认证）
    @GetMapping("/categories")
    // 获取全部分类的方法
    public Result<List<Category>> getAll() {
        // 调用服务查询全部分类，封装为成功响应返回
        return Result.success(categoryService.findAll());
    }

    /**
     * 创建分类（管理后台）
     * @param body 请求体，包含 "name" 字段
     * @return 创建后的分类信息
     * @throws BusinessException 名称为空或已存在时抛出
     */
    // 映射 HTTP POST 请求到 /api/admin/categories 路径（需 ADMIN 权限）
    @PostMapping("/admin/categories")
    // 创建分类的方法
    // @RequestBody Map<String, String>：接收 JSON 对象，用 Map 接收简单数据（只有分类名称）
    public Result<Category> create(@RequestBody Map<String, String> body) {
        // 从请求体中提取 "name" 字段值
        String name = body.get("name");
        // 如果名称为空或纯空格
        if (name == null || name.trim().isEmpty()) {
            // 抛出业务异常，由全局异常处理器统一处理
            throw new BusinessException("分类名称不能为空");
        }
        // 调用服务创建分类，返回成功响应
        return Result.success(categoryService.create(name));
    }

    /**
     * 更新分类名称（管理后台）
     * @param id   分类 ID
     * @param body 请求体，包含 "name" 字段
     * @return 更新后的分类信息
     * @throws BusinessException 名称为空或与其他分类冲突时抛出
     */
    // 映射 HTTP PUT 请求到 /api/admin/categories/{id} 路径（需 ADMIN 权限）
    @PutMapping("/admin/categories/{id}")
    // 更新分类的方法
    // @PathVariable Long id：从 URL 路径中获取分类 ID
    public Result<Category> update(@PathVariable Long id, @RequestBody Map<String, String> body) {
        // 从请求体中提取 "name" 字段值
        String name = body.get("name");
        // 如果名称为空或纯空格
        if (name == null || name.trim().isEmpty()) {
            // 抛出业务异常
            throw new BusinessException("分类名称不能为空");
        }
        // 调用服务更新分类名称，返回成功响应
        return Result.success(categoryService.update(id, name));
    }

    /**
     * 删除分类（管理后台）
     * @param id 分类 ID
     * @return 操作成功提示
     * @throws BusinessException 分类下存在关联新闻时抛出
     */
    // 映射 HTTP DELETE 请求到 /api/admin/categories/{id} 路径（需 ADMIN 权限）
    @DeleteMapping("/admin/categories/{id}")
    // 删除分类的方法
    public Result<Void> delete(@PathVariable Long id) {
        // 调用服务删除分类（存在关联新闻时会抛出 BusinessException）
        categoryService.delete(id);
        // 返回无数据的成功响应
        return Result.success();
    }
}

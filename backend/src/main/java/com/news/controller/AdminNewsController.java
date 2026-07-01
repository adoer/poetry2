// 声明当前类所属的包路径
package com.news.controller;

// 导入新闻 DTO
import com.news.dto.NewsDTO;
// 导入统一响应结果封装类
import com.news.dto.Result;
// 导入新闻服务接口
import com.news.service.NewsService;
// 导入 Spring Data 分页接口
import org.springframework.data.domain.Page;
// 导入 Spring Web MVC 注解
import org.springframework.web.bind.annotation.*;

/**
 * 管理后台新闻控制器
 * 提供新闻的增删改查接口，所有接口均需要 ADMIN 角色权限
 */
// Spring 注解：标记此类为 RESTful 控制器
@RestController
// 类级别请求路径映射：所有接口都以 /api/admin/news 开头（SecurityConfig 中已配置需要 ADMIN 角色）
@RequestMapping("/api/admin/news")
// 管理后台新闻控制器类定义
public class AdminNewsController {

    // 新闻服务依赖
    private final NewsService newsService;

    // 构造函数，通过依赖注入获取 NewsService 实例
    public AdminNewsController(NewsService newsService) {
        // 赋值新闻服务
        this.newsService = newsService;
    }

    /**
     * 分页获取后台新闻列表（含草稿和已发布）
     * @param keyword 标题搜索关键词（可选）
     * @param page    页码，默认从 0 开始
     * @param size    每页条数，默认 10
     * @return 新闻分页数据
     */
    // 映射 HTTP GET 请求到 /api/admin/news 路径
    @GetMapping
    // 管理后台新闻列表查询方法
    public Result<Page<NewsDTO>> list(
            // 请求参数 keyword（标题关键词），可选
            @RequestParam(required = false) String keyword,
            // 请求参数 page（页码），默认值为 0
            @RequestParam(defaultValue = "0") int page,
            // 请求参数 size（每页条数），默认值为 10
            @RequestParam(defaultValue = "10") int size) {
        // 调用服务查询后台新闻列表（含草稿和已发布），封装为成功响应返回
        return Result.success(newsService.getAdminNews(keyword, page, size));
    }

    /**
     * 创建新闻
     * @param dto 新闻数据（标题、内容、分类等）
     * @return 创建后的新闻信息
     */
    // 映射 HTTP POST 请求到 /api/admin/news 路径
    @PostMapping
    // 创建新闻的方法
    // @RequestBody NewsDTO dto：将请求体 JSON 自动反序列化为 NewsDTO 对象
    public Result<NewsDTO> create(@RequestBody NewsDTO dto) {
        // 调用服务创建新闻，返回成功响应
        return Result.success(newsService.create(dto));
    }

    /**
     * 根据 ID 获取单条新闻详情（管理后台）
     * @param id 新闻 ID
     * @return 新闻详细信息
     */
    // 映射 HTTP GET 请求到 /api/admin/news/{id} 路径
    @GetMapping("/{id}")
    // 查询单条新闻详情的方法（管理后台）
    // @PathVariable Long id：从 URL 路径中获取新闻 ID
    public Result<NewsDTO> get(@PathVariable Long id) {
        // 调用服务查询任意状态的单条新闻，返回成功响应
        return Result.success(newsService.getAdminNewsById(id));
    }

    /**
     * 更新新闻
     * @param id  新闻 ID
     * @param dto 更新的新闻数据
     * @return 更新后的新闻信息
     */
    // 映射 HTTP PUT 请求到 /api/admin/news/{id} 路径
    @PutMapping("/{id}")
    // 更新新闻的方法
    // @PathVariable Long id：从 URL 路径中获取新闻 ID
    // @RequestBody NewsDTO dto：新的新闻数据
    public Result<NewsDTO> update(@PathVariable Long id, @RequestBody NewsDTO dto) {
        // 调用服务更新新闻，返回成功响应
        return Result.success(newsService.update(id, dto));
    }

    /**
     * 删除新闻
     * @param id 要删除的新闻 ID
     * @return 操作成功提示（无数据体）
     */
    // 映射 HTTP DELETE 请求到 /api/admin/news/{id} 路径
    @DeleteMapping("/{id}")
    // 删除新闻的方法
    public Result<Void> delete(@PathVariable Long id) {
        // 调用服务删除新闻
        newsService.delete(id);
        // 返回无数据的成功响应
        return Result.success();
    }
}

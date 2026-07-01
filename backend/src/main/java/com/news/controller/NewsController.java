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
 * 公开新闻控制器
 * 提供对外的新闻查询接口，仅返回已发布的新闻
 */
// Spring 注解：标记此类为 RESTful 控制器
@RestController
// 类级别请求路径映射：所有接口都以 /api/news 开头
@RequestMapping("/api/news")
// 公开新闻控制器类定义
public class NewsController {

    // 新闻服务依赖
    private final NewsService newsService;

    // 构造函数，通过依赖注入获取 NewsService 实例
    public NewsController(NewsService newsService) {
        // 赋值新闻服务
        this.newsService = newsService;
    }

    /**
     * 分页获取已发布的公开新闻列表
     * 支持按分类和标题关键词进行筛选
     * @param categoryId 分类 ID（可选）
     * @param keyword    标题关键词（可选）
     * @param page       页码，默认从 0 开始
     * @param size       每页条数，默认 10
     * @return 已发布新闻的分页数据
     */
    // 映射 HTTP GET 请求到 /api/news 路径
    @GetMapping
    // 公开新闻列表查询方法
    public Result<Page<NewsDTO>> list(
            // 请求参数 categoryId，可选（不传则不按分类筛选）
            @RequestParam(required = false) Long categoryId,
            // 请求参数 keyword（标题关键词），可选
            @RequestParam(required = false) String keyword,
            // 请求参数 page（页码），默认值为 0
            @RequestParam(defaultValue = "0") int page,
            // 请求参数 size（每页条数），默认值为 10
            @RequestParam(defaultValue = "10") int size) {
        // 调用服务查询公开新闻，封装为成功响应返回
        return Result.success(newsService.getPublicNews(categoryId, keyword, page, size));
    }

    /**
     * 获取单条已发布的新闻详情
     * @param id 新闻 ID
     * @return 新闻详细信息
     */
    // 映射 HTTP GET 请求到 /api/news/{id} 路径
    @GetMapping("/{id}")
    // 查询单条新闻详情的方法
    // @PathVariable Long id：从 URL 路径中获取新闻 ID
    public Result<NewsDTO> detail(@PathVariable Long id) {
        // 调用服务查询单条已发布新闻，封装为成功响应返回
        return Result.success(newsService.getPublicNewsById(id));
    }
}

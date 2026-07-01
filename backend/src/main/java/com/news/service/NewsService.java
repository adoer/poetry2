// 声明当前类所属的包路径
package com.news.service;

// 导入新闻 DTO
import com.news.dto.NewsDTO;
// 导入分页接口
import org.springframework.data.domain.Page;

/**
 * 新闻服务接口
 * 定义新闻的前台展示和后台管理业务方法
 */
// 新闻服务接口定义
public interface NewsService {

    /**
     * 分页查询已发布的公开新闻（支持按分类和关键词筛选）
     * @param categoryId 分类 ID（可选）
     * @param keyword    标题关键词（可选）
     * @param page       页码，从 0 开始
     * @param size       每页条数
     * @return 已发布新闻的分页数据
     */
    // 前台公开接口：仅返回已发布的新闻
    Page<NewsDTO> getPublicNews(Long categoryId, String keyword, int page, int size);

    // 前台公开接口：查询单条已发布新闻详情
    NewsDTO getPublicNewsById(Long id);

    /**
     * 分页查询所有状态的新闻（管理后台用）
     * @param keyword 标题关键词（可选）
     * @param page    页码，从 0 开始
     * @param size    每页条数
     * @return 全量新闻的分页数据
     */
    // 管理后台接口：可查看所有状态（含草稿）的新闻
    Page<NewsDTO> getAdminNews(String keyword, int page, int size);

    // 管理后台接口：可查看任意状态的单条新闻
    NewsDTO getAdminNewsById(Long id);

    // 新增新闻
    NewsDTO create(NewsDTO dto);

    // 修改新闻内容
    NewsDTO update(Long id, NewsDTO dto);

    // 删除新闻
    void delete(Long id);
}

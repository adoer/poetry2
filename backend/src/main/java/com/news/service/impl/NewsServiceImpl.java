// 声明实现类所属的包路径
package com.news.service.impl;

// 导入新闻 DTO
import com.news.dto.NewsDTO;
// 导入分类实体类
import com.news.entity.Category;
// 导入新闻实体类
import com.news.entity.News;
// 导入新闻状态枚举
import com.news.entity.NewsStatus;
// 导入资源不存在异常类
import com.news.exception.ResourceNotFoundException;
// 导入新闻数据访问接口
import com.news.repository.NewsRepository;
// 导入分类服务接口（用于获取分类名称）
import com.news.service.CategoryService;
// 导入新闻服务接口
import com.news.service.NewsService;
// 导入 SLF4J 日志接口
import org.slf4j.Logger;
// 导入 SLF4J 日志工厂类
import org.slf4j.LoggerFactory;
// 导入分页接口
import org.springframework.data.domain.Page;
// 导入分页请求构建类
import org.springframework.data.domain.PageRequest;
// 导入分页参数接口
import org.springframework.data.domain.Pageable;
// 导入排序对象
import org.springframework.data.domain.Sort;
// 导入 Spring @Service 注解
import org.springframework.stereotype.Service;

// 导入日期时间格式化类
import java.time.format.DateTimeFormatter;

/**
 * 新闻服务实现类
 * 提供新闻的完整业务逻辑，包括前台公开查询、后台管理查询、增删改查，
 * 以及 News 实体到 NewsDTO 的转换
 */
// Spring 注解：标记此类为服务层组件，自动注册到 Spring 容器中
@Service
// 实现 NewsService 接口
public class NewsServiceImpl implements NewsService {

    // 创建 SLF4J 日志记录器，用于记录转换 DTO 时的警告信息
    private static final Logger log = LoggerFactory.getLogger(NewsServiceImpl.class);

    // 新闻 Repository 依赖
    private final NewsRepository newsRepository;
    // 分类服务依赖（用于组装 DTO 时填充分类名称）
    private final CategoryService categoryService;

    // 构造函数，通过依赖注入获取两个依赖
    public NewsServiceImpl(NewsRepository newsRepository, CategoryService categoryService) {
        // 赋值新闻 Repository
        this.newsRepository = newsRepository;
        // 赋值分类服务
        this.categoryService = categoryService;
    }

    /**
     * 分页查询已发布的公开新闻
     * 支持按分类 ID、标题关键词进行组合筛选，所有结果按创建时间倒序排列
     * @param categoryId 分类 ID（为空表示不按分类筛选）
     * @param keyword    标题关键词（为空表示不按关键词筛选）
     * @param page       页码（从 0 开始）
     * @param size       每页条数
     * @return 已发布新闻的分页 DTO 数据
     */
    // 重写接口方法
    @Override
    // 查询公开新闻的分页列表
    public Page<NewsDTO> getPublicNews(Long categoryId, String keyword, int page, int size) {
        // 构建分页请求对象：指定页码、每页条数、按创建时间倒序排序
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        // 声明新闻分页变量
        Page<News> newsPage;

        // 分类 ID 和关键词都有值：按分类 + 关键词 + 已发布状态查询
        if (categoryId != null && keyword != null && !keyword.isEmpty()) {
            newsPage = newsRepository.findByCategoryIdAndTitleContainingAndStatus(
                    categoryId, keyword, NewsStatus.PUBLISHED, pageable);
        // 只有分类 ID：按分类 + 已发布状态查询
        } else if (categoryId != null) {
            newsPage = newsRepository.findByCategoryIdAndStatus(categoryId, NewsStatus.PUBLISHED, pageable);
        // 只有关键词：按标题模糊 + 已发布状态查询
        } else if (keyword != null && !keyword.isEmpty()) {
            newsPage = newsRepository.findByTitleContainingAndStatus(keyword, NewsStatus.PUBLISHED, pageable);
        } else {
            // 无筛选条件：仅按已发布状态查询全部
            newsPage = newsRepository.findByStatus(NewsStatus.PUBLISHED, pageable);
        }

        // 将 News 实体的分页结果映射为 NewsDTO 的分页结果
        return newsPage.map(this::toDTO);
    }

    /**
     * 根据 ID 获取单条已发布的公开新闻
     * 如果新闻不存在或状态不是 PUBLISHED，均返回"新闻不存在"提示（信息模糊化处理）
     * @param id 新闻 ID
     * @return 新闻 DTO
     * @throws ResourceNotFoundException 新闻不存在或未发布时抛出
     */
    // 重写接口方法
    @Override
    // 查询单条公开新闻详情
    public NewsDTO getPublicNewsById(Long id) {
        // 根据 ID 查询新闻
        News news = newsRepository.findById(id)
                // 未找到时抛出异常
                .orElseThrow(() -> new ResourceNotFoundException("新闻不存在"));
        // 如果新闻存在但状态不是已发布
        if (news.getStatus() != NewsStatus.PUBLISHED) {
            // 同样抛出"新闻不存在"而非"新闻未发布"，避免泄露信息
            throw new ResourceNotFoundException("新闻不存在");
        }
        // 将实体转换为 DTO 并返回
        return toDTO(news);
    }

    /**
     * 分页查询全部状态的新闻（管理后台使用）
     * 支持按标题关键词筛选，结果按创建时间倒序排列
     * @param keyword 标题关键词（可选）
     * @param page    页码（从 0 开始）
     * @param size    每页条数
     * @return 全量新闻的分页 DTO 数据
     */
    // 重写接口方法
    @Override
    // 查询管理后台新闻列表（包含草稿）
    public Page<NewsDTO> getAdminNews(String keyword, int page, int size) {
        // 构建分页请求：按创建时间倒序排序
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        // 声明新闻分页变量
        Page<News> newsPage;

        // 有关键词筛选条件
        if (keyword != null && !keyword.isEmpty()) {
            // 按标题模糊查询（不限制状态）
            newsPage = newsRepository.findByTitleContaining(keyword, pageable);
        } else {
            // 查询全部新闻（不分状态）
            newsPage = newsRepository.findAll(pageable);
        }

        // 映射为 DTO 分页结果
        return newsPage.map(this::toDTO);
    }

    /**
     * 根据 ID 获取任意状态的新闻（管理后台使用）
     * @param id 新闻 ID
     * @return 新闻 DTO
     * @throws ResourceNotFoundException 新闻不存在时抛出
     */
    // 重写接口方法
    @Override
    // 查询管理后台的新闻详情
    public NewsDTO getAdminNewsById(Long id) {
        // 根据 ID 查询
        News news = newsRepository.findById(id)
                // 未找到时抛出异常
                .orElseThrow(() -> new ResourceNotFoundException("新闻不存在"));
        // 转换为 DTO 返回（不检查状态，草稿也可以查看）
        return toDTO(news);
    }

    /**
     * 创建新闻
     * 将 DTO 数据转换为实体后保存，若未指定状态则默认为草稿
     * @param dto 新闻数据（标题、内容、分类等）
     * @return 创建完成后的新闻 DTO（含自动生成的 ID 和时间）
     */
    // 重写接口方法
    @Override
    // 创建新闻
    public NewsDTO create(NewsDTO dto) {
        // 创建新的新闻实体
        News news = new News();
        // 设置新闻标题
        news.setTitle(dto.getTitle());
        // 设置新闻正文
        news.setContent(dto.getContent());
        // 设置新闻摘要
        news.setSummary(dto.getSummary());
        // 设置封面图地址
        news.setCoverImage(dto.getCoverImage());
        // 设置所属分类 ID
        news.setCategoryId(dto.getCategoryId());
        // 设置新闻状态：如果 DTO 中指定了状态则使用该状态，否则默认为 DRAFT（草稿）
        news.setStatus(dto.getStatus() != null ? dto.getStatus() : NewsStatus.DRAFT);
        // 保存到数据库并将返回的实体转换为 DTO
        return toDTO(newsRepository.save(news));
    }

    /**
     * 更新新闻
     * 先根据 ID 查找现有记录，再更新各字段后保存
     * @param id  新闻 ID
     * @param dto 新的新闻数据
     * @return 更新后的新闻 DTO
     * @throws ResourceNotFoundException 新闻不存在时抛出
     */
    // 重写接口方法
    @Override
    // 更新新闻
    public NewsDTO update(Long id, NewsDTO dto) {
        // 先查询现有记录
        News news = newsRepository.findById(id)
                // 不存在则抛出异常
                .orElseThrow(() -> new ResourceNotFoundException("新闻不存在"));
        // 更新标题
        news.setTitle(dto.getTitle());
        // 更新正文
        news.setContent(dto.getContent());
        // 更新摘要
        news.setSummary(dto.getSummary());
        // 更新封面图
        news.setCoverImage(dto.getCoverImage());
        // 更新分类 ID
        news.setCategoryId(dto.getCategoryId());
        // 更新状态，未指定时默认为草稿
        news.setStatus(dto.getStatus() != null ? dto.getStatus() : NewsStatus.DRAFT);
        // 保存更新后的实体并转换为 DTO 返回
        return toDTO(newsRepository.save(news));
    }

    /**
     * 删除新闻
     * @param id 要删除的新闻 ID
     * @throws ResourceNotFoundException 新闻不存在时抛出
     */
    // 重写接口方法
    @Override
    // 删除新闻
    public void delete(Long id) {
        // 先检查新闻是否存在
        if (!newsRepository.existsById(id)) {
            // 不存在则抛出异常
            throw new ResourceNotFoundException("新闻不存在");
        }
        // 存在则执行删除操作
        newsRepository.deleteById(id);
    }

    /**
     * 将 News 实体转换为 NewsDTO
     * 包含实体字段的逐字段拷贝，并补充分类名称（通过分类服务查询）和格式化时间字符串
     * @param news 新闻实体
     * @return 转换后的新闻 DTO
     */
    // 私有方法：将 News 实体转换为 NewsDTO
    private NewsDTO toDTO(News news) {
        // 创建 DTO 实例
        NewsDTO dto = new NewsDTO();
        // 拷贝 ID
        dto.setId(news.getId());
        // 拷贝标题
        dto.setTitle(news.getTitle());
        // 拷贝正文
        dto.setContent(news.getContent());
        // 拷贝摘要
        dto.setSummary(news.getSummary());
        // 拷贝封面图 URL
        dto.setCoverImage(news.getCoverImage());
        // 拷贝分类 ID
        dto.setCategoryId(news.getCategoryId());
        // 拷贝新闻状态
        dto.setStatus(news.getStatus());

        // 尝试获取分类名称
        try {
            // 通过分类服务查询分类实体
            Category category = categoryService.findById(news.getCategoryId());
            // 设置分类名称到 DTO
            dto.setCategoryName(category.getName());
        } catch (Exception e) {
            // 如果分类查询失败（如分类已被删除）
            log.warn("Failed to fetch category name for categoryId={}", news.getCategoryId(), e);
            // 记录警告日志，不影响新闻本身的返回
        }

        // 创建日期时间格式化器，格式为：年-月-日 时:分:秒
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // 将 LocalDateTime 格式化为字符串并设置到 DTO
        if (news.getCreatedAt() != null) dto.setCreatedAt(news.getCreatedAt().format(fmt));
        // 将 LocalDateTime 格式化为字符串并设置到 DTO
        if (news.getUpdatedAt() != null) dto.setUpdatedAt(news.getUpdatedAt().format(fmt));

        // 返回填充完成的 DTO
        return dto;
    }
}

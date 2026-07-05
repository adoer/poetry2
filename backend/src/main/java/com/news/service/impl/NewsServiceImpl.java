package com.news.service.impl;

import com.news.dto.NewsDTO;
import com.news.entity.Category;
import com.news.entity.News;
import com.news.entity.NewsStatus;
import com.news.exception.ResourceNotFoundException;
import com.news.repository.NewsRepository;
import com.news.service.CategoryService;
import com.news.service.NewsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class NewsServiceImpl implements NewsService {

    private static final Logger log = LoggerFactory.getLogger(NewsServiceImpl.class);

    private final NewsRepository newsRepository;
    private final CategoryService categoryService;

    public NewsServiceImpl(NewsRepository newsRepository, CategoryService categoryService) {
        this.newsRepository = newsRepository;
        this.categoryService = categoryService;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NewsDTO> getPublicNews(Long categoryId, String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<News> newsPage;

        if (categoryId != null && keyword != null && !keyword.isEmpty()) {
            newsPage = newsRepository.findByCategoryIdAndTitleContainingAndStatus(
                    categoryId, keyword, NewsStatus.PUBLISHED, pageable);
        } else if (categoryId != null) {
            newsPage = newsRepository.findByCategoryIdAndStatus(categoryId, NewsStatus.PUBLISHED, pageable);
        } else if (keyword != null && !keyword.isEmpty()) {
            newsPage = newsRepository.findByTitleContainingAndStatus(keyword, NewsStatus.PUBLISHED, pageable);
        } else {
            newsPage = newsRepository.findByStatus(NewsStatus.PUBLISHED, pageable);
        }

        Map<Long, String> categoryNames = buildCategoryNameMap(newsPage.getContent());
        return newsPage.map(news -> toDTO(news, categoryNames));
    }

    @Override
    @Transactional(readOnly = true)
    public NewsDTO getPublicNewsById(Long id) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("新闻不存在"));
        if (news.getStatus() != NewsStatus.PUBLISHED) {
            throw new ResourceNotFoundException("新闻不存在");
        }
        return toDTO(news);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NewsDTO> getAdminNews(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<News> newsPage;

        if (keyword != null && !keyword.isEmpty()) {
            newsPage = newsRepository.findByTitleContaining(keyword, pageable);
        } else {
            newsPage = newsRepository.findAll(pageable);
        }

        Map<Long, String> categoryNames = buildCategoryNameMap(newsPage.getContent());
        return newsPage.map(news -> toDTO(news, categoryNames));
    }

    @Override
    @Transactional(readOnly = true)
    public NewsDTO getAdminNewsById(Long id) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("新闻不存在"));
        return toDTO(news);
    }

    @Override
    public NewsDTO create(NewsDTO dto) {
        News news = new News();
        news.setTitle(dto.getTitle());
        news.setContent(dto.getContent());
        news.setSummary(dto.getSummary());
        news.setCoverImage(dto.getCoverImage());
        news.setCategoryId(dto.getCategoryId());
        news.setStatus(dto.getStatus() != null ? dto.getStatus() : NewsStatus.DRAFT);
        return toDTO(newsRepository.save(news));
    }

    @Override
    public NewsDTO update(Long id, NewsDTO dto) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("新闻不存在"));
        news.setTitle(dto.getTitle());
        news.setContent(dto.getContent());
        news.setSummary(dto.getSummary());
        news.setCoverImage(dto.getCoverImage());
        news.setCategoryId(dto.getCategoryId());
        news.setStatus(dto.getStatus() != null ? dto.getStatus() : NewsStatus.DRAFT);
        return toDTO(newsRepository.save(news));
    }

    @Override
    public void delete(Long id) {
        if (!newsRepository.existsById(id)) {
            throw new ResourceNotFoundException("新闻不存在");
        }
        newsRepository.deleteById(id);
    }

    private Map<Long, String> buildCategoryNameMap(List<News> newsList) {
        Set<Long> categoryIds = newsList.stream()
                .map(News::getCategoryId)
                .collect(Collectors.toSet());
        if (categoryIds.isEmpty()) return Map.of();
        return categoryService.findAllById(categoryIds).stream()
                .collect(Collectors.toMap(Category::getId, Category::getName));
    }

    private NewsDTO toDTO(News news, Map<Long, String> categoryNames) {
        NewsDTO dto = new NewsDTO();
        copyFields(news, dto);
        dto.setCategoryName(categoryNames.get(news.getCategoryId()));
        return dto;
    }

    private NewsDTO toDTO(News news) {
        NewsDTO dto = new NewsDTO();
        copyFields(news, dto);
        try {
            Category category = categoryService.findById(news.getCategoryId());
            dto.setCategoryName(category.getName());
        } catch (Exception e) {
            log.warn("Failed to fetch category name for categoryId={}", news.getCategoryId(), e);
        }
        return dto;
    }

    private void copyFields(News news, NewsDTO dto) {
        dto.setId(news.getId());
        dto.setTitle(news.getTitle());
        dto.setContent(news.getContent());
        dto.setSummary(news.getSummary());
        dto.setCoverImage(news.getCoverImage());
        dto.setCategoryId(news.getCategoryId());
        dto.setStatus(news.getStatus());
        dto.setCreatedAt(news.getCreatedAt());
        dto.setUpdatedAt(news.getUpdatedAt());
    }
}

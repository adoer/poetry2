package com.news.service.impl;

import com.news.entity.Category;
import com.news.exception.BusinessException;
import com.news.exception.ResourceNotFoundException;
import com.news.repository.CategoryRepository;
import com.news.repository.NewsRepository;
import com.news.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final NewsRepository newsRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository, NewsRepository newsRepository) {
        this.categoryRepository = categoryRepository;
        this.newsRepository = newsRepository;
    }

    @Override
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    @Override
    public List<Category> findAllById(Collection<Long> ids) {
        return categoryRepository.findAllById(ids);
    }

    @Override
    public Category findById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("分类不存在"));
    }

    @Override
    public Category create(String name) {
        if (categoryRepository.existsByName(name)) {
            throw new BusinessException("分类名称已存在");
        }
        Category category = new Category();
        category.setName(name);
        return categoryRepository.save(category);
    }

    @Override
    public Category update(Long id, String name) {
        Category category = findById(id);
        if (!category.getName().equals(name) && categoryRepository.existsByName(name)) {
            throw new BusinessException("分类名称已存在");
        }
        category.setName(name);
        return categoryRepository.save(category);
    }

    @Override
    public void delete(Long id) {
        Category category = findById(id);
        if (newsRepository.countByCategoryId(id) > 0) {
            throw new BusinessException("该分类下存在新闻，无法删除");
        }
        categoryRepository.delete(category);
    }
}

package com.news.service;

import com.news.entity.Category;

import java.util.Collection;
import java.util.List;

public interface CategoryService {

    List<Category> findAll();

    List<Category> findAllById(Collection<Long> ids);

    Category findById(Long id);

    Category create(String name);

    Category update(Long id, String name);

    void delete(Long id);
}

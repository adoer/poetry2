package com.poetry.service;

import java.util.List;
import java.util.Map;

public interface CategoryService {
    List<String> getCategories();
    List<Map<String, Object>> getCategoryDetail(String category);
}

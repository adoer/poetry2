package com.news.service;

import com.news.entity.Authors;

import java.util.List;
import java.util.Map;

public interface AuthorsService {
    List<Map<String, Object>> getAuthorsList(int pageNum);
    Map<String, Object> getAuthorById(Integer id);
    List<Map<String, Object>> getWriters();
    Map<String, Object> getAuthorsListPage(int page, int size, String keyword);
}

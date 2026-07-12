package com.news.service;

import com.news.entity.Poesy;

import java.util.List;
import java.util.Map;

public interface PoesyService {
    List<Map<String, Object>> getPoesyList(int pageNum, boolean recommended);
    Map<String, Object> getPoesyById(Integer id);
    int getTotalPages();
    Map<String, Object> getPoesyListPage(int page, int size, boolean recommended);
    Map<String, Object> getPoesyListPage(int page, int size, boolean recommended, String keyword, String writer);
}

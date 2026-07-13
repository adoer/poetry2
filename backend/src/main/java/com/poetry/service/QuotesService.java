package com.poetry.service;

import com.poetry.entity.Quotes;

import java.util.List;
import java.util.Map;

public interface QuotesService {
    List<Quotes> getQuotesList(int pageNum);
    Map<String, Object> getQuotesListPage(int page, int size, String keyword);
}

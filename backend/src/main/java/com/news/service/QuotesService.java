package com.news.service;

import com.news.entity.Quotes;

import java.util.List;

public interface QuotesService {
    List<Quotes> getQuotesList(int pageNum);
}

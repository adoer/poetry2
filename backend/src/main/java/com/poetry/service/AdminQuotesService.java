package com.poetry.service;

import com.poetry.entity.Quotes;
import java.util.Map;

public interface AdminQuotesService {
    Map<String, Object> getQuotesList(int page, int size, String keyword);
    Quotes getQuotesById(Integer id);
    Quotes createQuotes(Quotes quotes);
    Quotes updateQuotes(Integer id, Quotes quotes);
    void deleteQuotes(Integer id);
}

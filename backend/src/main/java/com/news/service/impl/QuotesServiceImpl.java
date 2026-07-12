package com.news.service.impl;

import com.news.entity.Quotes;
import com.news.repository.QuotesRepository;
import com.news.service.QuotesService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class QuotesServiceImpl implements QuotesService {

    private static final int PAGE_SIZE = 30;
    private static final int MAX_PAGE = 5;

    private final QuotesRepository quotesRepository;

    public QuotesServiceImpl(QuotesRepository quotesRepository) {
        this.quotesRepository = quotesRepository;
    }

    @Override
    public List<Quotes> getQuotesList(int pageNum) {
        if (pageNum < 1) pageNum = 1;
        if (pageNum > MAX_PAGE) pageNum = MAX_PAGE;

        return quotesRepository.findAll(PageRequest.of(pageNum - 1, PAGE_SIZE))
                .getContent();
    }

    @Override
    public Map<String, Object> getQuotesListPage(int page, int size, String keyword) {
        if (page < 1) page = 1;
        Page<Quotes> pageResult;
        if (keyword != null && !keyword.isBlank()) {
            pageResult = quotesRepository.findByContentContaining(keyword, PageRequest.of(page - 1, size));
        } else {
            pageResult = quotesRepository.findAll(PageRequest.of(page - 1, size));
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("content", pageResult.getContent());
        result.put("totalPages", pageResult.getTotalPages());
        result.put("totalElements", pageResult.getTotalElements());
        return result;
    }
}

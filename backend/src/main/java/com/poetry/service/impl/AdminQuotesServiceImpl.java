package com.poetry.service.impl;

import com.poetry.entity.Quotes;
import com.poetry.repository.QuotesRepository;
import com.poetry.service.AdminQuotesService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdminQuotesServiceImpl implements AdminQuotesService {

    private final QuotesRepository quotesRepository;

    public AdminQuotesServiceImpl(QuotesRepository quotesRepository) {
        this.quotesRepository = quotesRepository;
    }

    @Override
    public Map<String, Object> getQuotesList(int page, int size, String keyword) {
        if (page < 1) page = 1;
        var pageable = PageRequest.of(page - 1, size);
        var pageResult = (keyword != null && !keyword.isBlank())
            ? quotesRepository.findByContentContaining(keyword, pageable)
            : quotesRepository.findAll(pageable);

        var content = pageResult.getContent().stream().map(q -> {
            var map = new LinkedHashMap<String, Object>();
            map.put("id", q.getId());
            map.put("content", q.getContent());
            map.put("poetryId", q.getPoetryId());
            map.put("poetryName", q.getPoetryName());
            map.put("writer", q.getWriter());
            map.put("writerId", q.getWriterId());
            return map;
        }).collect(Collectors.toList());

        var result = new LinkedHashMap<String, Object>();
        result.put("content", content);
        result.put("totalPages", pageResult.getTotalPages());
        result.put("totalElements", pageResult.getTotalElements());
        return result;
    }

    @Override
    public Quotes getQuotesById(Integer id) {
        return quotesRepository.findById(id).orElse(null);
    }

    @Override
    public Quotes createQuotes(Quotes quotes) {
        Integer maxId = quotesRepository.findMaxId();
        quotes.setId(maxId == null ? 1 : maxId + 1);
        return quotesRepository.save(quotes);
    }

    @Override
    public Quotes updateQuotes(Integer id, Quotes quotes) {
        var existing = quotesRepository.findById(id).orElse(null);
        if (existing == null) return null;
        quotes.setId(id);
        return quotesRepository.save(quotes);
    }

    @Override
    public void deleteQuotes(Integer id) {
        quotesRepository.deleteById(id);
    }
}

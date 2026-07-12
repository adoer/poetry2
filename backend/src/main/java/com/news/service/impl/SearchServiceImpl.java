package com.news.service.impl;

import com.news.entity.Authors;
import com.news.entity.Poesy;
import com.news.entity.Quotes;
import com.news.repository.AuthorsRepository;
import com.news.repository.PoesyRepository;
import com.news.repository.QuotesRepository;
import com.news.service.SearchService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SearchServiceImpl implements SearchService {

    private final PoesyRepository poesyRepository;
    private final AuthorsRepository authorsRepository;
    private final QuotesRepository quotesRepository;

    public SearchServiceImpl(PoesyRepository poesyRepository,
                              AuthorsRepository authorsRepository,
                              QuotesRepository quotesRepository) {
        this.poesyRepository = poesyRepository;
        this.authorsRepository = authorsRepository;
        this.quotesRepository = quotesRepository;
    }

    @Override
    public Map<String, List<Map<String, Object>>> searchAll(String keyword) {
        Map<String, List<Map<String, Object>>> result = new LinkedHashMap<>();

        List<Poesy> contentMatches = poesyRepository.findByContentContaining(keyword, PageRequest.of(0, 6));
        result.put("Poesy", contentMatches.stream().map(p -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", p.getId());
            m.put("content", p.getContent());
            m.put("title", p.getTitle());
            m.put("writer", p.getWriter());
            return m;
        }).collect(Collectors.toList()));

        List<Authors> authorMatches = authorsRepository.findByNameContaining(keyword);
        result.put("Authors", authorMatches.stream().map(a -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", a.getId());
            m.put("name", a.getName());
            m.put("dynasty", a.getDynasty());
            return m;
        }).collect(Collectors.toList()));

        List<Quotes> quoteMatches = quotesRepository.findByContentContaining(keyword);
        result.put("Quotes", quoteMatches.stream().limit(6).map(q -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", q.getId());
            m.put("content", q.getContent());
            m.put("writer", q.getWriter());
            m.put("poetryName", q.getPoetryName());
            return m;
        }).collect(Collectors.toList()));

        List<Poesy> titleMatches = poesyRepository.findByTitleContaining(keyword, PageRequest.of(0, 6));
        result.put("Titles", titleMatches.stream().map(p -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", p.getId());
            m.put("title", p.getTitle());
            m.put("writer", p.getWriter());
            m.put("dynasty", p.getDynasty());
            return m;
        }).collect(Collectors.toList()));

        return result;
    }
}

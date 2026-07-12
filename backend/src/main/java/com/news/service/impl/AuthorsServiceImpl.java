package com.news.service.impl;

import com.news.entity.Authors;
import com.news.repository.AuthorsRepository;
import com.news.service.AuthorsService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AuthorsServiceImpl implements AuthorsService {

    private static final int PAGE_SIZE = 10;
    private static final int MAX_PAGE = 5;

    private final AuthorsRepository authorsRepository;

    public AuthorsServiceImpl(AuthorsRepository authorsRepository) {
        this.authorsRepository = authorsRepository;
    }

    @Override
    public List<Map<String, Object>> getAuthorsList(int pageNum) {
        if (pageNum < 1) pageNum = 1;
        if (pageNum > MAX_PAGE) pageNum = MAX_PAGE;

        return authorsRepository.findAll(PageRequest.of(pageNum - 1, PAGE_SIZE)).getContent().stream().map(a -> {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", a.getId());
            map.put("name", a.getName());
            map.put("simpleIntro", a.getSimpleIntro());
            map.put("headImageUrl", a.getHeadImageUrl());
            map.put("dynasty", a.getDynasty());
            return map;
        }).collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getAuthorById(Integer id) {
        Authors a = authorsRepository.findById(id).orElse(null);
        if (a == null) return null;
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", a.getId());
        map.put("name", a.getName());
        map.put("simpleIntro", a.getSimpleIntro());
        map.put("detailIntro", a.getDetailIntro());
        map.put("headImageUrl", a.getHeadImageUrl());
        map.put("dynasty", a.getDynasty());
        return map;
    }

    @Override
    public List<Map<String, Object>> getWriters() {
        return authorsRepository.findAll().stream()
                .limit(60)
                .map(a -> {
                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put("name", a.getName());
                    map.put("id", a.getId());
                    return map;
                })
                .collect(Collectors.toList());
    }
}

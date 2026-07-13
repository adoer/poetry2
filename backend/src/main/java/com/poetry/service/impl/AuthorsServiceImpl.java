package com.poetry.service.impl;

import com.poetry.entity.Authors;
import com.poetry.repository.AuthorsRepository;
import com.poetry.service.AuthorsService;
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
    public Map<String, Object> getAuthorsListPage(int page, int size, String keyword) {
        if (page < 1) page = 1;
        org.springframework.data.domain.Page<Authors> pageResult;
        if (keyword != null && !keyword.isBlank()) {
            pageResult = authorsRepository.findByNameContaining(keyword, PageRequest.of(page - 1, size));
        } else {
            pageResult = authorsRepository.findAll(PageRequest.of(page - 1, size));
        }

        List<Map<String, Object>> content = pageResult.getContent().stream().map(a -> {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", a.getId());
            map.put("name", a.getName());
            map.put("simpleIntro", a.getSimpleIntro());
            map.put("headImageUrl", a.getHeadImageUrl());
            map.put("dynasty", a.getDynasty());
            return map;
        }).collect(Collectors.toList());

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("content", content);
        result.put("totalPages", pageResult.getTotalPages());
        result.put("totalElements", pageResult.getTotalElements());
        return result;
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

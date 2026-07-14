package com.poetry.service.impl;

import com.poetry.entity.Authors;
import com.poetry.repository.AuthorsRepository;
import com.poetry.service.AdminAuthorsService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdminAuthorsServiceImpl implements AdminAuthorsService {

    private final AuthorsRepository authorsRepository;

    public AdminAuthorsServiceImpl(AuthorsRepository authorsRepository) {
        this.authorsRepository = authorsRepository;
    }

    @Override
    public Map<String, Object> getAuthorsList(int page, int size, String keyword) {
        if (page < 1) page = 1;
        var pageable = PageRequest.of(page - 1, size);
        var pageResult = (keyword != null && !keyword.isBlank())
            ? authorsRepository.findByNameContaining(keyword, pageable)
            : authorsRepository.findAll(pageable);

        var content = pageResult.getContent().stream().map(a -> {
            var map = new LinkedHashMap<String, Object>();
            map.put("id", a.getId());
            map.put("name", a.getName());
            map.put("simpleIntro", a.getSimpleIntro());
            map.put("detailIntro", a.getDetailIntro());
            map.put("headImageUrl", a.getHeadImageUrl());
            map.put("dynasty", a.getDynasty());
            return map;
        }).collect(Collectors.toList());

        var result = new LinkedHashMap<String, Object>();
        result.put("content", content);
        result.put("totalPages", pageResult.getTotalPages());
        result.put("totalElements", pageResult.getTotalElements());
        return result;
    }

    @Override
    public Authors getAuthorById(Integer id) {
        return authorsRepository.findById(id).orElse(null);
    }

    @Override
    public Authors createAuthor(Authors author) {
        Integer maxId = authorsRepository.findMaxId();
        author.setId(maxId == null ? 1 : maxId + 1);
        return authorsRepository.save(author);
    }

    @Override
    public Authors updateAuthor(Integer id, Authors author) {
        var existing = authorsRepository.findById(id).orElse(null);
        if (existing == null) return null;
        author.setId(id);
        return authorsRepository.save(author);
    }

    @Override
    public void deleteAuthor(Integer id) {
        authorsRepository.deleteById(id);
    }
}

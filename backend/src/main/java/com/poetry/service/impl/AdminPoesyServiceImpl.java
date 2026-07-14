package com.poetry.service.impl;

import com.poetry.entity.Poesy;
import com.poetry.repository.PoesyRepository;
import com.poetry.service.AdminPoesyService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdminPoesyServiceImpl implements AdminPoesyService {

    private final PoesyRepository poesyRepository;

    public AdminPoesyServiceImpl(PoesyRepository poesyRepository) {
        this.poesyRepository = poesyRepository;
    }

    @Override
    public Map<String, Object> getPoesyList(int page, int size, String keyword) {
        if (page < 1) page = 1;
        var pageable = PageRequest.of(page - 1, size);
        var pageResult = (keyword != null && !keyword.isBlank())
            ? poesyRepository.findByTitleContainingOrContentContaining(keyword, pageable)
            : poesyRepository.findAll(pageable);

        var content = pageResult.getContent().stream().map(p -> {
            var map = new LinkedHashMap<String, Object>();
            map.put("id", p.getId());
            map.put("title", p.getTitle());
            map.put("dynasty", p.getDynasty());
            map.put("writer", p.getWriter());
            map.put("writerId", p.getWriterId());
            map.put("writerImg", p.getWriterImg());
            map.put("content", p.getContent());
            map.put("type", p.getType());
            map.put("remark", p.getRemark());
            map.put("shangxi", p.getShangxi());
            map.put("translation", p.getTranslation());
            map.put("audioUrl", p.getAudioUrl());
            return map;
        }).collect(Collectors.toList());

        var result = new LinkedHashMap<String, Object>();
        result.put("content", content);
        result.put("totalPages", pageResult.getTotalPages());
        result.put("totalElements", pageResult.getTotalElements());
        return result;
    }

    @Override
    public Poesy getPoesyById(Integer id) {
        return poesyRepository.findById(id).orElse(null);
    }

    @Override
    public Poesy createPoesy(Poesy poesy) {
        Integer maxId = poesyRepository.findMaxId();
        poesy.setId(maxId == null ? 1 : maxId + 1);
        return poesyRepository.save(poesy);
    }

    @Override
    public Poesy updatePoesy(Integer id, Poesy poesy) {
        var existing = poesyRepository.findById(id).orElse(null);
        if (existing == null) return null;
        poesy.setId(id);
        return poesyRepository.save(poesy);
    }

    @Override
    public void deletePoesy(Integer id) {
        poesyRepository.deleteById(id);
    }
}

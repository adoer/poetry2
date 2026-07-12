package com.news.service.impl;

import com.news.entity.Poesy;
import com.news.repository.PoesyRepository;
import com.news.service.PoesyService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PoesyServiceImpl implements PoesyService {

    private static final int PAGE_SIZE = 10;
    private static final int MAX_PAGE = 5;

    private final PoesyRepository poesyRepository;

    public PoesyServiceImpl(PoesyRepository poesyRepository) {
        this.poesyRepository = poesyRepository;
    }

    @Override
    public List<Map<String, Object>> getPoesyList(int pageNum, boolean recommended) {
        if (pageNum < 1) pageNum = 1;
        if (pageNum > MAX_PAGE) pageNum = MAX_PAGE;

        int effectivePage;
        if (recommended) {
            long seed = LocalDate.now().toEpochDay();
            Random random = new Random(seed);
            int randomOffset = random.nextInt(MAX_PAGE);
            effectivePage = Math.min(pageNum + randomOffset - 1, MAX_PAGE - 1);
        } else {
            effectivePage = pageNum - 1;
        }

        List<Poesy> poesys = poesyRepository.findAll(PageRequest.of(effectivePage, PAGE_SIZE))
                .getContent();

        return poesys.stream().map(this::toMap).collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getPoesyById(Integer id) {
        Poesy poesy = poesyRepository.findById(id).orElse(null);
        if (poesy == null) return null;
        return toMap(poesy);
    }

    @Override
    public int getTotalPages() {
        return MAX_PAGE;
    }

    @Override
    public Map<String, Object> getPoesyListPage(int page, int size, boolean recommended) {
        return getPoesyListPage(page, size, recommended, null, null);
    }

    @Override
    public Map<String, Object> getPoesyListPage(int page, int size, boolean recommended, String keyword, String writer) {
        if (page < 1) page = 1;
        int effectivePage;
        if (recommended) {
            long seed = LocalDate.now().toEpochDay();
            Random random = new Random(seed);
            long totalDB = poesyRepository.count();
            int maxPageDB = Math.max(1, (int) Math.ceil((double) totalDB / size));
            int randomOffset = random.nextInt(Math.min(maxPageDB, 10));
            effectivePage = Math.min(page + randomOffset - 1, maxPageDB - 1);
        } else {
            effectivePage = page - 1;
        }

        org.springframework.data.domain.Page<Poesy> pageResult;
        if (writer != null && !writer.isBlank()) {
            pageResult = poesyRepository.findByWriterContaining(writer, PageRequest.of(effectivePage, size));
        } else if (keyword != null && !keyword.isBlank() && !"all".equals(keyword) && !"tuijian".equals(keyword)) {
            pageResult = poesyRepository.findByTitleContainingOrContentContaining(keyword, PageRequest.of(effectivePage, size));
        } else {
            pageResult = poesyRepository.findAll(PageRequest.of(effectivePage, size));
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("content", pageResult.getContent().stream().map(this::toMap).collect(Collectors.toList()));
        result.put("totalPages", pageResult.getTotalPages());
        result.put("totalElements", pageResult.getTotalElements());
        return result;
    }

    private Map<String, Object> toMap(Poesy p) {
        Map<String, Object> map = new LinkedHashMap<>();
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
    }
}

package com.poetry.service.impl;

import com.poetry.entity.Poesy;
import com.poetry.repository.PoesyRepository;
import com.poetry.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    private static final List<String> FIXED_CATEGORIES = List.of(
            "唐诗三百首", "古诗三百首", "宋词三百首", "小学古诗", "初中古诗",
            "高中古诗", "小学文言文", "初中文言文", "高中文言文", "宋词精选",
            "古诗十九首", "诗经", "乐府", "楚辞", "春天", "夏天", "秋天", "冬天"
    );

    private final PoesyRepository poesyRepository;

    public CategoryServiceImpl(PoesyRepository poesyRepository) {
        this.poesyRepository = poesyRepository;
    }

    @Override
    public List<String> getCategories() {
        List<Poesy> poesys = poesyRepository.findAll();
        Set<String> allTypes = new LinkedHashSet<>(FIXED_CATEGORIES);
        for (Poesy p : poesys) {
            if (p.getType() != null && !p.getType().isEmpty()) {
                try {
                    String[] types = p.getType().replace("[", "").replace("]", "")
                            .replace("\"", "").split(",");
                    for (String t : types) {
                        String trimmed = t.trim();
                        if (!trimmed.isEmpty()) {
                            allTypes.add(trimmed);
                        }
                    }
                } catch (Exception ignored) {
                }
            }
        }
        return new ArrayList<>(allTypes);
    }

    @Override
    public List<Map<String, Object>> getCategoryDetail(String category) {
        List<Poesy> poesys = poesyRepository.findByTypeContaining("\"" + category + "\"");
        return poesys.stream().map(p -> {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", p.getId());
            map.put("writer", p.getWriter());
            map.put("title", p.getTitle());
            return map;
        }).collect(Collectors.toList());
    }
}

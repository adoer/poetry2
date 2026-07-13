package com.poetry.service.impl;

import com.poetry.entity.Poesy;
import com.poetry.entity.Quotes;
import com.poetry.repository.FavoriteRepository;
import com.poetry.repository.PoesyRepository;
import com.poetry.repository.QuotesRepository;
import com.poetry.service.RecommendService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendServiceImpl implements RecommendService {

    private final PoesyRepository poesyRepository;
    private final QuotesRepository quotesRepository;
    private final FavoriteRepository favoriteRepository;

    private volatile Map<String, Object> cachedData;
    private volatile long cacheDate;

    public RecommendServiceImpl(PoesyRepository poesyRepository,
                                 QuotesRepository quotesRepository,
                                 FavoriteRepository favoriteRepository) {
        this.poesyRepository = poesyRepository;
        this.quotesRepository = quotesRepository;
        this.favoriteRepository = favoriteRepository;
    }

    @Override
    public List<Map<String, Object>> getRecommend(String username) {
        long today = LocalDate.now().toEpochDay();
        if (cachedData == null || cacheDate != today) {
            synchronized (this) {
                if (cachedData == null || cacheDate != today) {
                    generateCache(today);
                }
            }
        }

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> results = (List<Map<String, Object>>) cachedData.get("data");
        List<Map<String, Object>> resultCopy = results.stream()
                .map(m -> new LinkedHashMap<>(m))
                .collect(Collectors.toList());

        if (username != null && !username.isEmpty()) {
            var favorites = favoriteRepository.findByUsernameOrderByCreateTimeDesc(username);
            var favSet = favorites.stream()
                    .map(f -> f.getContentId())
                    .collect(Collectors.toSet());
            for (Map<String, Object> item : resultCopy) {
                String itemId = String.valueOf(item.getOrDefault("poetryId", item.get("id")));
                if (favSet.contains(itemId)) {
                    item.put("like", true);
                }
            }
        }

        return resultCopy;
    }

    private synchronized void generateCache(long today) {
        Random random = new Random(today);
        List<Map<String, Object>> results = new ArrayList<>();

        List<Poesy> poems = poesyRepository.findAll();
        int poemStart = poems.size() > 1000 ? random.nextInt(1000) : 0;
        for (int i = poemStart; i < Math.min(poemStart + 15, poems.size()); i++) {
            Poesy p = poems.get(i);
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", p.getId());
            map.put("title", p.getTitle());
            map.put("dynasty", p.getDynasty());
            map.put("writer", p.getWriter() != null ? p.getWriter() : "佚名");
            map.put("writerId", p.getWriterId());
            map.put("writerImg", p.getWriterImg());
            map.put("content", p.getContent());
            map.put("reComType", "Poesy");
            if (p.getType() != null && !p.getType().isEmpty()) {
                try {
                    String[] types = p.getType().replace("[", "").replace("]", "")
                            .replace("\"", "").split(",");
                    List<String> typeList = new ArrayList<>();
                    for (String t : types) {
                        String trimmed = t.trim();
                        if (!trimmed.isEmpty()) typeList.add(trimmed);
                    }
                    map.put("type", typeList);
                } catch (Exception e) {
                    map.put("type", List.of());
                }
            } else {
                map.put("type", List.of());
            }
            results.add(map);
        }

        List<Quotes> quotesList = quotesRepository.findAll();
        int quoteStart = quotesList.size() > 2000 ? random.nextInt(2000) : 0;
        for (int i = quoteStart; i < Math.min(quoteStart + 5, quotesList.size()); i++) {
            Quotes q = quotesList.get(i);
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", q.getId());
            map.put("poetryId", q.getPoetryId());
            map.put("poetryName", q.getPoetryName());
            map.put("content", q.getContent());
            map.put("writer", q.getWriter() != null ? q.getWriter() : "佚名");
            map.put("writerId", q.getWriterId());
            map.put("reComType", "Quotes");
            results.add(map);
        }

        results.sort(Comparator.comparing(m -> {
            String w = (String) m.get("writer");
            return (w == null || w.isEmpty()) ? '~' : w.charAt(0);
        }));

        Map<String, Object> newCache = new HashMap<>();
        newCache.put("data", results);
        cachedData = newCache;
        cacheDate = today;
    }
}

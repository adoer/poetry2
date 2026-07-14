package com.poetry.service.impl;

import com.poetry.repository.FavoriteRepository;
import com.poetry.repository.PoesyRepository;
import com.poetry.repository.QuotesRepository;
import com.poetry.repository.AuthorsRepository;
import com.poetry.repository.UserRepository;
import com.poetry.service.AdminStatsService;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class AdminStatsServiceImpl implements AdminStatsService {

    private final PoesyRepository poesyRepository;
    private final QuotesRepository quotesRepository;
    private final AuthorsRepository authorsRepository;
    private final UserRepository userRepository;
    private final FavoriteRepository favoriteRepository;

    public AdminStatsServiceImpl(PoesyRepository poesyRepository,
                                  QuotesRepository quotesRepository,
                                  AuthorsRepository authorsRepository,
                                  UserRepository userRepository,
                                  FavoriteRepository favoriteRepository) {
        this.poesyRepository = poesyRepository;
        this.quotesRepository = quotesRepository;
        this.authorsRepository = authorsRepository;
        this.userRepository = userRepository;
        this.favoriteRepository = favoriteRepository;
    }

    @Override
    public Map<String, Object> getOverview() {
        var result = new LinkedHashMap<String, Object>();
        result.put("poesyCount", poesyRepository.count());
        result.put("quotesCount", quotesRepository.count());
        result.put("authorsCount", authorsRepository.count());
        result.put("userCount", userRepository.count());
        return result;
    }

    @Override
    public Map<String, Object> getRegistrationTrend() {
        var result = new LinkedHashMap<String, Object>();
        var users = userRepository.findAll();
        var dailyCount = new LinkedHashMap<String, Long>();
        for (var user : users) {
            var day = user.getCreateTime();
            if (day != null && day.length() >= 10) {
                day = day.substring(0, 10);
                dailyCount.merge(day, 1L, Long::sum);
            }
        }
        result.put("daily", dailyCount);
        return result;
    }

    @Override
    public Map<String, Object> getFavoriteDistribution() {
        var favorites = favoriteRepository.findAll();
        long poesyCount = favorites.stream().filter(f -> "poesy".equals(f.getType())).count();
        long quotesCount = favorites.stream().filter(f -> "quotes".equals(f.getType())).count();
        var result = new LinkedHashMap<String, Object>();
        result.put("poesy", poesyCount);
        result.put("quotes", quotesCount);
        return result;
    }
}

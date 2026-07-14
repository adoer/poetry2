package com.poetry.service;

import java.util.Map;

public interface AdminStatsService {
    Map<String, Object> getOverview();
    Map<String, Object> getRegistrationTrend();
    Map<String, Object> getFavoriteDistribution();
}

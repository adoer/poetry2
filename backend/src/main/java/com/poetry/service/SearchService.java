package com.poetry.service;

import java.util.List;
import java.util.Map;

public interface SearchService {
    Map<String, List<Map<String, Object>>> searchAll(String keyword);
}

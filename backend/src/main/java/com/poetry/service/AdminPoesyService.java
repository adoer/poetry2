package com.poetry.service;

import com.poetry.entity.Poesy;
import java.util.Map;

public interface AdminPoesyService {
    Map<String, Object> getPoesyList(int page, int size, String keyword);
    Poesy getPoesyById(Integer id);
    Poesy createPoesy(Poesy poesy);
    Poesy updatePoesy(Integer id, Poesy poesy);
    void deletePoesy(Integer id);
}

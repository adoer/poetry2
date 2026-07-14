package com.poetry.service;

import com.poetry.entity.Authors;
import java.util.Map;

public interface AdminAuthorsService {
    Map<String, Object> getAuthorsList(int page, int size, String keyword);
    Authors getAuthorById(Integer id);
    Authors createAuthor(Authors author);
    Authors updateAuthor(Integer id, Authors author);
    void deleteAuthor(Integer id);
}

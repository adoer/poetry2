package com.poetry.service;

import java.util.Map;

public interface AdminUserService {
    Map<String, Object> getUserList(int page, int size, String keyword);
    Map<String, Object> getUserById(String id);
}

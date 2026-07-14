package com.poetry.service.impl;

import com.poetry.entity.User;
import com.poetry.repository.UserRepository;
import com.poetry.service.AdminUserService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdminUserServiceImpl implements AdminUserService {

    private final UserRepository userRepository;

    public AdminUserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Map<String, Object> getUserList(int page, int size, String keyword) {
        if (page < 1) page = 1;
        var pageable = PageRequest.of(page - 1, size);

        var pageResult = (keyword != null && !keyword.isBlank())
            ? userRepository.searchByKeyword(keyword, pageable)
            : userRepository.findAll(pageable);

        var content = pageResult.getContent().stream().map(u -> {
            var map = new LinkedHashMap<String, Object>();
            map.put("id", u.getId());
            map.put("username", u.getUsername());
            map.put("nickname", u.getNickname());
            map.put("phone", u.getPhone());
            map.put("email", u.getEmail());
            map.put("vipLevel", u.getVipLevel());
            map.put("createTime", u.getCreateTime());
            map.put("lastlogintime", u.getLastlogintime());
            map.put("ip", u.getIp());
            return map;
        }).collect(Collectors.toList());

        var result = new LinkedHashMap<String, Object>();
        result.put("content", content);
        result.put("totalPages", pageResult.getTotalPages());
        result.put("totalElements", pageResult.getTotalElements());
        return result;
    }

    @Override
    public Map<String, Object> getUserById(String id) {
        var user = userRepository.findById(id).orElse(null);
        if (user == null) return null;
        var map = new LinkedHashMap<String, Object>();
        map.put("id", user.getId());
        map.put("username", user.getUsername());
        map.put("nickname", user.getNickname());
        map.put("phone", user.getPhone());
        map.put("email", user.getEmail());
        map.put("vipLevel", user.getVipLevel());
        map.put("createTime", user.getCreateTime());
        map.put("lastlogintime", user.getLastlogintime());
        map.put("ip", user.getIp());
        return map;
    }
}

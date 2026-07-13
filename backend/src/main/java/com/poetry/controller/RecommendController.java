package com.poetry.controller;

import com.poetry.dto.Result;
import com.poetry.service.RecommendService;
import com.poetry.util.JwtUtil;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recommend")
public class RecommendController {

    private final RecommendService recommendService;
    private final JwtUtil jwtUtil;

    public RecommendController(RecommendService recommendService, JwtUtil jwtUtil) {
        this.recommendService = recommendService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping
    public Result<?> getRecommend(@RequestHeader(value = "Authorization", required = false) String token) {
        String username = null;
        if (token != null && token.startsWith("Bearer ")) {
            try {
                username = jwtUtil.getUsername(token.substring(7));
            } catch (Exception ignored) {
            }
        }
        return Result.success(recommendService.getRecommend(username));
    }
}

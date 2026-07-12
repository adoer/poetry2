package com.news.controller;

import com.news.dto.Result;
import com.news.service.RecommendService;
import com.news.util.JwtUtil;
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

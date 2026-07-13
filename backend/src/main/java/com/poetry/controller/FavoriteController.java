package com.poetry.controller;

import com.poetry.dto.Result;
import com.poetry.service.FavoriteService;
import com.poetry.util.JwtUtil;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/favorite")
public class FavoriteController {

    private final FavoriteService favoriteService;
    private final JwtUtil jwtUtil;

    public FavoriteController(FavoriteService favoriteService, JwtUtil jwtUtil) {
        this.favoriteService = favoriteService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping
    public Result<?> getFavorites(@RequestHeader("Authorization") String token) {
        String username = extractUsername(token);
        if (username == null) {
            return Result.error(400, "未登录");
        }
        return Result.success(favoriteService.getFavorites(username));
    }

    @PostMapping
    public Result<?> addFavorite(@RequestHeader("Authorization") String token,
                                  @RequestBody Map<String, String> body) {
        String username = extractUsername(token);
        if (username == null) {
            return Result.error(400, "未登录");
        }
        boolean ok = favoriteService.addFavorite(
                username,
                body.get("id"),
                body.get("type"),
                body.get("title"),
                body.get("content"),
                body.get("writer")
        );
        if (!ok) {
            return Result.error(409, "已收藏");
        }
        return Result.success(true);
    }

    @DeleteMapping
    public Result<?> deleteFavorite(@RequestHeader("Authorization") String token,
                                     @RequestBody Map<String, String> body) {
        String username = extractUsername(token);
        if (username == null) {
            return Result.error(400, "未登录");
        }
        boolean ok = favoriteService.deleteFavorite(
                username,
                body.get("contentId"),
                body.get("id")
        );
        if (!ok) {
            return Result.error(400, "取消失败");
        }
        return Result.success(true);
    }

    private String extractUsername(String token) {
        try {
            if (token != null && token.startsWith("Bearer ")) {
                return jwtUtil.getUsername(token.substring(7));
            }
        } catch (Exception ignored) {
        }
        return null;
    }
}

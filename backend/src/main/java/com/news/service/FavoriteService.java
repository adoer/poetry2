package com.news.service;

import com.news.entity.Favorite;

import java.util.List;

public interface FavoriteService {
    List<Favorite> getFavorites(String username);
    boolean addFavorite(String username, String id, String type, String title, String content, String writer);
    boolean deleteFavorite(String username, String contentId, String id);
}

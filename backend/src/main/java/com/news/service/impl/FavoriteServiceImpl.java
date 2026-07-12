package com.news.service.impl;

import com.news.entity.Favorite;
import com.news.repository.FavoriteRepository;
import com.news.service.FavoriteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteRepository favoriteRepository;

    public FavoriteServiceImpl(FavoriteRepository favoriteRepository) {
        this.favoriteRepository = favoriteRepository;
    }

    @Override
    public List<Favorite> getFavorites(String username) {
        return favoriteRepository.findByUsernameOrderByCreateTimeDesc(username);
    }

    @Override
    @Transactional
    public boolean addFavorite(String username, String id, String type, String title, String content, String writer) {
        if (id == null || type == null) return false;

        if (favoriteRepository.findByContentIdAndUsername(id, username).isPresent()) {
            return false;
        }

        Favorite favorite = new Favorite();
        favorite.setContentId(id);
        favorite.setType(type);
        favorite.setTitle(title);
        favorite.setContent(content);
        favorite.setWriter(writer);
        favorite.setUsername(username);
        favorite.setCreateTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        favoriteRepository.save(favorite);
        return true;
    }

    @Override
    @Transactional
    public boolean deleteFavorite(String username, String contentId, String id) {
        if (id != null) {
            try {
                Favorite fav = favoriteRepository.findById(Integer.parseInt(id)).orElse(null);
                if (fav != null && fav.getUsername().equals(username)) {
                    favoriteRepository.delete(fav);
                    return true;
                }
            } catch (NumberFormatException ignored) {
            }
        } else if (contentId != null) {
            Favorite fav = favoriteRepository.findByContentIdAndUsername(contentId, username).orElse(null);
            if (fav != null) {
                favoriteRepository.delete(fav);
                return true;
            }
        }
        return false;
    }
}

package com.poetry.repository;

import com.poetry.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Integer> {

    List<Favorite> findByUsernameOrderByCreateTimeDesc(String username);

    Optional<Favorite> findByContentIdAndUsername(String contentId, String username);

    Optional<Favorite> findByContentId(String contentId);
}

package com.poetry.repository;

import com.poetry.entity.Authors;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AuthorsRepository extends JpaRepository<Authors, Integer> {

    List<Authors> findByNameContaining(String keyword);

    org.springframework.data.domain.Page<Authors> findByNameContaining(String keyword, org.springframework.data.domain.Pageable pageable);

    @Query("SELECT MAX(a.id) FROM Authors a")
    Integer findMaxId();
}

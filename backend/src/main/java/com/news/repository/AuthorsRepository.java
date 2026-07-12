package com.news.repository;

import com.news.entity.Authors;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuthorsRepository extends JpaRepository<Authors, Integer> {

    List<Authors> findByNameContaining(String keyword);
}

package com.poetry.repository;

import com.poetry.entity.Quotes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuotesRepository extends JpaRepository<Quotes, Integer> {

    List<Quotes> findByContentContaining(String keyword);

    org.springframework.data.domain.Page<Quotes> findByContentContaining(String keyword, org.springframework.data.domain.Pageable pageable);
}

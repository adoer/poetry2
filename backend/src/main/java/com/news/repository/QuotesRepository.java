package com.news.repository;

import com.news.entity.Quotes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuotesRepository extends JpaRepository<Quotes, Integer> {

    List<Quotes> findByContentContaining(String keyword);
}

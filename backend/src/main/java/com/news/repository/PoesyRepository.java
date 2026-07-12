package com.news.repository;

import com.news.entity.Poesy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PoesyRepository extends JpaRepository<Poesy, Integer> {

    @Query("SELECT p FROM Poesy p WHERE p.type LIKE %:category%")
    List<Poesy> findByTypeContaining(@Param("category") String category);

    @Query("SELECT p FROM Poesy p WHERE p.content LIKE %:keyword%")
    List<Poesy> findByContentContaining(@Param("keyword") String keyword, org.springframework.data.domain.Pageable pageable);

    @Query("SELECT p FROM Poesy p WHERE p.title LIKE %:keyword%")
    List<Poesy> findByTitleContaining(@Param("keyword") String keyword, org.springframework.data.domain.Pageable pageable);

}

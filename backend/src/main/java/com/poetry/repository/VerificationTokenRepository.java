package com.poetry.repository;

import com.poetry.entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, String> {
    Optional<VerificationToken> findByToken(String token);
    Optional<VerificationToken> findByUsernameAndType(String username, String type);
}

package com.deepconnect.ats.auth.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.deepconnect.ats.auth.model.RefreshToken;

@Repository
public interface RefreshTokenRepo extends JpaRepository<RefreshToken, Long> {
    RefreshToken getByToken(String token);
    RefreshToken getByUserId(Long userId);
    void deleteByToken(String token);
    void deleteByUserId(Long userId);
}

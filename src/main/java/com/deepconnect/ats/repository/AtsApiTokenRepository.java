package com.deepconnect.ats.repository;

import com.deepconnect.ats.model.AtsApiToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AtsApiTokenRepository extends JpaRepository<AtsApiToken, Long> {
    Optional<AtsApiToken> findByToken(String token);
}

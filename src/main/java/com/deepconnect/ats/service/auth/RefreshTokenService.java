package com.deepconnect.ats.service.auth;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.deepconnect.ats.model.RefreshToken;
import com.deepconnect.ats.repository.RefreshTokenRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepo refreshTokenRepository;

    @Value("${jwt.refreshExpirationMs}")
    private Long refreshTokenDurationMs;

    @Transactional
    public RefreshToken createRefreshToken(Long userId, String email) {
        this.deleteByUserId(userId);//delete if there's an existing token

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUserId(userId);
        refreshToken.setEmail(email);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshToken.setLastRefreshedDateTime(LocalDateTime.now());
        return refreshTokenRepository.save(refreshToken);
    }

    @Transactional
    public boolean isExpired(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            return true;
        }
        return false;
    }

    @Transactional
    public void deleteByToken(String token) {
        refreshTokenRepository.deleteByToken(token);
    }

    @Transactional
    public void deleteByUserId(Long userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }

    public RefreshToken getByToken(String token) {
        return refreshTokenRepository.getByToken(token);
    }

    public RefreshToken getByUserId(Long userId) {
        return refreshTokenRepository.getByUserId(userId);
    }
}

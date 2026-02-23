package com.sribank.authservice.domain.repository;

import com.sribank.authservice.domain.model.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository {

    RefreshToken save(RefreshToken refreshToken);

    Optional<RefreshToken> findActiveByToken(String token);

    void revokeByToken(String token);
}

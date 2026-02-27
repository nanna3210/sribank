package com.sribank.authservice.domain.repository;

import com.sribank.authservice.domain.model.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository {

    RefreshToken save(RefreshToken refreshToken);

    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findActiveByToken(String token);

    void revokeByToken(String token);

    void revokeByFamilyId(String familyId);
}

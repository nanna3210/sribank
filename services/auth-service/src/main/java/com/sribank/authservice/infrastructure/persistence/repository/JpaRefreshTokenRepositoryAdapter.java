package com.sribank.authservice.infrastructure.persistence.repository;

import com.sribank.authservice.domain.model.RefreshToken;
import com.sribank.authservice.domain.repository.RefreshTokenRepository;
import com.sribank.authservice.infrastructure.persistence.entity.RefreshTokenJpaEntity;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class JpaRefreshTokenRepositoryAdapter implements RefreshTokenRepository {

    private final SpringDataRefreshTokenRepository springDataRefreshTokenRepository;

    public JpaRefreshTokenRepositoryAdapter(SpringDataRefreshTokenRepository springDataRefreshTokenRepository) {
        this.springDataRefreshTokenRepository = springDataRefreshTokenRepository;
    }

    @Override
    public RefreshToken save(RefreshToken refreshToken) {
        RefreshTokenJpaEntity saved = springDataRefreshTokenRepository.save(toEntity(refreshToken));
        return toDomain(saved);
    }

    @Override
    public Optional<RefreshToken> findActiveByToken(String token) {
        return springDataRefreshTokenRepository.findByTokenAndRevokedFalse(token).map(this::toDomain);
    }

    @Override
    @Transactional
    public void revokeByToken(String token) {
        springDataRefreshTokenRepository.revokeByToken(token);
    }

    private RefreshTokenJpaEntity toEntity(RefreshToken refreshToken) {
        return new RefreshTokenJpaEntity(
                refreshToken.getId(),
                refreshToken.getUserId(),
                refreshToken.getToken(),
                refreshToken.getExpiryDate(),
                refreshToken.isRevoked()
        );
    }

    private RefreshToken toDomain(RefreshTokenJpaEntity entity) {
        return RefreshToken.restore(
                entity.getId(),
                entity.getUserId(),
                entity.getToken(),
                entity.getExpiryDate(),
                entity.isRevoked()
        );
    }
}

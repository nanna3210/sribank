package com.sribank.authservice.infrastructure.persistence.repository;

import com.sribank.authservice.infrastructure.persistence.entity.RefreshTokenJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SpringDataRefreshTokenRepository extends JpaRepository<RefreshTokenJpaEntity, String> {

    Optional<RefreshTokenJpaEntity> findByToken(String token);

    Optional<RefreshTokenJpaEntity> findByTokenAndRevokedFalse(String token);

    @Modifying
    @Query("update RefreshTokenJpaEntity t set t.revoked = true where t.token = :token and t.revoked = false")
    void revokeByToken(@Param("token") String token);

    @Modifying
    @Query("update RefreshTokenJpaEntity t set t.revoked = true where t.familyId = :familyId and t.revoked = false")
    void revokeByFamilyId(@Param("familyId") String familyId);
}

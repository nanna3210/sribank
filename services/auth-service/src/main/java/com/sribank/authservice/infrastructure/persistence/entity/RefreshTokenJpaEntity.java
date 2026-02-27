package com.sribank.authservice.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "refresh_token")
public class RefreshTokenJpaEntity {

    @Id
    @Column(name = "id", nullable = false, length = 36)
    private String id;

    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;

    @Column(name = "token", nullable = false, length = 512, unique = true)
    private String token;

    @Column(name = "expiry_date", nullable = false)
    private Instant expiryDate;

    @Column(name = "family_id", nullable = false, length = 36)
    private String familyId;

    @Column(name = "parent_token_id", length = 36)
    private String parentTokenId;

    @Column(name = "revoked", nullable = false)
    private boolean revoked;

    protected RefreshTokenJpaEntity() {
    }

    public RefreshTokenJpaEntity(String id,
                                 String userId,
                                 String token,
                                 Instant expiryDate,
                                 String familyId,
                                 String parentTokenId,
                                 boolean revoked) {
        this.id = id;
        this.userId = userId;
        this.token = token;
        this.expiryDate = expiryDate;
        this.familyId = familyId;
        this.parentTokenId = parentTokenId;
        this.revoked = revoked;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getToken() {
        return token;
    }

    public Instant getExpiryDate() {
        return expiryDate;
    }

    public String getFamilyId() {
        return familyId;
    }

    public String getParentTokenId() {
        return parentTokenId;
    }

    public boolean isRevoked() {
        return revoked;
    }
}

package com.sribank.authservice.domain.model;

import java.time.Instant;
import java.util.UUID;

public class RefreshToken {

    private final String id;
    private final String userId;
    private final String token;
    private final Instant expiryDate;
    private final String familyId;
    private final String parentTokenId;
    private final boolean revoked;

    private RefreshToken(String id,
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

    public static RefreshToken issueNewFamily(String userId, String token, Instant expiryDate) {
        String tokenId = UUID.randomUUID().toString();
        return new RefreshToken(tokenId, userId, token, expiryDate, tokenId, null, false);
    }

    public static RefreshToken issueInFamily(String userId,
                                             String token,
                                             Instant expiryDate,
                                             String familyId,
                                             String parentTokenId) {
        return new RefreshToken(UUID.randomUUID().toString(), userId, token, expiryDate, familyId, parentTokenId, false);
    }

    public static RefreshToken restore(String id,
                                       String userId,
                                       String token,
                                       Instant expiryDate,
                                       String familyId,
                                       String parentTokenId,
                                       boolean revoked) {
        return new RefreshToken(id, userId, token, expiryDate, familyId, parentTokenId, revoked);
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

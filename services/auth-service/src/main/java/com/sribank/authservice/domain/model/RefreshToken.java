package com.sribank.authservice.domain.model;

import java.time.Instant;
import java.util.UUID;

public class RefreshToken {

    private final String id;
    private final String userId;
    private final String token;
    private final Instant expiryDate;
    private final boolean revoked;

    private RefreshToken(String id, String userId, String token, Instant expiryDate, boolean revoked) {
        this.id = id;
        this.userId = userId;
        this.token = token;
        this.expiryDate = expiryDate;
        this.revoked = revoked;
    }

    public static RefreshToken issue(String userId, String token, Instant expiryDate) {
        return new RefreshToken(UUID.randomUUID().toString(), userId, token, expiryDate, false);
    }

    public static RefreshToken restore(String id, String userId, String token, Instant expiryDate, boolean revoked) {
        return new RefreshToken(id, userId, token, expiryDate, revoked);
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

    public boolean isRevoked() {
        return revoked;
    }
}

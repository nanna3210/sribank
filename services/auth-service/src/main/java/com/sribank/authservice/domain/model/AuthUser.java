package com.sribank.authservice.domain.model;

import java.time.Instant;
import java.util.UUID;

public class AuthUser {

    private final String id;
    private final String username;
    private final String email;
    private final String passwordHash;
    private final boolean active;
    private final Instant createdAt;

    private AuthUser(String id, String username, String email, String passwordHash, boolean active, Instant createdAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.active = active;
        this.createdAt = createdAt;
    }

    public static AuthUser create(String username, String email, String passwordHash) {
        return new AuthUser(UUID.randomUUID().toString(), username, email, passwordHash, true, Instant.now());
    }

    public static AuthUser restore(String id, String username, String email, String passwordHash, boolean active, Instant createdAt) {
        return new AuthUser(id, username, email, passwordHash, active, createdAt);
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public boolean isActive() {
        return active;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}

package com.sribank.authservice.domain.model;

import java.time.Instant;
import java.util.UUID;

public class LoginAttempt {

    private final String id;
    private final String username;
    private final boolean success;
    private final String failureReason;
    private final Instant attemptTime;

    private LoginAttempt(String id, String username, boolean success, String failureReason, Instant attemptTime) {
        this.id = id;
        this.username = username;
        this.success = success;
        this.failureReason = failureReason;
        this.attemptTime = attemptTime;
    }

    public static LoginAttempt success(String username) {
        return new LoginAttempt(UUID.randomUUID().toString(), username, true, null, Instant.now());
    }

    public static LoginAttempt failure(String username, String failureReason) {
        return new LoginAttempt(UUID.randomUUID().toString(), username, false, failureReason, Instant.now());
    }

    public static LoginAttempt restore(String id, String username, boolean success, String failureReason, Instant attemptTime) {
        return new LoginAttempt(id, username, success, failureReason, attemptTime);
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public Instant getAttemptTime() {
        return attemptTime;
    }
}

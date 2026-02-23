package com.sribank.authservice.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "login_attempt")
public class LoginAttemptJpaEntity {

    @Id
    @Column(name = "id", nullable = false, length = 36)
    private String id;

    @Column(name = "username", nullable = false, length = 50)
    private String username;

    @Column(name = "success", nullable = false)
    private boolean success;

    @Column(name = "failure_reason", length = 255)
    private String failureReason;

    @Column(name = "attempt_time", nullable = false)
    private Instant attemptTime;

    protected LoginAttemptJpaEntity() {
    }

    public LoginAttemptJpaEntity(String id, String username, boolean success, String failureReason, Instant attemptTime) {
        this.id = id;
        this.username = username;
        this.success = success;
        this.failureReason = failureReason;
        this.attemptTime = attemptTime;
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

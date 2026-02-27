package com.sribank.authservice.domain.repository;

import com.sribank.authservice.domain.model.LoginAttempt;

import java.time.Instant;
import java.util.Optional;

public interface LoginAttemptRepository {

    LoginAttempt save(LoginAttempt loginAttempt);

    long countFailedAttemptsSince(String username, Instant since);

    Optional<Instant> findLatestFailedAttemptTime(String username);
}

package com.sribank.authservice.infrastructure.persistence.repository;

import com.sribank.authservice.infrastructure.persistence.entity.LoginAttemptJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Optional;

public interface SpringDataLoginAttemptRepository extends JpaRepository<LoginAttemptJpaEntity, String> {

    long countByUsernameAndSuccessFalseAndAttemptTimeAfter(String username, Instant attemptTime);

    Optional<LoginAttemptJpaEntity> findTopByUsernameAndSuccessFalseOrderByAttemptTimeDesc(String username);
}

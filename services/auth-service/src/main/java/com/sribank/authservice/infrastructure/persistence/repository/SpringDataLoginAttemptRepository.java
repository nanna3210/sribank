package com.sribank.authservice.infrastructure.persistence.repository;

import com.sribank.authservice.infrastructure.persistence.entity.LoginAttemptJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataLoginAttemptRepository extends JpaRepository<LoginAttemptJpaEntity, String> {
}

package com.sribank.authservice.infrastructure.persistence.repository;

import com.sribank.authservice.infrastructure.persistence.entity.AuthUserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDataAuthUserRepository extends JpaRepository<AuthUserJpaEntity, String> {

    Optional<AuthUserJpaEntity> findByUsername(String username);

    boolean existsByUsernameOrEmail(String username, String email);
}

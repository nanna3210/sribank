package com.sribank.userservice.infrastructure.persistence.repository;

import com.sribank.userservice.infrastructure.persistence.entity.UserProfileJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDataUserProfileRepository extends JpaRepository<UserProfileJpaEntity, String> {

    boolean existsByUserId(String userId);

    Optional<UserProfileJpaEntity> findByUserId(String userId);
}

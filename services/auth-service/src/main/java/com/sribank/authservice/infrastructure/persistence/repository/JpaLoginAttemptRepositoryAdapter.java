package com.sribank.authservice.infrastructure.persistence.repository;

import com.sribank.authservice.domain.model.LoginAttempt;
import com.sribank.authservice.domain.repository.LoginAttemptRepository;
import com.sribank.authservice.infrastructure.persistence.entity.LoginAttemptJpaEntity;
import org.springframework.stereotype.Repository;

@Repository
public class JpaLoginAttemptRepositoryAdapter implements LoginAttemptRepository {

    private final SpringDataLoginAttemptRepository springDataLoginAttemptRepository;

    public JpaLoginAttemptRepositoryAdapter(SpringDataLoginAttemptRepository springDataLoginAttemptRepository) {
        this.springDataLoginAttemptRepository = springDataLoginAttemptRepository;
    }

    @Override
    public LoginAttempt save(LoginAttempt loginAttempt) {
        LoginAttemptJpaEntity saved = springDataLoginAttemptRepository.save(toEntity(loginAttempt));
        return toDomain(saved);
    }

    private LoginAttemptJpaEntity toEntity(LoginAttempt loginAttempt) {
        return new LoginAttemptJpaEntity(
                loginAttempt.getId(),
                loginAttempt.getUsername(),
                loginAttempt.isSuccess(),
                loginAttempt.getFailureReason(),
                loginAttempt.getAttemptTime()
        );
    }

    private LoginAttempt toDomain(LoginAttemptJpaEntity entity) {
        return LoginAttempt.restore(
                entity.getId(),
                entity.getUsername(),
                entity.isSuccess(),
                entity.getFailureReason(),
                entity.getAttemptTime()
        );
    }
}

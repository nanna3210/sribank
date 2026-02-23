package com.sribank.authservice.infrastructure.persistence.repository;

import com.sribank.authservice.domain.model.AuthUser;
import com.sribank.authservice.domain.repository.AuthUserRepository;
import com.sribank.authservice.infrastructure.persistence.entity.AuthUserJpaEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class JpaAuthUserRepositoryAdapter implements AuthUserRepository {

    private final SpringDataAuthUserRepository springDataAuthUserRepository;

    public JpaAuthUserRepositoryAdapter(SpringDataAuthUserRepository springDataAuthUserRepository) {
        this.springDataAuthUserRepository = springDataAuthUserRepository;
    }

    @Override
    public Optional<AuthUser> findById(String id) {
        return springDataAuthUserRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<AuthUser> findByUsername(String username) {
        return springDataAuthUserRepository.findByUsername(username).map(this::toDomain);
    }

    @Override
    public boolean existsByUsernameOrEmail(String username, String email) {
        return springDataAuthUserRepository.existsByUsernameOrEmail(username, email);
    }

    @Override
    public AuthUser save(AuthUser user) {
        AuthUserJpaEntity entity = toEntity(user);
        AuthUserJpaEntity saved = springDataAuthUserRepository.save(entity);
        return toDomain(saved);
    }

    private AuthUser toDomain(AuthUserJpaEntity entity) {
        return AuthUser.restore(
                entity.getId(),
                entity.getUsername(),
                entity.getEmail(),
                entity.getPasswordHash(),
                entity.isActive(),
                entity.getCreatedAt()
        );
    }

    private AuthUserJpaEntity toEntity(AuthUser domain) {
        return new AuthUserJpaEntity(
                domain.getId(),
                domain.getUsername(),
                domain.getEmail(),
                domain.getPasswordHash(),
                domain.isActive(),
                domain.getCreatedAt()
        );
    }
}

package com.sribank.userservice.infrastructure.persistence.repository;

import com.sribank.userservice.domain.model.UserProfile;
import com.sribank.userservice.domain.repository.UserProfileRepository;
import com.sribank.userservice.infrastructure.persistence.entity.UserProfileJpaEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class JpaUserProfileRepositoryAdapter implements UserProfileRepository {

    private final SpringDataUserProfileRepository springDataUserProfileRepository;

    public JpaUserProfileRepositoryAdapter(SpringDataUserProfileRepository springDataUserProfileRepository) {
        this.springDataUserProfileRepository = springDataUserProfileRepository;
    }

    @Override
    public boolean existsByUserId(String userId) {
        return springDataUserProfileRepository.existsByUserId(userId);
    }

    @Override
    public UserProfile save(UserProfile userProfile) {
        UserProfileJpaEntity saved = springDataUserProfileRepository.save(toEntity(userProfile));
        return toDomain(saved);
    }

    @Override
    public Optional<UserProfile> findByUserId(String userId) {
        return springDataUserProfileRepository.findByUserId(userId).map(this::toDomain);
    }

    private UserProfileJpaEntity toEntity(UserProfile userProfile) {
        return new UserProfileJpaEntity(
                userProfile.getId(),
                userProfile.getUserId(),
                userProfile.getFirstName(),
                userProfile.getLastName(),
                userProfile.getPhoneNumber(),
                userProfile.getCreatedAt(),
                userProfile.getUpdatedAt()
        );
    }

    private UserProfile toDomain(UserProfileJpaEntity entity) {
        return UserProfile.restore(
                entity.getId(),
                entity.getUserId(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getPhoneNumber(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}

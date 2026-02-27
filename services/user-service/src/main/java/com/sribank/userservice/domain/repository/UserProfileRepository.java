package com.sribank.userservice.domain.repository;

import com.sribank.userservice.domain.model.UserProfile;

import java.util.Optional;

public interface UserProfileRepository {

    boolean existsByUserId(String userId);

    UserProfile save(UserProfile userProfile);

    Optional<UserProfile> findByUserId(String userId);
}

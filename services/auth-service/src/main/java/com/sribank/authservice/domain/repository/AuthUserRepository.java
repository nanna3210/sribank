package com.sribank.authservice.domain.repository;

import com.sribank.authservice.domain.model.AuthUser;

import java.util.List;
import java.util.Optional;

public interface AuthUserRepository {

    Optional<AuthUser> findById(String id);

    Optional<AuthUser> findByUsername(String username);

    boolean existsByUsernameOrEmail(String username, String email);

    AuthUser save(AuthUser user);

    List<String> findRoleCodesByUserId(String userId);

    void assignRole(String userId, String roleCode);
}

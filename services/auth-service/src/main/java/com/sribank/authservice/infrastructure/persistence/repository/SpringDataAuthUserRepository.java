package com.sribank.authservice.infrastructure.persistence.repository;

import com.sribank.authservice.infrastructure.persistence.entity.AuthUserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SpringDataAuthUserRepository extends JpaRepository<AuthUserJpaEntity, String> {

    Optional<AuthUserJpaEntity> findByUsername(String username);

    boolean existsByUsernameOrEmail(String username, String email);

    @Query(value = """
            SELECT r.code
            FROM auth_role r
            JOIN user_role ur ON ur.role_id = r.id
            WHERE ur.user_id = :userId
            """, nativeQuery = true)
    List<String> findRoleCodesByUserId(@Param("userId") String userId);

    @Modifying
    @Query(value = """
            INSERT INTO user_role (user_id, role_id, assigned_at)
            SELECT :userId, r.id, CURRENT_TIMESTAMP
            FROM auth_role r
            WHERE r.code = :roleCode
            AND NOT EXISTS (
                SELECT 1
                FROM user_role ur
                WHERE ur.user_id = :userId AND ur.role_id = r.id
            )
            """, nativeQuery = true)
    void assignRoleByCode(@Param("userId") String userId, @Param("roleCode") String roleCode);
}

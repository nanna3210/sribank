package com.sribank.authservice.integration;

import com.sribank.authservice.application.command.LoginCommand;
import com.sribank.authservice.application.command.LogoutCommand;
import com.sribank.authservice.application.command.RefreshTokenCommand;
import com.sribank.authservice.application.command.RegisterCommand;
import com.sribank.authservice.application.dto.AuthResult;
import com.sribank.authservice.application.usecase.LoginUseCase;
import com.sribank.authservice.application.usecase.LogoutUseCase;
import com.sribank.authservice.application.usecase.RefreshTokenUseCase;
import com.sribank.authservice.application.usecase.RegisterUseCase;
import com.sribank.authservice.domain.exception.RefreshTokenReuseDetectedException;
import com.sribank.authservice.domain.repository.RefreshTokenRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Testcontainers
class AuthFlowIntegrationTest {

    @Container
    static final MySQLContainer<?> MYSQL = new MySQLContainer<>("mysql:8.0.36")
            .withDatabaseName("sribank_auth_it")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureDatasource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", MYSQL::getJdbcUrl);
        registry.add("spring.datasource.username", MYSQL::getUsername);
        registry.add("spring.datasource.password", MYSQL::getPassword);
        registry.add("spring.flyway.enabled", () -> true);
        registry.add("security.jwt.secret", () -> "test-secret-key-with-at-least-32-characters");
    }

    @Autowired
    private RegisterUseCase registerUseCase;

    @Autowired
    private LoginUseCase loginUseCase;

    @Autowired
    private RefreshTokenUseCase refreshTokenUseCase;

    @Autowired
    private LogoutUseCase logoutUseCase;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Test
    void authTokenLifecycleWorksWithFlywayAndMySql() {
        String username = "it_user_" + System.currentTimeMillis();
        String email = username + "@example.com";
        String password = "Pass@1234";

        AuthResult registered = registerUseCase.execute(new RegisterCommand(username, email, password));
        assertEquals(username, registered.username());
        assertTrue(refreshTokenRepository.findActiveByToken(registered.refreshToken()).isPresent());

        AuthResult loginResult = loginUseCase.execute(new LoginCommand(username, password));
        String oldRefreshToken = loginResult.refreshToken();
        assertTrue(refreshTokenRepository.findActiveByToken(oldRefreshToken).isPresent());

        AuthResult rotated = refreshTokenUseCase.execute(new RefreshTokenCommand(oldRefreshToken));
        assertNotEquals(oldRefreshToken, rotated.refreshToken());
        assertTrue(refreshTokenRepository.findActiveByToken(oldRefreshToken).isEmpty());
        assertTrue(refreshTokenRepository.findActiveByToken(rotated.refreshToken()).isPresent());

        assertThrows(
                RefreshTokenReuseDetectedException.class,
                () -> refreshTokenUseCase.execute(new RefreshTokenCommand(oldRefreshToken))
        );

        // Reusing old revoked token should revoke the entire family, including the latest token.
        assertTrue(refreshTokenRepository.findActiveByToken(rotated.refreshToken()).isEmpty());

        logoutUseCase.execute(new LogoutCommand(rotated.refreshToken()));
    }
}

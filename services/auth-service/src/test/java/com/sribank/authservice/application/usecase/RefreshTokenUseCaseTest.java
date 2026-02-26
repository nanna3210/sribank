package com.sribank.authservice.application.usecase;

import com.sribank.authservice.application.command.RefreshTokenCommand;
import com.sribank.authservice.application.dto.AuthResult;
import com.sribank.authservice.domain.exception.InvalidRefreshTokenException;
import com.sribank.authservice.domain.model.AuthUser;
import com.sribank.authservice.domain.model.RefreshToken;
import com.sribank.authservice.domain.repository.AuthUserRepository;
import com.sribank.authservice.domain.repository.RefreshTokenRepository;
import com.sribank.authservice.infrastructure.security.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RefreshTokenUseCaseTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private AuthUserRepository authUserRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private AuthTokenIssueService authTokenIssueService;

    @InjectMocks
    private RefreshTokenUseCase refreshTokenUseCase;

    @Test
    void executeThrowsWhenTokenNotFound() {
        when(refreshTokenRepository.findActiveByToken("token")).thenReturn(Optional.empty());

        assertThrows(InvalidRefreshTokenException.class,
                () -> refreshTokenUseCase.execute(new RefreshTokenCommand("token")));

        verify(refreshTokenRepository, never()).revokeByToken("token");
    }

    @Test
    void executeRevokesAndThrowsWhenTokenExpired() {
        RefreshToken expired = RefreshToken.restore("r1", "u1", "token", Instant.now().minusSeconds(10), false);
        when(refreshTokenRepository.findActiveByToken("token")).thenReturn(Optional.of(expired));

        assertThrows(InvalidRefreshTokenException.class,
                () -> refreshTokenUseCase.execute(new RefreshTokenCommand("token")));

        verify(refreshTokenRepository).revokeByToken("token");
    }

    @Test
    void executeRevokesAndThrowsWhenSubjectMismatch() {
        RefreshToken active = RefreshToken.restore("r1", "u1", "token", Instant.now().plusSeconds(120), false);
        when(refreshTokenRepository.findActiveByToken("token")).thenReturn(Optional.of(active));
        when(jwtTokenProvider.extractSubject("token")).thenReturn("u2");

        assertThrows(InvalidRefreshTokenException.class,
                () -> refreshTokenUseCase.execute(new RefreshTokenCommand("token")));

        verify(refreshTokenRepository).revokeByToken("token");
    }

    @Test
    void executeRevokesAndIssuesNewTokensWhenRefreshIsValid() {
        RefreshToken active = RefreshToken.restore("r1", "u1", "token", Instant.now().plusSeconds(120), false);
        AuthUser user = AuthUser.restore("u1", "sai", "sai@example.com", "hash", true, Instant.now());
        AuthResult expected = new AuthResult("u1", "sai", "access2", "refresh2");

        when(refreshTokenRepository.findActiveByToken("token")).thenReturn(Optional.of(active));
        when(jwtTokenProvider.extractSubject("token")).thenReturn("u1");
        when(authUserRepository.findById("u1")).thenReturn(Optional.of(user));
        when(authTokenIssueService.issueForUser(user)).thenReturn(expected);

        AuthResult result = refreshTokenUseCase.execute(new RefreshTokenCommand("token"));

        assertEquals("u1", result.userId());
        assertEquals("access2", result.accessToken());
        verify(refreshTokenRepository).revokeByToken("token");
        verify(authTokenIssueService).issueForUser(user);
    }
}

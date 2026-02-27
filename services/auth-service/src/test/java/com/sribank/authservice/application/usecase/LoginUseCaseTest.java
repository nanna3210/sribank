package com.sribank.authservice.application.usecase;

import com.sribank.authservice.application.command.LoginCommand;
import com.sribank.authservice.application.dto.AuthResult;
import com.sribank.authservice.domain.exception.InvalidCredentialsException;
import com.sribank.authservice.domain.exception.LoginTemporarilyBlockedException;
import com.sribank.authservice.domain.model.AuthUser;
import com.sribank.authservice.domain.model.LoginAttempt;
import com.sribank.authservice.domain.repository.AuthUserRepository;
import com.sribank.authservice.domain.repository.LoginAttemptRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginUseCaseTest {

    @Mock
    private AuthUserRepository authUserRepository;

    @Mock
    private LoginAttemptRepository loginAttemptRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthTokenIssueService authTokenIssueService;

    private LoginUseCase loginUseCase;

    @BeforeEach
    void setUp() {
        loginUseCase = new LoginUseCase(
                authUserRepository,
                loginAttemptRepository,
                passwordEncoder,
                authTokenIssueService,
                5,
                15,
                15
        );
    }

    @Test
    void executeThrowsWhenUserNotFoundAndSavesFailureAttempt() {
        LoginCommand command = new LoginCommand("sai", "Pass@1234");
        when(loginAttemptRepository.countFailedAttemptsSince(any(), any())).thenReturn(0L);
        when(authUserRepository.findByUsername("sai")).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class, () -> loginUseCase.execute(command));

        ArgumentCaptor<LoginAttempt> attemptCaptor = ArgumentCaptor.forClass(LoginAttempt.class);
        verify(loginAttemptRepository).save(attemptCaptor.capture());
        assertEquals("sai", attemptCaptor.getValue().getUsername());
        assertEquals("USER_NOT_FOUND", attemptCaptor.getValue().getFailureReason());
        verify(authTokenIssueService, never()).issueForUser(any());
    }

    @Test
    void executeThrowsWhenPasswordMismatchAndSavesFailureAttempt() {
        LoginCommand command = new LoginCommand("sai", "wrong");
        AuthUser user = AuthUser.restore("u1", "sai", "sai@example.com", "hash", true, Instant.now());

        when(loginAttemptRepository.countFailedAttemptsSince(any(), any())).thenReturn(0L);
        when(authUserRepository.findByUsername("sai")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "hash")).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> loginUseCase.execute(command));

        ArgumentCaptor<LoginAttempt> attemptCaptor = ArgumentCaptor.forClass(LoginAttempt.class);
        verify(loginAttemptRepository).save(attemptCaptor.capture());
        assertEquals("PASSWORD_MISMATCH", attemptCaptor.getValue().getFailureReason());
        verify(authTokenIssueService, never()).issueForUser(any());
    }

    @Test
    void executeReturnsAuthResultWhenCredentialsAreValid() {
        LoginCommand command = new LoginCommand("sai", "Pass@1234");
        AuthUser user = AuthUser.restore("u1", "sai", "sai@example.com", "hash", true, Instant.now());
        AuthResult expected = new AuthResult("u1", "sai", "access", "refresh");

        when(loginAttemptRepository.countFailedAttemptsSince(any(), any())).thenReturn(0L);
        when(authUserRepository.findByUsername("sai")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Pass@1234", "hash")).thenReturn(true);
        when(authTokenIssueService.issueForUser(user)).thenReturn(expected);

        AuthResult result = loginUseCase.execute(command);

        assertEquals("u1", result.userId());
        assertEquals("access", result.accessToken());

        ArgumentCaptor<LoginAttempt> attemptCaptor = ArgumentCaptor.forClass(LoginAttempt.class);
        verify(loginAttemptRepository).save(attemptCaptor.capture());
        assertEquals("sai", attemptCaptor.getValue().getUsername());
        assertEquals(true, attemptCaptor.getValue().isSuccess());
        assertEquals(null, attemptCaptor.getValue().getFailureReason());
        verify(authTokenIssueService).issueForUser(user);
    }

    @Test
    void executeThrowsWhenUserIsTemporarilyBlocked() {
        LoginCommand command = new LoginCommand("sai", "Pass@1234");
        when(loginAttemptRepository.countFailedAttemptsSince(any(), any())).thenReturn(5L);
        when(loginAttemptRepository.findLatestFailedAttemptTime("sai")).thenReturn(Optional.of(Instant.now()));

        assertThrows(LoginTemporarilyBlockedException.class, () -> loginUseCase.execute(command));

        verify(authUserRepository, never()).findByUsername(any());
        verify(loginAttemptRepository, never()).save(any());
    }
}

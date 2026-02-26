package com.sribank.authservice.application.usecase;

import com.sribank.authservice.application.command.RegisterCommand;
import com.sribank.authservice.application.dto.AuthResult;
import com.sribank.authservice.domain.exception.UserAlreadyExistsException;
import com.sribank.authservice.domain.model.AuthUser;
import com.sribank.authservice.domain.repository.AuthUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegisterUseCaseTest {

    @Mock
    private AuthUserRepository authUserRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthTokenIssueService authTokenIssueService;

    @InjectMocks
    private RegisterUseCase registerUseCase;

    @Test
    void executeThrowsWhenUserAlreadyExists() {
        RegisterCommand command = new RegisterCommand("sai", "sai@example.com", "Pass@1234");
        when(authUserRepository.existsByUsernameOrEmail("sai", "sai@example.com")).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> registerUseCase.execute(command));

        verify(authUserRepository, never()).save(any());
        verify(authTokenIssueService, never()).issueForUser(any());
    }

    @Test
    void executeSavesUserAssignsDefaultRoleAndIssuesTokens() {
        RegisterCommand command = new RegisterCommand("sai", "sai@example.com", "Pass@1234");
        AuthUser savedUser = AuthUser.restore("user-1", "sai", "sai@example.com", "hashed", true, java.time.Instant.now());
        AuthResult expected = new AuthResult("user-1", "sai", "access", "refresh");

        when(authUserRepository.existsByUsernameOrEmail("sai", "sai@example.com")).thenReturn(false);
        when(passwordEncoder.encode("Pass@1234")).thenReturn("hashed");
        when(authUserRepository.save(any(AuthUser.class))).thenReturn(savedUser);
        when(authTokenIssueService.issueForUser(savedUser)).thenReturn(expected);

        AuthResult result = registerUseCase.execute(command);

        assertEquals("user-1", result.userId());
        assertEquals("access", result.accessToken());

        ArgumentCaptor<AuthUser> userCaptor = ArgumentCaptor.forClass(AuthUser.class);
        verify(authUserRepository).save(userCaptor.capture());
        assertEquals("sai", userCaptor.getValue().getUsername());
        assertEquals("sai@example.com", userCaptor.getValue().getEmail());
        assertEquals("hashed", userCaptor.getValue().getPasswordHash());

        verify(authUserRepository).assignRole("user-1", "USER");
        verify(authTokenIssueService).issueForUser(savedUser);
    }
}

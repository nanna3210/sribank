package com.sribank.authservice.application.usecase;

import com.sribank.authservice.application.command.LoginCommand;
import com.sribank.authservice.application.dto.AuthResult;
import com.sribank.authservice.domain.exception.InvalidCredentialsException;
import com.sribank.authservice.domain.exception.LoginTemporarilyBlockedException;
import com.sribank.authservice.domain.model.AuthUser;
import com.sribank.authservice.domain.model.LoginAttempt;
import com.sribank.authservice.domain.repository.AuthUserRepository;
import com.sribank.authservice.domain.repository.LoginAttemptRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
public class LoginUseCase {

    private final AuthUserRepository authUserRepository;
    private final LoginAttemptRepository loginAttemptRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthTokenIssueService authTokenIssueService;
    private final int maxFailedAttempts;
    private final Duration failedAttemptWindow;
    private final Duration lockDuration;

    public LoginUseCase(AuthUserRepository authUserRepository,
                        LoginAttemptRepository loginAttemptRepository,
                        PasswordEncoder passwordEncoder,
                        AuthTokenIssueService authTokenIssueService,
                        @Value("${security.login-protection.max-failed-attempts:5}") int maxFailedAttempts,
                        @Value("${security.login-protection.attempt-window-minutes:15}") long attemptWindowMinutes,
                        @Value("${security.login-protection.lock-duration-minutes:15}") long lockDurationMinutes) {
        this.authUserRepository = authUserRepository;
        this.loginAttemptRepository = loginAttemptRepository;
        this.passwordEncoder = passwordEncoder;
        this.authTokenIssueService = authTokenIssueService;
        this.maxFailedAttempts = maxFailedAttempts;
        this.failedAttemptWindow = Duration.ofMinutes(attemptWindowMinutes);
        this.lockDuration = Duration.ofMinutes(lockDurationMinutes);
    }

    public AuthResult execute(LoginCommand command) {
        ensureNotTemporarilyBlocked(command.username());

        AuthUser user = authUserRepository.findByUsername(command.username())
                .orElseThrow(() -> {
                    loginAttemptRepository.save(LoginAttempt.failure(command.username(), "USER_NOT_FOUND"));
                    return new InvalidCredentialsException("Invalid username or password");
                });

        if (!passwordEncoder.matches(command.password(), user.getPasswordHash())) {
            loginAttemptRepository.save(LoginAttempt.failure(command.username(), "PASSWORD_MISMATCH"));
            ensureNotTemporarilyBlocked(command.username());
            throw new InvalidCredentialsException("Invalid username or password");
        }

        loginAttemptRepository.save(LoginAttempt.success(command.username()));
        return authTokenIssueService.issueForUser(user);
    }

    private void ensureNotTemporarilyBlocked(String username) {
        Instant now = Instant.now();
        Instant windowStart = now.minus(failedAttemptWindow);
        long failedAttempts = loginAttemptRepository.countFailedAttemptsSince(username, windowStart);
        if (failedAttempts < maxFailedAttempts) {
            return;
        }

        Instant latestFailedAttempt = loginAttemptRepository.findLatestFailedAttemptTime(username).orElse(now);
        Instant unlockAt = latestFailedAttempt.plus(lockDuration);
        if (unlockAt.isAfter(now)) {
            long retryAfterMinutes = Math.max(1, Duration.between(now, unlockAt).toMinutes());
            throw new LoginTemporarilyBlockedException(
                    "Too many failed login attempts. Retry after " + retryAfterMinutes + " minute(s)");
        }
    }
}

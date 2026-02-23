package com.sribank.authservice.application.usecase;

import com.sribank.authservice.application.command.LoginCommand;
import com.sribank.authservice.application.dto.AuthResult;
import com.sribank.authservice.domain.exception.InvalidCredentialsException;
import com.sribank.authservice.domain.model.AuthUser;
import com.sribank.authservice.domain.model.LoginAttempt;
import com.sribank.authservice.domain.repository.AuthUserRepository;
import com.sribank.authservice.domain.repository.LoginAttemptRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class LoginUseCase {

    private final AuthUserRepository authUserRepository;
    private final LoginAttemptRepository loginAttemptRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthTokenIssueService authTokenIssueService;

    public LoginUseCase(AuthUserRepository authUserRepository,
                        LoginAttemptRepository loginAttemptRepository,
                        PasswordEncoder passwordEncoder,
                        AuthTokenIssueService authTokenIssueService) {
        this.authUserRepository = authUserRepository;
        this.loginAttemptRepository = loginAttemptRepository;
        this.passwordEncoder = passwordEncoder;
        this.authTokenIssueService = authTokenIssueService;
    }

    public AuthResult execute(LoginCommand command) {
        AuthUser user = authUserRepository.findByUsername(command.username())
                .orElseThrow(() -> {
                    loginAttemptRepository.save(LoginAttempt.failure(command.username(), "USER_NOT_FOUND"));
                    return new InvalidCredentialsException("Invalid username or password");
                });

        if (!passwordEncoder.matches(command.password(), user.getPasswordHash())) {
            loginAttemptRepository.save(LoginAttempt.failure(command.username(), "PASSWORD_MISMATCH"));
            throw new InvalidCredentialsException("Invalid username or password");
        }

        loginAttemptRepository.save(LoginAttempt.success(command.username()));
        return authTokenIssueService.issueForUser(user);
    }
}

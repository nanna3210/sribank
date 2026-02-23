package com.sribank.authservice.application.usecase;

import com.sribank.authservice.application.command.RegisterCommand;
import com.sribank.authservice.application.dto.AuthResult;
import com.sribank.authservice.domain.exception.UserAlreadyExistsException;
import com.sribank.authservice.domain.model.AuthUser;
import com.sribank.authservice.domain.repository.AuthUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegisterUseCase {

    private final AuthUserRepository authUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthTokenIssueService authTokenIssueService;

    public RegisterUseCase(AuthUserRepository authUserRepository,
                           PasswordEncoder passwordEncoder,
                           AuthTokenIssueService authTokenIssueService) {
        this.authUserRepository = authUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.authTokenIssueService = authTokenIssueService;
    }

    public AuthResult execute(RegisterCommand command) {
        if (authUserRepository.existsByUsernameOrEmail(command.username(), command.email())) {
            throw new UserAlreadyExistsException("Username or email already exists");
        }

        String passwordHash = passwordEncoder.encode(command.password());
        AuthUser savedUser = authUserRepository.save(AuthUser.create(command.username(), command.email(), passwordHash));

        return authTokenIssueService.issueForUser(savedUser);
    }
}

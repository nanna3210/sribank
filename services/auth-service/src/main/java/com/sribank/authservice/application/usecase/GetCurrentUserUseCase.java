package com.sribank.authservice.application.usecase;

import com.sribank.authservice.application.dto.CurrentUserResult;
import com.sribank.authservice.domain.exception.InvalidCredentialsException;
import com.sribank.authservice.domain.model.AuthUser;
import com.sribank.authservice.domain.repository.AuthUserRepository;
import org.springframework.stereotype.Service;

@Service
public class GetCurrentUserUseCase {

    private final AuthUserRepository authUserRepository;

    public GetCurrentUserUseCase(AuthUserRepository authUserRepository) {
        this.authUserRepository = authUserRepository;
    }

    public CurrentUserResult execute(String userId) {
        AuthUser user = authUserRepository.findById(userId)
                .orElseThrow(() -> new InvalidCredentialsException("Authenticated user not found"));

        return new CurrentUserResult(
                user.getId(),
                user.getUsername(),
                user.getEmail()
        );
    }
}

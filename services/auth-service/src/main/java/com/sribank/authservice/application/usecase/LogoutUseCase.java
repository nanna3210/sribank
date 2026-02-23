package com.sribank.authservice.application.usecase;

import com.sribank.authservice.application.command.LogoutCommand;
import com.sribank.authservice.domain.repository.RefreshTokenRepository;
import org.springframework.stereotype.Service;

@Service
public class LogoutUseCase {

    private final RefreshTokenRepository refreshTokenRepository;

    public LogoutUseCase(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public void execute(LogoutCommand command) {
        refreshTokenRepository.revokeByToken(command.refreshToken());
    }
}

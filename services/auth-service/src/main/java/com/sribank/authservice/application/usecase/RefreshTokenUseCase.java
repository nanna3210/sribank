package com.sribank.authservice.application.usecase;

import com.sribank.authservice.application.command.RefreshTokenCommand;
import com.sribank.authservice.application.dto.AuthResult;
import com.sribank.authservice.domain.exception.InvalidRefreshTokenException;
import com.sribank.authservice.domain.model.AuthUser;
import com.sribank.authservice.domain.model.RefreshToken;
import com.sribank.authservice.domain.repository.AuthUserRepository;
import com.sribank.authservice.domain.repository.RefreshTokenRepository;
import com.sribank.authservice.infrastructure.security.JwtTokenProvider;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class RefreshTokenUseCase {

    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthUserRepository authUserRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthTokenIssueService authTokenIssueService;

    public RefreshTokenUseCase(RefreshTokenRepository refreshTokenRepository,
                               AuthUserRepository authUserRepository,
                               JwtTokenProvider jwtTokenProvider,
                               AuthTokenIssueService authTokenIssueService) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.authUserRepository = authUserRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authTokenIssueService = authTokenIssueService;
    }

    public AuthResult execute(RefreshTokenCommand command) {
        String token = command.refreshToken();
        RefreshToken storedToken = refreshTokenRepository.findActiveByToken(token)
                .orElseThrow(() -> new InvalidRefreshTokenException("Refresh token is invalid or revoked"));

        if (storedToken.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.revokeByToken(token);
            throw new InvalidRefreshTokenException("Refresh token has expired");
        }

        String userIdFromToken = jwtTokenProvider.extractSubject(token);
        if (!storedToken.getUserId().equals(userIdFromToken)) {
            refreshTokenRepository.revokeByToken(token);
            throw new InvalidRefreshTokenException("Refresh token subject mismatch");
        }

        AuthUser user = authUserRepository.findById(storedToken.getUserId())
                .orElseThrow(() -> new InvalidRefreshTokenException("User not found for refresh token"));

        refreshTokenRepository.revokeByToken(token);
        return authTokenIssueService.issueForUser(user);
    }
}

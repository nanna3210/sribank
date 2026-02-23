package com.sribank.authservice.application.usecase;

import com.sribank.authservice.application.dto.AuthResult;
import com.sribank.authservice.domain.model.AuthUser;
import com.sribank.authservice.domain.model.RefreshToken;
import com.sribank.authservice.domain.repository.RefreshTokenRepository;
import com.sribank.authservice.infrastructure.security.JwtTokenProvider;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class AuthTokenIssueService {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    public AuthTokenIssueService(JwtTokenProvider jwtTokenProvider, RefreshTokenRepository refreshTokenRepository) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public AuthResult issueForUser(AuthUser user) {
        String accessToken = jwtTokenProvider.createAccessToken(user.getId());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());
        Instant expiry = jwtTokenProvider.extractExpiration(refreshToken).toInstant();

        refreshTokenRepository.save(RefreshToken.issue(user.getId(), refreshToken, expiry));

        return new AuthResult(
                user.getId(),
                user.getUsername(),
                accessToken,
                refreshToken
        );
    }
}

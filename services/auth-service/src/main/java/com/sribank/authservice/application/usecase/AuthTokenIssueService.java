package com.sribank.authservice.application.usecase;

import com.sribank.authservice.application.dto.AuthResult;
import com.sribank.authservice.domain.model.AuthUser;
import com.sribank.authservice.domain.model.RefreshToken;
import com.sribank.authservice.domain.repository.AuthUserRepository;
import com.sribank.authservice.domain.repository.RefreshTokenRepository;
import com.sribank.authservice.infrastructure.security.JwtTokenProvider;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class AuthTokenIssueService {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthUserRepository authUserRepository;

    public AuthTokenIssueService(JwtTokenProvider jwtTokenProvider,
                                 RefreshTokenRepository refreshTokenRepository,
                                 AuthUserRepository authUserRepository) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.refreshTokenRepository = refreshTokenRepository;
        this.authUserRepository = authUserRepository;
    }

    public AuthResult issueForUser(AuthUser user) {
        List<String> roleCodes = authUserRepository.findRoleCodesByUserId(user.getId());
        String accessToken = jwtTokenProvider.createAccessToken(user.getId(), roleCodes);
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

package com.sribank.authservice.interfaces.rest.response;

public record AuthResponse(
        String userId,
        String username,
        String accessToken,
        String refreshToken
) {
}

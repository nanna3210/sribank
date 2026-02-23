package com.sribank.authservice.application.dto;

public record AuthResult(String userId, String username, String accessToken, String refreshToken) {
}

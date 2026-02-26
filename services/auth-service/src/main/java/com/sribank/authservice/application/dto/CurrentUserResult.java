package com.sribank.authservice.application.dto;

public record CurrentUserResult(
        String userId,
        String username,
        String email
) {
}

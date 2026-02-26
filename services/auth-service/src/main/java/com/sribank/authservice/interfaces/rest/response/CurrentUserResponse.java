package com.sribank.authservice.interfaces.rest.response;

public record CurrentUserResponse(
        String userId,
        String username,
        String email
) {
}

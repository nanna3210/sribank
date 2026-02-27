package com.sribank.userservice.interfaces.rest.response;

public record UserProfileResponse(
        String userId,
        String firstName,
        String lastName,
        String phoneNumber
) {
}

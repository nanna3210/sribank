package com.sribank.userservice.application.dto;

public record UserProfileResult(
        String userId,
        String firstName,
        String lastName,
        String phoneNumber
) {
}

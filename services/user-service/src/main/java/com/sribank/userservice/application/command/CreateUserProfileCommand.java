package com.sribank.userservice.application.command;

public record CreateUserProfileCommand(
        String userId,
        String firstName,
        String lastName,
        String phoneNumber
) {
}

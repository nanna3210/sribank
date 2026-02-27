package com.sribank.userservice.interfaces.rest.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateUserProfileRequest(
        @NotBlank @Size(min = 3, max = 36) String userId,
        @NotBlank @Size(min = 1, max = 100) String firstName,
        @NotBlank @Size(min = 1, max = 100) String lastName,
        @Pattern(regexp = "^[0-9+ -]{7,20}$", message = "must be a valid phone number format")
        String phoneNumber
) {
}

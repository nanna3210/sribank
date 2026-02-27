package com.sribank.userservice.interfaces.rest.response;

import java.time.Instant;

public record ApiErrorResponse(
        String code,
        String message,
        Instant timestamp
) {
}

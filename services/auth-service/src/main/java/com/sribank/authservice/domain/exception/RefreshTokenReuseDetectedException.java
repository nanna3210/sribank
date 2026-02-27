package com.sribank.authservice.domain.exception;

public class RefreshTokenReuseDetectedException extends RuntimeException {

    public RefreshTokenReuseDetectedException(String message) {
        super(message);
    }
}

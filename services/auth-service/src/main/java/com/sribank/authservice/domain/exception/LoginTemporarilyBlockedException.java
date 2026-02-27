package com.sribank.authservice.domain.exception;

public class LoginTemporarilyBlockedException extends RuntimeException {

    public LoginTemporarilyBlockedException(String message) {
        super(message);
    }
}

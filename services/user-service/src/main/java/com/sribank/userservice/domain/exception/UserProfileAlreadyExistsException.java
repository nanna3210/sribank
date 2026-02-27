package com.sribank.userservice.domain.exception;

public class UserProfileAlreadyExistsException extends RuntimeException {

    public UserProfileAlreadyExistsException(String message) {
        super(message);
    }
}

package com.sribank.userservice.interfaces.rest;

import com.sribank.userservice.domain.exception.UserProfileAlreadyExistsException;
import com.sribank.userservice.domain.exception.UserProfileNotFoundException;
import com.sribank.userservice.interfaces.rest.response.ApiErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserProfileAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiErrorResponse handleAlreadyExists(UserProfileAlreadyExistsException ex) {
        return new ApiErrorResponse("USER_PROFILE_EXISTS", ex.getMessage(), Instant.now());
    }

    @ExceptionHandler(UserProfileNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiErrorResponse handleNotFound(UserProfileNotFoundException ex) {
        return new ApiErrorResponse("USER_PROFILE_NOT_FOUND", ex.getMessage(), Instant.now());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(err -> err.getField() + " " + err.getDefaultMessage())
                .orElse("Validation failed");
        return new ApiErrorResponse("USER_VALIDATION_ERROR", message, Instant.now());
    }
}

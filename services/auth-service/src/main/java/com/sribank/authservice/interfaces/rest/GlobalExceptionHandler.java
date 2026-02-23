package com.sribank.authservice.interfaces.rest;

import com.sribank.authservice.domain.exception.InvalidCredentialsException;
import com.sribank.authservice.domain.exception.InvalidRefreshTokenException;
import com.sribank.authservice.domain.exception.UserAlreadyExistsException;
import com.sribank.authservice.interfaces.rest.response.ApiErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiErrorResponse handleUserAlreadyExists(UserAlreadyExistsException ex) {
        return new ApiErrorResponse("AUTH_USER_EXISTS", ex.getMessage(), Instant.now());
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiErrorResponse handleInvalidCredentials(InvalidCredentialsException ex) {
        return new ApiErrorResponse("AUTH_INVALID_CREDENTIALS", ex.getMessage(), Instant.now());
    }

    @ExceptionHandler({InvalidRefreshTokenException.class, IllegalArgumentException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiErrorResponse handleInvalidRefreshToken(RuntimeException ex) {
        return new ApiErrorResponse("AUTH_INVALID_REFRESH_TOKEN", ex.getMessage(), Instant.now());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(err -> err.getField() + " " + err.getDefaultMessage())
                .orElse("Validation failed");

        return new ApiErrorResponse("AUTH_VALIDATION_ERROR", message, Instant.now());
    }
}

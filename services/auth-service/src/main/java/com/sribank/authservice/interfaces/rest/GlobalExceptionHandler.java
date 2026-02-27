package com.sribank.authservice.interfaces.rest;

import com.sribank.authservice.domain.exception.InvalidCredentialsException;
import com.sribank.authservice.domain.exception.InvalidRefreshTokenException;
import com.sribank.authservice.domain.exception.LoginTemporarilyBlockedException;
import com.sribank.authservice.domain.exception.UserAlreadyExistsException;
import com.sribank.authservice.interfaces.rest.response.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiErrorResponse handleUserAlreadyExists(UserAlreadyExistsException ex, HttpServletRequest request) {
        log.warn("api_error code=AUTH_USER_EXISTS path={} message={}", request.getRequestURI(), ex.getMessage());
        return new ApiErrorResponse("AUTH_USER_EXISTS", ex.getMessage(), Instant.now());
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiErrorResponse handleInvalidCredentials(InvalidCredentialsException ex, HttpServletRequest request) {
        log.warn("api_error code=AUTH_INVALID_CREDENTIALS path={} message={}", request.getRequestURI(), ex.getMessage());
        return new ApiErrorResponse("AUTH_INVALID_CREDENTIALS", ex.getMessage(), Instant.now());
    }

    @ExceptionHandler(LoginTemporarilyBlockedException.class)
    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    public ApiErrorResponse handleLoginTemporarilyBlocked(LoginTemporarilyBlockedException ex, HttpServletRequest request) {
        log.warn("api_error code=AUTH_LOGIN_TEMP_BLOCKED path={} message={}", request.getRequestURI(), ex.getMessage());
        return new ApiErrorResponse("AUTH_LOGIN_TEMP_BLOCKED", ex.getMessage(), Instant.now());
    }

    @ExceptionHandler({InvalidRefreshTokenException.class, IllegalArgumentException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiErrorResponse handleInvalidRefreshToken(RuntimeException ex, HttpServletRequest request) {
        log.warn("api_error code=AUTH_INVALID_REFRESH_TOKEN path={} message={}", request.getRequestURI(), ex.getMessage());
        return new ApiErrorResponse("AUTH_INVALID_REFRESH_TOKEN", ex.getMessage(), Instant.now());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(err -> err.getField() + " " + err.getDefaultMessage())
                .orElse("Validation failed");

        log.warn("api_error code=AUTH_VALIDATION_ERROR path={} message={}", request.getRequestURI(), message);
        return new ApiErrorResponse("AUTH_VALIDATION_ERROR", message, Instant.now());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiErrorResponse handleUnexpected(Exception ex, HttpServletRequest request) {
        log.error("api_error code=AUTH_INTERNAL_ERROR path={}", request.getRequestURI(), ex);
        return new ApiErrorResponse("AUTH_INTERNAL_ERROR", "Internal server error", Instant.now());
    }
}

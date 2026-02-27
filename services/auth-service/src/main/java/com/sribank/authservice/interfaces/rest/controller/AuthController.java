package com.sribank.authservice.interfaces.rest.controller;

import com.sribank.authservice.application.command.LoginCommand;
import com.sribank.authservice.application.command.LogoutCommand;
import com.sribank.authservice.application.command.RefreshTokenCommand;
import com.sribank.authservice.application.command.RegisterCommand;
import com.sribank.authservice.application.dto.AuthResult;
import com.sribank.authservice.application.dto.CurrentUserResult;
import com.sribank.authservice.application.usecase.GetCurrentUserUseCase;
import com.sribank.authservice.application.usecase.LoginUseCase;
import com.sribank.authservice.application.usecase.LogoutUseCase;
import com.sribank.authservice.application.usecase.RefreshTokenUseCase;
import com.sribank.authservice.application.usecase.RegisterUseCase;
import com.sribank.authservice.interfaces.rest.request.LoginRequest;
import com.sribank.authservice.interfaces.rest.request.LogoutRequest;
import com.sribank.authservice.interfaces.rest.request.RefreshTokenRequest;
import com.sribank.authservice.interfaces.rest.request.RegisterRequest;
import com.sribank.authservice.interfaces.rest.response.AuthResponse;
import com.sribank.authservice.interfaces.rest.response.ApiErrorResponse;
import com.sribank.authservice.interfaces.rest.response.CurrentUserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Register, login, token, and authz endpoints")
public class AuthController {

    private final RegisterUseCase registerUseCase;
    private final LoginUseCase loginUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;
    private final LogoutUseCase logoutUseCase;
    private final GetCurrentUserUseCase getCurrentUserUseCase;

    public AuthController(RegisterUseCase registerUseCase,
                          LoginUseCase loginUseCase,
                          RefreshTokenUseCase refreshTokenUseCase,
                          LogoutUseCase logoutUseCase,
                          GetCurrentUserUseCase getCurrentUserUseCase) {
        this.registerUseCase = registerUseCase;
        this.loginUseCase = loginUseCase;
        this.refreshTokenUseCase = refreshTokenUseCase;
        this.logoutUseCase = logoutUseCase;
        this.getCurrentUserUseCase = getCurrentUserUseCase;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register a new user")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Registered", content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "User already exists", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        AuthResult result = registerUseCase.execute(new RegisterCommand(
                request.username(),
                request.email(),
                request.password()
        ));

        return toResponse(result);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Login with username and password")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Authenticated", content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "429", description = "Temporarily blocked due to repeated failures", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        AuthResult result = loginUseCase.execute(new LoginCommand(
                request.username(),
                request.password()
        ));

        return toResponse(result);
    }

    @PostMapping("/refresh")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Refresh access/refresh token pair")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tokens rotated", content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid refresh token or replay detected", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public AuthResponse refresh(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResult result = refreshTokenUseCase.execute(new RefreshTokenCommand(request.refreshToken()));
        return toResponse(result);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Logout by revoking refresh token")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Logged out"),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public void logout(@Valid @RequestBody LogoutRequest request) {
        logoutUseCase.execute(new LogoutCommand(request.refreshToken()));
    }

    @GetMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get current authenticated user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Current user", content = @Content(schema = @Schema(implementation = CurrentUserResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public CurrentUserResponse me(Authentication authentication) {
        CurrentUserResult result = getCurrentUserUseCase.execute(String.valueOf(authentication.getPrincipal()));
        return new CurrentUserResponse(result.userId(), result.username(), result.email());
    }

    @GetMapping("/admin/ping")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Admin-only ping endpoint")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Admin access confirmed",
                    content = @Content(examples = @ExampleObject(value = "{\"status\":\"OK\",\"message\":\"admin access granted\"}"))),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public Map<String, String> adminPing() {
        return Map.of("status", "OK", "message", "admin access granted");
    }

    private AuthResponse toResponse(AuthResult result) {
        return new AuthResponse(
                result.userId(),
                result.username(),
                result.accessToken(),
                result.refreshToken()
        );
    }
}

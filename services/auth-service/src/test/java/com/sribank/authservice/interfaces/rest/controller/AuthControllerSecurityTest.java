package com.sribank.authservice.interfaces.rest.controller;

import com.sribank.authservice.application.dto.CurrentUserResult;
import com.sribank.authservice.application.usecase.GetCurrentUserUseCase;
import com.sribank.authservice.application.usecase.LoginUseCase;
import com.sribank.authservice.application.usecase.LogoutUseCase;
import com.sribank.authservice.application.usecase.RefreshTokenUseCase;
import com.sribank.authservice.application.usecase.RegisterUseCase;
import com.sribank.authservice.config.SecurityConfig;
import com.sribank.authservice.infrastructure.security.JwtAuthenticationFilter;
import com.sribank.authservice.infrastructure.security.JwtTokenProvider;
import com.sribank.authservice.interfaces.rest.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, GlobalExceptionHandler.class})
class AuthControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RegisterUseCase registerUseCase;

    @MockBean
    private LoginUseCase loginUseCase;

    @MockBean
    private RefreshTokenUseCase refreshTokenUseCase;

    @MockBean
    private LogoutUseCase logoutUseCase;

    @MockBean
    private GetCurrentUserUseCase getCurrentUserUseCase;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void meEndpointReturns401WhenNoToken() throws Exception {
        mockMvc.perform(get("/api/v1/auth/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("AUTH_UNAUTHORIZED"));
    }

    @Test
    void meEndpointReturns200WhenTokenIsValid() throws Exception {
        when(jwtTokenProvider.isTokenValid("user-token")).thenReturn(true);
        when(jwtTokenProvider.extractSubject("user-token")).thenReturn("u1");
        when(jwtTokenProvider.extractRoles("user-token")).thenReturn(List.of("USER"));
        when(getCurrentUserUseCase.execute("u1"))
                .thenReturn(new CurrentUserResult("u1", "sai", "sai@example.com"));

        mockMvc.perform(get("/api/v1/auth/me")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer user-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("u1"))
                .andExpect(jsonPath("$.username").value("sai"));
    }

    @Test
    void adminEndpointReturns403ForNonAdminRole() throws Exception {
        when(jwtTokenProvider.isTokenValid("user-token")).thenReturn(true);
        when(jwtTokenProvider.extractSubject("user-token")).thenReturn("u1");
        when(jwtTokenProvider.extractRoles("user-token")).thenReturn(List.of("USER"));

        mockMvc.perform(get("/api/v1/auth/admin/ping")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer user-token"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("AUTH_FORBIDDEN"));
    }

    @Test
    void adminEndpointReturns200ForAdminRole() throws Exception {
        when(jwtTokenProvider.isTokenValid("admin-token")).thenReturn(true);
        when(jwtTokenProvider.extractSubject("admin-token")).thenReturn("admin-1");
        when(jwtTokenProvider.extractRoles("admin-token")).thenReturn(List.of("ADMIN"));

        mockMvc.perform(get("/api/v1/auth/admin/ping")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer admin-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"));
    }
}

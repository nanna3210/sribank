package com.sribank.authservice.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class AuthHttpContractIntegrationTest {

    @Container
    static final MySQLContainer<?> MYSQL = new MySQLContainer<>("mysql:8.0.36")
            .withDatabaseName("sribank_auth_http_it")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureDatasource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", MYSQL::getJdbcUrl);
        registry.add("spring.datasource.username", MYSQL::getUsername);
        registry.add("spring.datasource.password", MYSQL::getPassword);
        registry.add("spring.flyway.enabled", () -> true);
        registry.add("security.jwt.secret", () -> "test-secret-key-with-at-least-32-characters");

        // Keep lockout deterministic in tests.
        registry.add("security.login-protection.max-failed-attempts", () -> 3);
        registry.add("security.login-protection.attempt-window-minutes", () -> 15);
        registry.add("security.login-protection.lock-duration-minutes", () -> 15);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void registerLoginAndMeFlowMatchesHttpContract() throws Exception {
        String username = "http_user_" + System.currentTimeMillis();
        String email = username + "@example.com";

        String registerBody = """
                {
                  "username": "%s",
                  "email": "%s",
                  "password": "Pass@1234"
                }
                """.formatted(username, email);

        String registerResponse = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").isNotEmpty())
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode registerJson = objectMapper.readTree(registerResponse);
        String accessToken = registerJson.get("accessToken").asText();

        mockMvc.perform(get("/api/v1/auth/me")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.email").value(email));
    }

    @Test
    void adminPingReturnsForbiddenForUserToken() throws Exception {
        String username = "user_only_" + System.currentTimeMillis();
        String email = username + "@example.com";
        String registerBody = """
                {
                  "username": "%s",
                  "email": "%s",
                  "password": "Pass@1234"
                }
                """.formatted(username, email);

        String registerResponse = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String accessToken = objectMapper.readTree(registerResponse).get("accessToken").asText();

        mockMvc.perform(get("/api/v1/auth/admin/ping")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("AUTH_FORBIDDEN"));
    }

    @Test
    void loginLockoutReturns429AfterConfiguredFailedAttempts() throws Exception {
        String username = "lock_user_" + System.currentTimeMillis();
        String email = username + "@example.com";
        String registerBody = """
                {
                  "username": "%s",
                  "email": "%s",
                  "password": "Pass@1234"
                }
                """.formatted(username, email);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody))
                .andExpect(status().isCreated());

        String wrongLoginBody = """
                {
                  "username": "%s",
                  "password": "WrongPass@1234"
                }
                """.formatted(username);

        int first = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(wrongLoginBody))
                .andReturn().getResponse().getStatus();

        int second = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(wrongLoginBody))
                .andReturn().getResponse().getStatus();

        int third = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(wrongLoginBody))
                .andReturn().getResponse().getStatus();

        assertThat(first).isEqualTo(401);
        assertThat(second).isEqualTo(401);
        assertThat(third).isEqualTo(429);
    }
}

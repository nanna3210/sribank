# Changelog

All notable changes to `auth-service` are documented in this file.

## [1.0.0-auth-foundation] - 2026-02-27

### Added
- Register/login/refresh/logout flows.
- Access token auth filter and protected `/api/v1/auth/me`.
- Role-based auth foundation with `/api/v1/auth/admin/ping`.
- Login-attempt audit persistence.
- Login lockout protection (`AUTH_LOGIN_TEMP_BLOCKED`).
- Refresh-token replay defense with token-family revocation (`AUTH_REFRESH_TOKEN_REUSE_DETECTED`).
- Global exception handling with stable `AUTH_*` error codes.
- Correlation-id tracing + client IP/user-agent request logs.
- OpenAPI/Swagger docs (`/swagger-ui.html`, `/api-docs`).
- Unit tests, WebMvc security tests, and Testcontainers integration tests.
- JaCoCo quality gate and Jenkins pipeline (`sri bank/Jenkinsfile`).

### Changed
- Secrets moved to environment variables (`AUTH_DB_*`, `AUTH_JWT_SECRET`).

### Database
- Flyway migrations up to `V6`:
  - `V1` auth user
  - `V2` refresh token
  - `V3` login attempt
  - `V4` auth role
  - `V5` user-role mapping
  - `V6` refresh token family tracking

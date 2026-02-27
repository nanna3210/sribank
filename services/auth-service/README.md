# auth-service

## Run

```bash
docker compose -f ../../infra/docker/docker-compose.local.yml up -d mysql
mvn spring-boot:run
```

## Jenkins CI

Repository root contains a pipeline file:
- `sri bank/Jenkinsfile`

Pipeline behavior:
- Runs `mvn clean verify` for `services/auth-service`
- Executes tests and JaCoCo coverage check
- Fails build if line coverage is below configured threshold
- Archives JUnit + JaCoCo artifacts
- Optional SonarQube stage placeholder included

## Release Baseline

- Changelog: `sri bank/services/auth-service/CHANGELOG.md`
- Release checklist/tag guide: `sri bank/services/auth-service/RELEASE_BASELINE.md`
- Current baseline tag target: `auth-service-v1.0.0-foundation`

## Required Environment Variables

Before starting, set secrets via environment variables (do not hardcode in `application.yml`).

PowerShell example:

```powershell
$env:AUTH_DB_PASSWORD="your-db-password"
$env:AUTH_JWT_SECRET="your-very-long-secret-at-least-32-characters"
```

Optional overrides:

- `AUTH_DB_URL`
- `AUTH_DB_USERNAME`
- `AUTH_DB_PASSWORD`
- `AUTH_JWT_SECRET`

Reference template:
- `sri bank/services/auth-service/.env.example`

## Endpoints

- `POST /api/v1/auth/register`
- `POST /api/v1/auth/login`
- `POST /api/v1/auth/refresh`
- `POST /api/v1/auth/logout`
- `GET /api/v1/auth/me` (requires access token)
- `GET /api/v1/auth/admin/ping` (requires `ADMIN` role)
- `GET /api/v1/health`
- `GET /swagger-ui.html` (OpenAPI UI)
- `GET /api-docs` (OpenAPI JSON)

## Notes

- Persistence uses MySQL with Spring Data JPA.
- Flyway migrations initialize `auth_user`, `refresh_token`, `login_attempt`, `auth_role`, and `user_role` tables on startup.
- New users are auto-assigned `USER` role at registration.
- Every request carries `X-Correlation-Id` (generated if absent) and logs include `corr:<id>`.
- Request summary logs also include `clientIp` and `userAgent`.
- Login endpoint has temporary lockout protection after repeated failed attempts.
- Health probes are enabled (`/actuator/health/liveness`, `/actuator/health/readiness`).

## Error Contract

All error responses follow:

```json
{
  "code": "AUTH_*",
  "message": "human-readable message",
  "timestamp": "2026-02-27T10:00:00Z"
}
```

Current auth error codes include:
- `AUTH_USER_EXISTS`
- `AUTH_INVALID_CREDENTIALS`
- `AUTH_INVALID_REFRESH_TOKEN`
- `AUTH_REFRESH_TOKEN_REUSE_DETECTED`
- `AUTH_VALIDATION_ERROR`
- `AUTH_UNAUTHORIZED`
- `AUTH_FORBIDDEN`
- `AUTH_LOGIN_TEMP_BLOCKED`
- `AUTH_INTERNAL_ERROR`

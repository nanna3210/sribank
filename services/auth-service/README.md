# auth-service

## Run

```bash
docker compose -f ../../infra/docker/docker-compose.local.yml up -d mysql
mvn spring-boot:run
```

## Endpoints

- `POST /api/v1/auth/register`
- `POST /api/v1/auth/login`
- `POST /api/v1/auth/refresh`
- `POST /api/v1/auth/logout`
- `GET /api/v1/auth/me` (requires access token)
- `GET /api/v1/auth/admin/ping` (requires `ADMIN` role)
- `GET /api/v1/health`

## Notes

- Persistence uses MySQL with Spring Data JPA.
- Flyway migrations initialize `auth_user`, `refresh_token`, `login_attempt`, `auth_role`, and `user_role` tables on startup.
- New users are auto-assigned `USER` role at registration.

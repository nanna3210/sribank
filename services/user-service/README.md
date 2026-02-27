# user-service

## Run

```bash
docker compose -f ../../infra/docker/docker-compose.local.yml up -d mysql
mvn spring-boot:run
```

## Required Environment Variables

PowerShell example:

```powershell
$env:USER_DB_PASSWORD="your-db-password"
```

Optional overrides:
- `USER_DB_URL`
- `USER_DB_USERNAME`
- `USER_DB_PASSWORD`

Reference template:
- `sri bank/services/user-service/.env.example`

## Endpoints (Step 1)

- `GET /api/v1/health`
- `POST /api/v1/users/profiles`
- `GET /api/v1/users/profiles/{userId}`

## Notes

- Persistence uses MySQL with Spring Data JPA.
- Flyway initializes `user_profile` table at startup.
- This is Step 1 foundation for user profile lifecycle.

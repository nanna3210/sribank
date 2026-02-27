# Auth Service Release Baseline

## Baseline Identifier

- Release name: `auth-service-v1.0.0-foundation`
- Changelog version: `1.0.0-auth-foundation`

## Pre-Release Checklist

1. Environment variables configured (no hardcoded secrets):
   - `AUTH_DB_PASSWORD`
   - `AUTH_JWT_SECRET`
2. Database migration compatibility verified (V1..V6).
3. Build quality gate passes:
   - `mvn clean verify`
4. Integration tests pass (Testcontainers available on CI agent).
5. OpenAPI docs accessible:
   - `/swagger-ui.html`
   - `/api-docs`
6. Postman collection smoke flow passes.
7. Jenkins pipeline green with JaCoCo gate.

## Suggested Release Commands

From repo root (`sri bank`):

```powershell
git add services/auth-service
git commit -m "auth-service: release baseline v1.0.0 foundation"
git tag -a auth-service-v1.0.0-foundation -m "Auth service foundation baseline"
git push origin main --tags
```

## Rollback Guidance

1. Checkout previous stable tag.
2. Redeploy service image/build from that tag.
3. Do not roll back Flyway migrations destructively; use forward-fix migration if schema issue exists.

# Uber Clone Spring Boot

Backend for a ride-sharing application built with Spring Boot 3.3.1, Java 22, PostgreSQL/PostGIS, Redis-backed caching, JWT authentication, and Swagger/OpenAPI.

## Current State

- This repository is still under active development and contains both legacy work and recent stabilization changes.
- The latest hardening work was continued on `codex/final-build-stabilization` from the existing `feature/production-ready` branch.
- Verified locally on 2026-04-05:
  - `cd ubercloneapp && ./mvnw test`
  - `cd ubercloneapp && ./mvnw spring-boot:run`
- `feature/production-ready` should be treated as an integration or release-candidate branch, not as the long-lived production branch.

## What Is Implemented

- Rider, driver, auth, wallet, payment, rating, and ride lifecycle flows
- JWT-based login and stateless Spring Security
- Strategy-based fare calculation and driver matching
- PostGIS-backed geospatial queries for nearby driver selection
- OSRM distance integration for fare calculation
- Admin analytics, pending driver approval, and revenue reporting
- Actuator health indicators and Redis cache support
- Unit, strategy, service, and controller integration tests

## Repository Layout

- `ubercloneapp/` - Maven project root and application source
- `docs/` - implementation notes and release workflow documentation
- `.claude/` - local agent orchestration commands, hook docs, and helper scripts

## Prerequisites

- Java 22
- PostgreSQL with PostGIS available
- Local runtime database: `postgres`
- Local test database: `testpostgres`
- Database user: `postgres`
- Database password: `user`
- Optional Redis instance on `localhost:6379`

The application and tests attempt `CREATE EXTENSION IF NOT EXISTS postgis` on startup. If the database user cannot create extensions, install PostGIS ahead of time.

## Local Setup

1. Start PostgreSQL and ensure both `postgres` and `testpostgres` exist.
2. Optionally start Redis for cache-backed features:

```bash
cd ubercloneapp
docker compose up -d redis
```

3. Build or run the application:

```bash
cd ubercloneapp
./mvnw clean install
./mvnw spring-boot:run
```

4. Open Swagger UI:
   - `http://localhost:8080/swagger-ui/index.html`
   - `http://localhost:8080/swagger-ui.html`

`src/main/resources/data.sql` is loaded on normal startup and seeds a small dataset aligned with the current schema.

## Running Tests

```bash
cd ubercloneapp
./mvnw test
```

Notes:

- Tests use the `test` profile.
- Tests do not load `src/main/resources/data.sql`.
- Integration tests rely on the test-only auth headers defined in `TestSecurityConfig`:
  - `X-Test-User-Id`
  - `X-Test-Email`
  - `X-Test-Role`
- Most controller responses are wrapped in `ApiResponse<?>` by `GlobalResponseHandler`.

## Architecture Snapshot

- Controllers: `AuthController`, `RiderController`, `DriverController`, `AdminController`
- Services: interfaces in `services/`, implementations in `services/impl/`
- Repositories: Spring Data JPA repositories, including PostGIS queries in `DriverRepository`
- Strategies:
  - `RideStrategyManager` chooses fare and driver-matching strategies
  - `PaymentStrategyManager` chooses cash or wallet payment handling
- Security:
  - `JwtAuthenticationFilter`
  - `JwtTokenProvider`
  - stateless `SecurityConfig`
- Cross-cutting:
  - `GlobalResponseHandler`
  - `GlobalExceptionHandler`
  - custom actuator health indicators
- External integrations:
  - OSRM public routing API
  - Redis cache support

## Branching Recommendation

Use a clear promotion path instead of treating a feature branch as production:

- `main` - long-lived integration branch
- `feature/*` - feature and exploratory work, including the existing `feature/production-ready`
- `codex/final-build-stabilization` - hardening and verification branch
- `production` - promotion-only branch that should always contain the cleanest releasable state

Recommended flow:

1. Build and test on a feature or stabilization branch.
2. Merge or cherry-pick only validated commits into `production`.
3. Protect `production` from direct experimental work.
4. Tag releases from `production`.

## Related Docs

- `AGENTS.md` - repository guidance for coding agents
- `docs/IMPLEMENTATION-INSTRUCTIONS.md` - stabilization and release notes
- `.claude/...` - local agent command and helper documentation

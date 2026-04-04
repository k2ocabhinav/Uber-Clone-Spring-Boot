# Implementation & Release Instructions

This document describes the current project state and release processes.

## Current Status (as of 2026-04-05)

The codebase is stabilized and verified:

- `cd ubercloneapp && ./mvnw test` — passes
- `cd ubercloneapp && ./mvnw spring-boot:run` — boots successfully against PostgreSQL/PostGIS
- Baseline tagged as `v1.0.0` on `main`

### What's Implemented

- Full ride lifecycle (request → accept → OTP start → end → payment)
- JWT-based login and stateless Spring Security
- Strategy-based fare calculation (default + surge pricing 6-9 PM)
- Strategy-based driver matching (nearest driver vs highest-rated)
- PostGIS geospatial queries for nearby driver search
- OSRM distance integration for fare calculation
- Cash + Wallet payment strategies
- Admin dashboard (stats, revenue, driver approval, user management)
- Rating system (rider ↔ driver)
- Redis cache support (optional)
- Actuator health indicators
- Comprehensive test suite (unit + service + controller integration)
- Database indexes and fetch optimizations on all entities
- Request logging via AOP aspect

## Verification Commands

Run all commands from the Maven project root:

```bash
cd ubercloneapp
./mvnw clean install
./mvnw test
./mvnw spring-boot:run
```

Environment assumptions:

- PostgreSQL on `localhost:5432`
- Databases: `postgres` and `testpostgres`
- Username: `postgres`
- Password: `user`
- Optional Redis on `localhost:6379`

## Branch Model

See [`docs/BRANCHING_STRATEGY.md`](BRANCHING_STRATEGY.md) for the full workflow.

Summary:

- `main` — primary branch, always deployable
- `feature/<name>` — feature branches from `main`
- `production` — promotion-only, points to latest tagged release
- Tags: `v1.0.0`, `v1.1.0`, etc. on `main`

## Testing Notes for Agents

- Integration tests rely on the test headers in `TestSecurityConfig`:
  - `X-Test-User-Id`
  - `X-Test-Email`
  - `X-Test-Role`
- Controller responses are wrapped by `ApiResponse<?>`, so assertions should target `$.data` or `$.error`.
- `UserPrincipal.getUserId()` is a `User.id`; map from user to rider or driver through `findByUserId(...)`.
- `data.sql` is loaded only for the default profile and must stay aligned with the live schema.

## Remaining Improvements

- Add CI gates (GitHub Actions) that run `./mvnw test` on every PR
- Add branch protection rules for `main` and `production`
- Decide whether Redis should be mandatory or remain optional for local dev
- Add startup smoke test / health-check job

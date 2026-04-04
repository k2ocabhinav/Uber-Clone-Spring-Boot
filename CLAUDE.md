# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Status

This is a **Spring Boot 3.3.1 / Java 22** ride-sharing backend API. The codebase was stabilized on 2026-04-05 and is verified working. Build passes, tests pass, app boots with JWT, admin, caching, and full ride lifecycle.

## Build & Run Commands

All commands run from `ubercloneapp/` subdirectory (the actual Maven project root):

```bash
cd ubercloneapp

# Build
./mvnw clean install

# Run
./mvnw spring-boot:run

# Run tests
./mvnw test

# Run a single test class
./mvnw test -Dtest=UbercloneappApplicationTests
```

**Prerequisites:** PostgreSQL running on localhost:5432 with databases `postgres` and `testpostgres`, user `postgres`, password `user`. The app uses `create-drop` DDL mode. Normal startup seeds data from `src/main/resources/data.sql`; tests skip that seed file and use the `test` profile.

## Architecture

Spring Boot 3.3.1 / Java 22 ride-sharing application. Base package: `com.github.k2ocabhinav.ubercloneapp`.

### Layered Structure

- **Controllers** (`controllers/`) — REST endpoints: `AuthController` (`/auth`), `RiderController` (`/riders`), `DriverController` (`/drivers`), `AdminController` (`/api/v1/admin`)
- **Services** (`services/`) — Interfaces in `services/`, implementations in `services/impl/`. All service classes use constructor injection via Lombok `@RequiredArgsConstructor`.
- **Repositories** (`repositories/`) — Spring Data JPA repositories. `DriverRepository` uses PostGIS native queries (`ST_Distance`, `ST_DWithin`) for geospatial driver matching.
- **Entities** (`entities/`) — JPA entities with Lombok annotations. Enums live in `entities/enums/` (RideStatus, RideRequestStatus, PaymentMethod, PaymentStatus, Role, TransactionType, TransactionMethod).
- **DTOs** (`dto/`) — Data transfer objects mapped via ModelMapper (configured in `configs/MapperConfig`).
- **Security** (`configs/`, `security/`) — Stateless JWT auth via `SecurityConfig`, `JwtAuthenticationFilter`, `JwtTokenProvider`, and `UserPrincipal`.
- **Observability** (`actuator/`) — Custom health indicators for application and database visibility.

### Strategy Pattern

Two strategy managers select behavior at runtime:

- **`RideStrategyManager`** — Selects fare calculation (default vs surge pricing between 6-9 PM) and driver matching (nearest driver vs highest-rated for riders with rating >= 4.8).
- **`PaymentStrategyManager`** — Routes to `CashPaymentStrategy` or `WalletPaymentStrategy` based on payment method.

Strategy interfaces: `RideFareCalculationStrategy`, `DriverMatchingStrategy`, `PaymentStrategy`.

### Cross-cutting

- **`GlobalResponseHandler`** — `ResponseBodyAdvice` that wraps all controller responses in `ApiResponse<>` (excludes `/v3/api-docs` and `/actuator` paths).
- **`GlobalExceptionHandler`** — Centralized exception handling via `@RestControllerAdvice`.
- **Custom exceptions:** `ResourceNotFoundException`, `RuntimeConflictException`.
- **Caching:** Redis dependencies and cache service exist, but tests disable Redis repositories for local reliability.

### Key Dependencies

- PostgreSQL + PostGIS (Hibernate Spatial)
- ModelMapper for entity-DTO mapping
- Lombok throughout
- SpringDoc OpenAPI (Swagger UI at `/swagger-ui.html`)
- Spring Boot Actuator
- Spring Security + JJWT
- Redis

### Ride Lifecycle

1. Rider requests ride → `RiderService.requestRide()` calculates fare, finds matching drivers
2. Driver accepts → `DriverService.acceptRide()` creates Ride with OTP
3. Driver starts ride with OTP verification → `DriverService.startRide()`
4. Driver ends ride → `DriverService.endRide()` triggers payment processing
5. Both parties can rate each other via `RatingService`

## Testing Notes

- Tests use `src/test/resources/application-test.properties`.
- Controller integration tests use the test-only headers configured in `TestSecurityConfig`:
  - `X-Test-User-Id`
  - `X-Test-Email`
  - `X-Test-Role`
- Because `GlobalResponseHandler` wraps controller output, JSON assertions usually need to target `$.data` or `$.error`.
- `UserPrincipal.getUserId()` maps to `User.id`, not `Rider.id` or `Driver.id`. Use `findByUserId(...)` for domain lookup.
- `src/main/resources/data.sql` is schema-sensitive. Keep it aligned with snake_case column names and current enum persistence rules.

## Branch Strategy

> **Full reference:** [`docs/BRANCHING_STRATEGY.md`](docs/BRANCHING_STRATEGY.md)

This project uses **GitHub Flow**. Key rules:

- **`main`** — primary branch. Always passes tests. Always deployable.
- **`feature/<name>`** — feature branches for isolated work, branched from `main`.
- **`production`** — promotion-only branch pointing to the latest tagged release.

**Commit convention:** `<type>: <description>` where type is one of: `feat`, `fix`, `refactor`, `test`, `docs`, `chore`.

## Related Docs

- `AGENTS.md` — repository guidance for coding agents
- `docs/BRANCHING_STRATEGY.md` — full branching and release workflow
- `docs/IMPLEMENTATION-INSTRUCTIONS.md` — stabilization and release notes

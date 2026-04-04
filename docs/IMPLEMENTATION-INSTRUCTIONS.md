# Stabilization and Release Instructions

This document replaces the older handoff note that referenced unfinished JWT work on `feature/complete-backend-jwt-auth`. The repository has moved beyond that point, and the current focus is build stabilization, test reliability, and release branch hygiene.

## Current Status

- Working branch for the latest hardening pass: a dedicated stabilization branch
- Historical feature branch with large recent changes: `feature/production-ready`
- Verified locally on 2026-04-05:
  - `cd ubercloneapp && ./mvnw test`
  - `cd ubercloneapp && ./mvnw spring-boot:run`

At the time of this update, the local Maven test suite passes and the application starts successfully against PostgreSQL/PostGIS.

## What Was Stabilized

The latest pass focused on making the recently expanded codebase consistent and testable:

- Completed JWT-backed login flow and controller endpoint wiring
- Replaced hardcoded rider and driver lookups with `findByUserId(...)` logic
- Repaired ModelMapper edge cases for user names and geometry conversions
- Updated test configuration to use the `test` profile cleanly
- Added test-only request headers for authenticated controller integration tests
- Realigned `data.sql` with the current schema and enum persistence behavior
- Verified the app boots with the default profile and seeded data

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

## Branch Roles

To keep release management understandable, use each branch for one purpose:

- `main`
  - ongoing integration branch
- `feature/*`
  - implementation and exploratory branches
- `feature/production-ready`
  - keep as a historical release-candidate or integration branch until all needed work is promoted elsewhere
- `release/final-build-stabilization` or another neutral stabilization branch
  - hardening, documentation, and final verification
- `production`
  - clean promotion-only branch for the best validated build

## Recommendation for the Existing `feature/production-ready` Branch

Do not delete it immediately. It already contains a large amount of implementation work and serves as a useful audit trail.

Recommended handling:

1. Finish stabilization on a dedicated stabilization branch.
2. Commit the validated changes there.
3. Create a dedicated `production` branch from that clean verified commit.
4. Treat `feature/production-ready` as a release-candidate history branch, not the final source of truth.
5. After the team is comfortable with the new flow, stop using `feature/production-ready` as a production signal.

## Promotion Workflow

Use this workflow for future releases:

1. Implement or stabilize work on `feature/*` or a dedicated stabilization branch.
2. Run `./mvnw test`.
3. Run a local smoke start with `./mvnw spring-boot:run`.
4. Merge or cherry-pick only the validated commit set into `production`.
5. Tag the release from `production`.
6. Protect `production` in the remote host so it is never used for active feature work.

## Testing Notes for Future Agents

- Integration tests rely on the test headers in `TestSecurityConfig`:
  - `X-Test-User-Id`
  - `X-Test-Email`
  - `X-Test-Role`
- Controller responses are usually wrapped by `ApiResponse<?>`, so assertions should target `$.data` or `$.error`.
- `UserPrincipal.getUserId()` is a `User.id`; always map from user to rider or driver through repository methods such as `findByUserId(...)`.
- `data.sql` is loaded only for the default profile and must stay aligned with the live schema.

## Remaining Operational Improvements

The build is now in a much better place, but a few release-engineering improvements are still worth doing:

- Add CI gates that run `./mvnw test` on every branch intended for promotion
- Add branch protection for `production`
- Decide whether Redis should be mandatory for local development or remain optional
- Add a small smoke test or health-check job for startup verification against PostgreSQL/PostGIS

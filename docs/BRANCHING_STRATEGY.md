# Branching Strategy

> **This is the single source of truth for Git workflow in this repository.**
> All agents and developers MUST follow this standard.

## Model: GitHub Flow (Portfolio-Grade)

This project uses simplified **GitHub Flow** designed for:
- An always demo-ready `main` branch
- Clean parallel feature development (including by AI agents)
- Straightforward release tagging and deployment promotion

## Branch Roles

| Branch | Purpose | Who Commits | Protected? |
|--------|---------|-------------|------------|
| `main` | Primary integration branch. **Always passes tests. Always deployable. Always demo-ready.** | Merge PRs only (no direct pushes after initial setup) | Yes |
| `feature/<name>` | Single-feature scope. Created from `main`, merged back to `main` via PR | Agent or developer | No |
| `production` | Points to the latest production-tagged release commit on `main`. Updated only by fast-forward from a tagged `main` commit | Fast-forward only | Yes |

### Visual

```
main  (always green, always deployable)
 ├── feature/real-time-notifications        → PR to main
 ├── feature/scheduled-rides                → PR to main
 ├── feature/promo-discount-engine          → PR to main
 ├── feature/fare-estimation-eta            → PR to main
 └── feature/driver-earnings-dashboard      → PR to main
```

## Branch Naming Convention

```
feature/<short-kebab-case-name>
```

Examples:
- `feature/real-time-notifications`
- `feature/scheduled-rides`
- `feature/promo-discount-engine`

Do NOT use:
- `dev/...`, `agent/...`, `wip/...` — these are non-standard
- Branch names with underscores — use hyphens

## Commit Message Convention

```
<type>: <short imperative description>

Types:
  feat     — new feature or capability
  fix      — bug fix
  refactor — code restructuring without behavior change
  test     — adding or updating tests
  docs     — documentation only
  chore    — build, config, or tooling changes

Examples:
  feat: add WebSocket notification endpoint
  test: add ScheduledRideService unit tests
  fix: handle null promo code in fare calculation
  docs: update API documentation for earnings endpoints
```

Rules:
- First line ≤ 72 characters
- Imperative mood ("add", not "added" or "adding")
- No period at end of subject line
- Capitalize the first word after the type prefix? No — keep it lowercase for consistency

## Feature Branch Workflow

### Creating a feature branch
```bash
git checkout main
git pull origin main
git checkout -b feature/<name>
```

### Working on a feature
1. Make changes, commit frequently following the commit convention
2. Run `cd ubercloneapp && ./mvnw test` — all tests must pass
3. Push the branch: `git push origin feature/<name>`

### Merging a feature
1. Ensure `./mvnw test` passes on the feature branch
2. Create a PR from `feature/<name>` → `main`
3. PR must be reviewed (by human or lead agent)
4. Squash-merge or merge commit — developer's choice
5. Delete the feature branch after merge

### Definition of Done (for each feature branch)
1. `./mvnw test` passes with zero failures
2. All new code has corresponding unit tests
3. All new controller endpoints have integration tests
4. All new endpoints documented with Swagger `@Operation` annotations
5. `data.sql` updated if new entities require seed data
6. No `TODO` comments left in production code
7. `FEATURE_PLAN.md` in repo root (on the feature branch) is updated with completion status

## Release Workflow

### Tagging a release
```bash
git checkout main
git pull origin main
# Ensure tests pass
cd ubercloneapp && ./mvnw test
# Tag
git tag -a v<X.Y.Z> -m "<release description>"
git push origin v<X.Y.Z>
```

### Promoting to production
```bash
git checkout production
git merge --ff-only v<X.Y.Z>
git push origin production
```

The `production` branch is NEVER used for active work. It is a pointer to "what is currently deployed / the latest validated release."

## Version Numbering

Use **Semantic Versioning**: `vMAJOR.MINOR.PATCH`

- **MAJOR**: Breaking API changes
- **MINOR**: New features (backward-compatible)
- **PATCH**: Bug fixes

Current baseline: `v1.0.0` — stabilized build with JWT, admin, caching, indexes, tests.

## Rules for AI Agents

1. **Read this file and `AGENTS.md` before starting any work**
2. **Always branch from `main`** — never from `production` or another feature branch
3. **Never push directly to `main`** — always use feature branches
4. **Never modify another feature branch's code** — work only within your assigned branch
5. **Run `./mvnw test` before declaring done** — all tests must pass, including pre-existing ones
6. **Follow the commit convention** — `<type>: <description>`
7. **Each feature branch must have a `FEATURE_PLAN.md`** at the repo root describing the feature, its status, and what was implemented
8. **If you need cross-feature functionality** (e.g., fare estimation needs promo codes), add a null-safe stub that can be wired up during merge — do NOT create a dependency on another unmerged branch

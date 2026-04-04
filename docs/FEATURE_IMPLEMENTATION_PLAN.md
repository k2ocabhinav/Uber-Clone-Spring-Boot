# Feature Implementation Plan — v1.1.0

> **Purpose:** This document is the single source of truth for the next round of feature development. It describes 5 features, each to be built on its own `feature/<name>` branch from `main`. Any agent or developer working on a feature MUST read this entire document, plus `AGENTS.md` and `docs/BRANCHING_STRATEGY.md`, before writing any code.

---

## Table of Contents

1. [Baseline & Current State](#1-baseline--current-state)
2. [Universal Rules for All Feature Work](#2-universal-rules-for-all-feature-work)
3. [Feature Branches to Create](#3-feature-branches-to-create)
4. [Feature 1 — Real-Time Notifications](#4-feature-1--real-time-notifications)
5. [Feature 2 — Scheduled Rides](#5-feature-2--scheduled-rides)
6. [Feature 3 — Promo Code & Discount Engine](#6-feature-3--promo-code--discount-engine)
7. [Feature 4 — Fare Estimation & ETA Preview](#7-feature-4--fare-estimation--eta-preview)
8. [Feature 5 — Driver Earnings Dashboard](#8-feature-5--driver-earnings-dashboard)
9. [Cross-Feature Dependency Map](#9-cross-feature-dependency-map)
10. [Merge Order & Integration Plan](#10-merge-order--integration-plan)

---

## 1. Baseline & Current State

**Tag:** `v1.0.0` on `main` (commit `a53639f`)
**Build:** `cd ubercloneapp && ./mvnw test` — passes
**App:** `cd ubercloneapp && ./mvnw spring-boot:run` — boots against PostgreSQL/PostGIS

### What Already Exists

| Layer | What's There |
|-------|-------------|
| **Auth** | JWT login, signup, driver onboarding, stateless Spring Security |
| **Ride lifecycle** | Request → accept → OTP start → end → payment processing |
| **Fare** | Strategy-based: default + surge pricing (6–9 PM), configurable via `FareConfig` |
| **Driver matching** | Strategy-based: nearest driver vs highest-rated (threshold in `PlatformConfig`) |
| **Payment** | Cash + Wallet strategies, wallet top-up/withdrawal |
| **Rating** | Rider ↔ Driver mutual rating after ride ends |
| **Admin** | Dashboard, daily stats, revenue report, driver approval, user management |
| **Caching** | Redis cache service (optional) |
| **Observability** | Actuator health indicators, request logging AOP |
| **Testing** | Unit tests (service layer), integration tests (controllers), test builders |

### Key Existing Patterns (Agents Must Follow)

| Pattern | How It's Done |
|---------|--------------|
| Dependency injection | Constructor injection via Lombok `@RequiredArgsConstructor`. Never `@Autowired`. |
| Transactions | `@Transactional` on all mutation methods. `@Transactional(readOnly = true)` on reads. |
| DTO mapping | `ModelMapper` (configured in `configs/MapperConfig`). If custom mappings needed, add to `MapperConfig`. |
| API responses | `GlobalResponseHandler` auto-wraps all controller returns in `ApiResponse<>`. Do NOT wrap manually. |
| Exceptions | `ResourceNotFoundException` (404), `RuntimeConflictException` (409). Handled by `GlobalExceptionHandler`. |
| Entity conventions | `@Version` for optimistic locking. `@Index` on frequently queried columns. Override `equals`/`hashCode` using ID only. `@ToString.Exclude` on lazy associations. |
| Controller conventions | `@Tag` + `@Operation` + `@SecurityRequirement(name="bearerAuth")` on all secured endpoints. |
| Enum persistence | `@Enumerated(EnumType.STRING)` always. |
| Current user | `UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal(); principal.getUserId()` returns `User.id`. Then use `findByUserId(...)` for Rider/Driver lookup. |
| Config externalization | `@ConfigurationProperties` classes in `configs/` (see `FareConfig`, `PlatformConfig`). |
| Test security | Integration tests use `TestSecurityConfig` headers: `X-Test-User-Id`, `X-Test-Email`, `X-Test-Role`. |
| Test assertions | JSON assertions target `$.data` or `$.error` because `GlobalResponseHandler` wraps everything. |
| Test data builders | Reusable builders in `testdata/` package: `UserTestBuilder`, `RiderTestBuilder`, `DriverTestBuilder`, `RideTestBuilder`, `RideRequestTestBuilder`, `WalletTestBuilder`. |

### Project Source Layout

```
ubercloneapp/src/main/java/com/github/k2ocabhinav/ubercloneapp/
├── actuator/          # Health indicators
├── advices/           # GlobalResponseHandler, GlobalExceptionHandler
├── aspect/            # RequestLoggingAspect
├── configs/           # SecurityConfig, MapperConfig, RedisConfig, FareConfig, PlatformConfig, MetricsConfig
├── controllers/       # AuthController, RiderController, DriverController, AdminController
├── dto/               # All DTOs (flat), dto/admin/ for admin-specific DTOs
├── entities/          # JPA entities
├── entities/enums/    # All enum types
├── exceptions/        # ResourceNotFoundException, RuntimeConflictException
├── repositories/      # Spring Data JPA repos
├── security/          # JwtAuthenticationFilter, JwtTokenProvider, UserPrincipal, CorrelationIdFilter
├── services/          # Service interfaces
├── services/impl/     # Service implementations
├── strategies/        # Strategy interfaces + managers
├── strategies/impl/   # Strategy implementations
└── utils/             # GeometryUtil
```

---

## 2. Universal Rules for All Feature Work

Every agent MUST follow these rules. Violations will require rework.

### Git Workflow

1. **Read `AGENTS.md` and `docs/BRANCHING_STRATEGY.md` before starting.**
2. Branch from `main`: `git checkout main && git pull && git checkout -b feature/<name>`
3. Commit convention: `<type>: <description>` (types: `feat`, `fix`, `refactor`, `test`, `docs`, `chore`)
4. Never push directly to `main`. Work only on your assigned feature branch.
5. Never modify code that belongs to another feature's scope.

### Code Quality

6. Run `cd ubercloneapp && ./mvnw test` before declaring done — ALL tests must pass, including pre-existing ones.
7. Every new service method must have a unit test.
8. Every new controller endpoint must have an integration test.
9. Every new controller endpoint must have `@Operation` + `@Tag` Swagger annotations.
10. No `TODO` comments in production code.
11. Update `data.sql` if new entities need seed data.

### Cross-Feature Independence

12. Do NOT depend on another unmerged feature branch.
13. If your feature could benefit from another feature (e.g., fare estimation could use promo codes), add a **null-safe stub** with a clear comment like: `// Integration point: apply promo discount when promo-discount-engine feature is merged`
14. Design interfaces so that cross-feature wiring can be done in a small follow-up PR after both features are merged.

---

## 3. Feature Branches to Create

| Branch Name | Feature | Priority |
|-------------|---------|----------|
| `feature/real-time-notifications` | WebSocket + event-driven notification system | High |
| `feature/scheduled-rides` | Advance ride booking with auto-dispatch | High |
| `feature/promo-discount-engine` | Promo codes, coupons, referral discounts | High |
| `feature/fare-estimation-eta` | Fare estimate + ETA preview before booking | Medium |
| `feature/driver-earnings-dashboard` | Driver earnings tracking + payout system | Medium |

All branches are created from `main` at tag `v1.0.0`.

---

## 4. Feature 1 — Real-Time Notifications

**Branch:** `feature/real-time-notifications`

### 4.1 What It Does

Adds a real-time notification system so riders and drivers receive instant updates about ride lifecycle events. Uses Spring WebSocket (STOMP over SockJS) for push delivery and Spring's `ApplicationEventPublisher` for internal event propagation. Notifications are also persisted for history/audit.

### 4.2 User Stories

- Rider gets notified when a driver accepts their ride
- Rider gets notified when ride starts, ends, or is cancelled
- Driver gets notified about new ride requests, cancellations, and payments
- Any user can view their notification history (paginated)
- Any user can mark notifications as read

### 4.3 New Files to Create

| File | Location | Description |
|------|----------|-------------|
| `Notification.java` | `entities/` | Notification entity (id, user, title, message, type, referenceId, read, createdTime, version) |
| `NotificationType.java` | `entities/enums/` | Enum: RIDE_REQUESTED, RIDE_ACCEPTED, RIDE_STARTED, RIDE_ENDED, RIDE_CANCELLED, PAYMENT_PROCESSED, DRIVER_ARRIVING, GENERAL |
| `NotificationRepository.java` | `repositories/` | JPA repo with: findByUserOrderByCreatedTimeDesc, countByUserAndReadFalse, markAllAsReadByUser (@Modifying @Query) |
| `NotificationService.java` | `services/` | Interface: createNotification, getUserNotifications, markAsRead, markAllAsRead, getUnreadCount |
| `NotificationServiceImpl.java` | `services/impl/` | Implementation following existing patterns |
| `WebSocketNotificationService.java` | `services/impl/` | Wraps `SimpMessagingTemplate`, sends to `/user/{userId}/queue/notifications` |
| `NotificationDto.java` | `dto/` | DTO: id, title, message, type, referenceId, read, createdTime |
| `NotificationController.java` | `controllers/` | REST: GET /notifications, GET /notifications/unread-count, POST /notifications/{id}/read, POST /notifications/read-all |
| `WebSocketConfig.java` | `configs/` | STOMP broker config: /topic, /queue prefixes; /ws endpoint with SockJS; /user destination prefix |
| `WebSocketAuthInterceptor.java` | `configs/` | ChannelInterceptor that extracts JWT from STOMP CONNECT frame, validates via JwtTokenProvider, sets Principal |
| `RideRequestedEvent.java` | `events/` | Event POJO: rideRequestId, riderId, pickupLocation |
| `RideAcceptedEvent.java` | `events/` | Event POJO: rideId, riderId, driverId |
| `RideStartedEvent.java` | `events/` | Event POJO: rideId, riderId, driverId |
| `RideEndedEvent.java` | `events/` | Event POJO: rideId, riderId, driverId, fare |
| `RideCancelledEvent.java` | `events/` | Event POJO: rideId, riderId, driverId (nullable), cancelledBy |
| `PaymentProcessedEvent.java` | `events/` | Event POJO: rideId, riderId, driverId, amount, paymentMethod |
| `NotificationEventListener.java` | `events/` | @Component with @EventListener for each event → creates Notification + sends WebSocket |
| `NotificationServiceImplTest.java` | test `services/impl/` | Unit tests: create, mark read, unread count |
| `NotificationEventListenerTest.java` | test `events/` | Unit tests: each event creates correct notification |
| `NotificationControllerIntegrationTest.java` | test `controllers/` | Integration tests: all 4 endpoints |
| `NotificationTestBuilder.java` | test `testdata/` | Test data builder |

### 4.4 Existing Files to Modify

| File | Change |
|------|--------|
| `pom.xml` | Add `spring-boot-starter-websocket` dependency |
| `RiderServiceImpl.java` | Inject `ApplicationEventPublisher`, publish `RideRequestedEvent` in `requestRide()`, `RideCancelledEvent` in `cancelRide()` |
| `DriverServiceImpl.java` | Inject `ApplicationEventPublisher`, publish `RideAcceptedEvent` in `acceptRide()`, `RideStartedEvent` in `startRide()`, `RideEndedEvent` in `endRide()`, `RideCancelledEvent` in `cancelRide()` |
| `PaymentServiceImpl.java` | Inject `ApplicationEventPublisher`, publish `PaymentProcessedEvent` in `processPayment()` |
| `SecurityConfig.java` | Add `.requestMatchers("/ws/**").permitAll()` to allow WebSocket handshake |

### 4.5 Entity Schema: `Notification`

```java
@Entity
@Table(indexes = {
    @Index(name = "idx_notification_user", columnList = "user_id"),
    @Index(name = "idx_notification_created", columnList = "createdTime"),
    @Index(name = "idx_notification_read", columnList = "read")
})
public class Notification {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @ToString.Exclude
    private User user;

    private String title;
    private String message;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    private Long referenceId;  // ride ID, request ID, etc.
    private Boolean read = false;

    @CreationTimestamp
    private LocalDateTime createdTime;

    @Version
    private Long version;

    // equals/hashCode by id (same pattern as other entities)
}
```

### 4.6 Test Requirements

| Test Class | Min Scenarios |
|-----------|---------------|
| `NotificationServiceImplTest` | createNotification success, getUserNotifications paginated, markAsRead, markAllAsRead, getUnreadCount |
| `NotificationEventListenerTest` | One test per event type (6 events = 6 tests) verifying correct notification creation |
| `NotificationControllerIntegrationTest` | GET /notifications → paginated list, GET /unread-count → number, POST /{id}/read → marks read, POST /read-all → marks all |

### 4.7 Acceptance Criteria

- [ ] `./mvnw test` passes (all tests, old + new)
- [ ] WebSocket endpoint at `/ws` is reachable
- [ ] All 6 ride lifecycle events are published from existing services
- [ ] Events create persistent Notification records
- [ ] REST API for notification history, mark-read, unread-count works
- [ ] All endpoints have Swagger docs
- [ ] No TODO comments

---

## 5. Feature 2 — Scheduled Rides

**Branch:** `feature/scheduled-rides`

### 5.1 What It Does

Allows riders to book rides in advance. A background scheduler auto-dispatches scheduled rides when their time approaches, feeding them into the existing driver-matching flow.

### 5.2 User Stories

- Rider schedules a ride for tomorrow 6 AM
- Rider views their upcoming scheduled rides
- Rider cancels or reschedules a scheduled ride
- System auto-dispatches the ride 15 minutes before scheduled time
- Admin sees all platform-wide scheduled rides

### 5.3 New Files to Create

| File | Location | Description |
|------|----------|-------------|
| `ScheduledRideService.java` | `services/` | Interface: scheduleRide, cancelScheduledRide, rescheduleRide, getMyScheduledRides, getAllScheduledRides (admin), dispatchScheduledRides |
| `ScheduledRideServiceImpl.java` | `services/impl/` | Full implementation (see logic below) |
| `ScheduledRideConfig.java` | `configs/` | @ConfigurationProperties: minAdvanceMinutes=30, maxAdvanceDays=7, dispatchWindowMinutes=15 |
| `ScheduledRideDispatcher.java` | `configs/` | @Component with @Scheduled(fixedRate=60000) calling dispatchScheduledRides() |
| `ScheduledRideDto.java` | `dto/` | DTO for scheduled ride list: id, pickupLocation, dropOffLocation, paymentMethod, fare, scheduledTime, status, requestedTime |
| `ScheduledRideServiceImplTest.java` | test `services/impl/` | Unit tests |
| `ScheduledRideControllerIntegrationTest.java` | test `controllers/` | Integration tests |

### 5.4 Existing Files to Modify

| File | Change |
|------|--------|
| `RideRequest.java` | Add `private LocalDateTime scheduledTime;` (nullable). Add `@Index(name = "idx_ride_request_scheduled", columnList = "scheduledTime")` |
| `RideRequestStatus.java` | Add `SCHEDULED` value: `PENDING, CONFIRMED, CANCELLED, SCHEDULED` |
| `RideRequestDto.java` | Add `private LocalDateTime scheduledTime;` |
| `RideRequestRepository.java` | Add: `findByRiderAndRideRequestStatusOrderByScheduledTimeAsc(Rider, RideRequestStatus, Pageable)`, `findDueScheduledRides(RideRequestStatus, LocalDateTime)` (@Query), `findByRideRequestStatusOrderByScheduledTimeAsc(RideRequestStatus, Pageable)` |
| `RiderController.java` | Add 4 endpoints: POST /riders/schedule-ride, GET /riders/scheduled-rides, PUT /riders/scheduled-rides/{id}/reschedule, DELETE /riders/scheduled-rides/{id} |
| `AdminController.java` | Add: GET /api/v1/admin/scheduled-rides |
| `UbercloneappApplication.java` | Add `@EnableScheduling` |
| `application.properties` | Add: `app.scheduled-ride.min-advance-minutes=30`, `app.scheduled-ride.max-advance-days=7`, `app.scheduled-ride.dispatch-window-minutes=15` |
| `application-test.properties` | Add same properties for test profile |

### 5.5 Core Service Logic

**`scheduleRide(rideRequestDto)`:**
1. Extract `scheduledTime` from DTO. Validate: must be >= now + minAdvanceMinutes and <= now + maxAdvanceDays. Throw `RuntimeConflictException` if invalid.
2. Get current rider via `getCurrentRider()` pattern.
3. Map DTO → `RideRequest` entity, set status = `SCHEDULED`.
4. Calculate fare using `rideStrategyManager.rideFareCalculationStrategy().calculateFare(rideRequest)`.
5. Save and return DTO.

**`dispatchScheduledRides()`:**
1. Query: `findDueScheduledRides(SCHEDULED, now + dispatchWindowMinutes)`
2. For each result: set status to `PENDING`, save, then trigger driver matching via `rideStrategyManager.driverMatchingStrategy(riderRating).findMatchingDrivers(rideRequest)`.
3. Wrap each dispatch in its own try-catch so one failure doesn't block others.
4. Log each dispatch action.

**`cancelScheduledRide(id)`:** Verify ownership + status==SCHEDULED, update to CANCELLED.

**`rescheduleRide(id, newTime)`:** Verify ownership + status==SCHEDULED, validate newTime, update scheduledTime, recalculate fare, save.

### 5.6 Test Requirements

| Test Class | Min Scenarios |
|-----------|---------------|
| `ScheduledRideServiceImplTest` | Schedule success, reject past time, reject <30min, reject >7days, cancel success, cancel non-SCHEDULED fails, reschedule success, reschedule recalculates fare, dispatch finds due rides, dispatch changes status to PENDING |
| `ScheduledRideControllerIntegrationTest` | POST schedule → 200, POST with past time → error, GET scheduled → paginated list, PUT reschedule → updated, DELETE cancel → success |

### 5.7 Acceptance Criteria

- [ ] `./mvnw test` passes (all tests, old + new)
- [ ] Riders can schedule/cancel/reschedule rides via API
- [ ] Auto-dispatcher runs every 60s and dispatches due rides
- [ ] Validation enforces 30min-7day window
- [ ] Admin can view all scheduled rides
- [ ] Unit + integration tests
- [ ] Swagger docs on all endpoints

---

## 6. Feature 3 — Promo Code & Discount Engine

**Branch:** `feature/promo-discount-engine`

### 6.1 What It Does

Adds a promotional code system for rider discounts. Supports flat-amount and percentage-based discounts, usage limits, date-based validity, and per-user tracking. Integrates into the existing fare calculation pipeline (applied after surge pricing). Also generates a referral code for each user at signup.

### 6.2 User Stories

- Admin creates promo codes with discount type, value, dates, usage limits
- Rider applies a promo code when requesting a ride
- System validates promo code (active, not expired, not over-used, meets min fare)
- Fare is reduced by the promo discount
- Each user gets a referral code on signup; referred users get a bonus
- Admin can deactivate promo codes

### 6.3 New Files to Create

| File | Location | Description |
|------|----------|-------------|
| `PromoCode.java` | `entities/` | Entity: id, code (unique), discountType (FLAT/PERCENTAGE), discountValue, maxUses, currentUses, startDate, endDate, minRideFare, active, description, createdTime, version |
| `PromoCodeUsage.java` | `entities/` | Entity: id, promoCode (ManyToOne), user (ManyToOne), ride (ManyToOne nullable), discountApplied, usedAt. Unique constraint on (promoCode, user) to prevent double-dipping. |
| `DiscountType.java` | `entities/enums/` | Enum: FLAT, PERCENTAGE |
| `PromoCodeRepository.java` | `repositories/` | findByCode, findByActiveTrue, existsByCode |
| `PromoCodeUsageRepository.java` | `repositories/` | existsByPromoCodeAndUser, countByPromoCode |
| `PromoCodeService.java` | `services/` | Interface: createPromoCode, updatePromoCode, deactivatePromoCode, validateAndApplyPromo, getActivePromoCodes, getPromoCodeUsageStats |
| `PromoCodeServiceImpl.java` | `services/impl/` | Implementation (see logic below) |
| `PromoCodeDto.java` | `dto/` | DTO for admin CRUD: id, code, discountType, discountValue, maxUses, currentUses, startDate, endDate, minRideFare, active, description |
| `PromoCodeApplyDto.java` | `dto/` | DTO for rider apply: code |
| `PromoCodeResultDto.java` | `dto/` | DTO for apply result: valid, discountAmount, originalFare, discountedFare, message |
| `PromoCodeController.java` | `controllers/` | Admin CRUD: POST /api/v1/admin/promo-codes, GET /api/v1/admin/promo-codes, PUT /api/v1/admin/promo-codes/{id}, POST /api/v1/admin/promo-codes/{id}/deactivate. Rider: GET /promo-codes/active (public active promos) |
| `PromoCodeServiceImplTest.java` | test `services/impl/` | Unit tests |
| `PromoCodeControllerIntegrationTest.java` | test `controllers/` | Integration tests |
| `PromoCodeTestBuilder.java` | test `testdata/` | Test data builder |

### 6.4 Existing Files to Modify

| File | Change |
|------|--------|
| `RideRequest.java` | Add: `private String promoCode;` (nullable), `private Double discountAmount;` (default 0.0) |
| `Ride.java` | Add: `private String promoCode;` (nullable), `private Double discountAmount;` (default 0.0) |
| `RideRequestDto.java` | Add: `private String promoCode;`, `private Double discountAmount;` |
| `RideDto.java` | Add: `private String promoCode;`, `private Double discountAmount;` |
| `RiderServiceImpl.java` | In `requestRide()`: after fare calculation, if promoCode is present, call `promoCodeService.validateAndApplyPromo(promoCode, rider, fare)` → get discount → set discountAmount on rideRequest → subtract from fare |
| `User.java` | Add: `private String referralCode;` (unique, nullable) |
| `AuthServiceImpl.java` | In `signup()`: generate unique referral code (e.g., first 4 chars of name + random 6 digits), set on user |
| `SignupDto.java` | Add: `private String referredByCode;` (nullable) — if provided, give both users a bonus |

### 6.5 Entity Schema: `PromoCode`

```java
@Entity
@Table(indexes = {
    @Index(name = "idx_promo_code", columnList = "code"),
    @Index(name = "idx_promo_active", columnList = "active")
})
public class PromoCode {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    @Enumerated(EnumType.STRING)
    private DiscountType discountType;  // FLAT or PERCENTAGE

    private Double discountValue;       // e.g., 50.0 for ₹50 flat or 10.0 for 10%
    private Integer maxUses;            // null = unlimited
    private Integer currentUses = 0;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double minRideFare;         // minimum fare required to use this code
    private Boolean active = true;
    private String description;

    @CreationTimestamp
    private LocalDateTime createdTime;

    @Version
    private Long version;
}
```

### 6.6 Core Service Logic

**`validateAndApplyPromo(code, user, originalFare)`:**
1. Find promo by code → throw `ResourceNotFoundException` if not found
2. Check `active == true`
3. Check `now` is between `startDate` and `endDate`
4. Check `currentUses < maxUses` (if maxUses is not null)
5. Check `originalFare >= minRideFare` (if minRideFare is not null)
6. Check no existing `PromoCodeUsage` for this (promoCode, user) → prevent double-dipping
7. Calculate discount: if FLAT → discountValue, if PERCENTAGE → originalFare * discountValue / 100. Cap discount so fare doesn't go below 0.
8. Create `PromoCodeUsage` record
9. Increment `currentUses` on `PromoCode`
10. Return `PromoCodeResultDto` with valid=true, discountAmount, originalFare, discountedFare

### 6.7 Test Requirements

| Test Class | Min Scenarios |
|-----------|---------------|
| `PromoCodeServiceImplTest` | Create promo, apply FLAT discount, apply PERCENTAGE discount, reject expired, reject inactive, reject over-limit, reject below min fare, reject double-use, referral code generation |
| `PromoCodeControllerIntegrationTest` | Admin CREATE/GET/UPDATE/DEACTIVATE, Rider GET active promos, apply promo during ride request |

### 6.8 Acceptance Criteria

- [ ] `./mvnw test` passes
- [ ] Admin can CRUD promo codes
- [ ] Riders can apply promo code when requesting a ride
- [ ] Discount is correctly calculated and applied to fare
- [ ] Double-use prevention works
- [ ] Referral codes are generated on signup
- [ ] Unit + integration tests
- [ ] Swagger docs

---

## 7. Feature 4 — Fare Estimation & ETA Preview

**Branch:** `feature/fare-estimation-eta`

### 7.1 What It Does

Lets riders see an estimated fare and arrival time before committing to a ride request. Reuses the existing fare/strategy infrastructure without persisting anything. Extends the OSRM integration to return duration alongside distance.

### 7.2 User Stories

- Rider enters pickup and dropoff → sees estimated fare, distance, duration, and surge info
- Fare estimate reflects current surge pricing state
- ETA includes estimated time for driver to reach pickup

### 7.3 New Files to Create

| File | Location | Description |
|------|----------|-------------|
| `FareEstimateDto.java` | `dto/` | Response: estimatedFare, distance (km), duration (minutes), surgeMultiplier, surgeActive, estimatedPickupTime (minutes), promoDiscountStub (always 0 — integration point for promo feature) |
| `FareEstimateRequestDto.java` | `dto/` | Request: pickupLocation (PointDto), dropOffLocation (PointDto), paymentMethod |
| `FareEstimationService.java` | `services/` | Interface: estimateFare(FareEstimateRequestDto) → FareEstimateDto |
| `FareEstimationServiceImpl.java` | `services/impl/` | Implementation (see logic below) |
| `FareEstimationServiceImplTest.java` | test `services/impl/` | Unit tests |
| `FareEstimationControllerIntegrationTest.java` | test `controllers/` | Integration tests |

### 7.4 Existing Files to Modify

| File | Change |
|------|--------|
| `RiderController.java` | Add: POST /riders/fare-estimate → estimateFare |
| `DistanceService.java` | Add method: `double[] calculateDistanceAndDuration(Point src, Point dest)` returning [distance_km, duration_minutes]. Keep existing `calculateDistance` for backward compat. |
| `DistanceServiceOSRMImpl.java` | Implement `calculateDistanceAndDuration` — parse `duration` from OSRM response JSON alongside `distance`. OSRM already returns both. |

### 7.5 Core Service Logic

**`estimateFare(requestDto)`:**
1. Convert PointDto → Point for pickup and dropoff using `GeometryUtil`.
2. Call `distanceService.calculateDistanceAndDuration(pickup, dropoff)` → get [distance, duration].
3. Build a transient (non-persisted) `RideRequest` with pickup, dropoff, and a stub rider.
4. Call `rideStrategyManager.rideFareCalculationStrategy().calculateFare(transientRideRequest)` → get fare.
5. Determine surge: check if `rideStrategyManager.rideFareCalculationStrategy()` returned the surge strategy → set surgeActive=true, read surgeMultiplier from config.
6. Estimate pickup time: find nearest available driver distance via `DriverRepository` PostGIS query, compute travel time (distance / avg_speed or use OSRM for driver→pickup route).
7. Build and return `FareEstimateDto`.

**Key design decision:** This service does NOT persist anything. It's purely a read-only calculation. No `RideRequest` is saved.

**Promo code stub:** The response DTO includes a `promoDiscountStub` field (always 0.0) with a comment: `// Integration point: when promo-discount-engine is merged, call promoCodeService.calculateDiscount() here`

### 7.6 OSRM Response Parsing

The existing `DistanceServiceOSRMImpl` calls `http://router.project-osrm.org/route/v1/driving/{lon1},{lat1};{lon2},{lat2}?overview=false`. The response JSON already includes both `distance` (meters) and `duration` (seconds) in `routes[0].legs[0]`. Extend parsing to extract both.

### 7.7 Test Requirements

| Test Class | Min Scenarios |
|-----------|---------------|
| `FareEstimationServiceImplTest` | Estimate with default pricing, estimate with surge pricing, estimate with zero distance, mock OSRM response |
| `FareEstimationControllerIntegrationTest` | POST /riders/fare-estimate → valid estimate, POST with null locations → error |

### 7.8 Acceptance Criteria

- [ ] `./mvnw test` passes
- [ ] POST /riders/fare-estimate returns fare, distance, duration, surge info
- [ ] OSRM integration returns duration alongside distance
- [ ] No data is persisted during estimation
- [ ] Estimation result is cached in Redis (if available) with 5-minute TTL keyed on rounded coordinates
- [ ] Unit + integration tests
- [ ] Swagger docs

---

## 8. Feature 5 — Driver Earnings Dashboard

**Branch:** `feature/driver-earnings-dashboard`

### 8.1 What It Does

Gives drivers visibility into their per-ride earnings, commission breakdowns, and aggregate summaries. Adds a payout request system so drivers can cash out their earnings. Admin gets a payout management view.

### 8.2 User Stories

- Driver sees per-ride earning breakdown (gross fare, platform commission, net earning)
- Driver sees daily/weekly/monthly aggregate earnings
- Driver requests a payout of accumulated earnings
- Admin approves/rejects/processes payout requests

### 8.3 New Files to Create

| File | Location | Description |
|------|----------|-------------|
| `DriverEarning.java` | `entities/` | Entity: id, driver (ManyToOne), ride (OneToOne), grossFare, platformCommission, netEarning, createdTime, version |
| `PayoutRequest.java` | `entities/` | Entity: id, driver (ManyToOne), amount, status (PayoutStatus enum), requestedAt, processedAt, adminNote, version |
| `PayoutStatus.java` | `entities/enums/` | Enum: PENDING, APPROVED, REJECTED, PROCESSED |
| `DriverEarningRepository.java` | `repositories/` | findByDriver (paginated), sum queries for aggregation by date range |
| `PayoutRequestRepository.java` | `repositories/` | findByDriver (paginated), findByStatus (paginated) |
| `DriverEarningsService.java` | `services/` | Interface: createEarningRecord, getEarningsHistory, getDailySummary, getWeeklySummary, getMonthlySummary, requestPayout |
| `DriverEarningsServiceImpl.java` | `services/impl/` | Implementation |
| `PayoutService.java` | `services/` | Interface: requestPayout, approvePayout, rejectPayout, processPayout, getPendingPayouts (admin), getMyPayouts (driver) |
| `PayoutServiceImpl.java` | `services/impl/` | Implementation with wallet integration |
| `DriverEarningDto.java` | `dto/` | DTO: id, rideId, grossFare, platformCommission, netEarning, createdTime |
| `EarningsSummaryDto.java` | `dto/` | DTO: totalGross, totalCommission, totalNet, rideCount, period, startDate, endDate |
| `PayoutRequestDto.java` | `dto/` | DTO: id, amount, status, requestedAt, processedAt, adminNote |
| `DriverEarningsController.java` | `controllers/` | Driver endpoints: GET /drivers/earnings, GET /drivers/earnings/summary?period=daily&date=..., POST /drivers/earnings/payout |
| `DriverEarningsServiceImplTest.java` | test `services/impl/` | Unit tests |
| `PayoutServiceImplTest.java` | test `services/impl/` | Unit tests |
| `DriverEarningsControllerIntegrationTest.java` | test `controllers/` | Integration tests |
| `DriverEarningTestBuilder.java` | test `testdata/` | Test data builder |

### 8.4 Existing Files to Modify

| File | Change |
|------|--------|
| `DriverServiceImpl.java` | In `endRide()`: after payment processing, call `driverEarningsService.createEarningRecord(ride)` to auto-create the earning record |
| `AdminController.java` | Add: GET /api/v1/admin/payouts/pending, POST /api/v1/admin/payouts/{id}/approve, POST /api/v1/admin/payouts/{id}/reject, POST /api/v1/admin/payouts/{id}/process |
| `application.properties` | Commission rate is already configurable at `app.platform.commission-rate=0.30` — reuse this |

### 8.5 Entity Schema: `DriverEarning`

```java
@Entity
@Table(indexes = {
    @Index(name = "idx_earning_driver", columnList = "driver_id"),
    @Index(name = "idx_earning_created", columnList = "createdTime")
})
public class DriverEarning {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @ToString.Exclude
    private Driver driver;

    @OneToOne(fetch = FetchType.LAZY) @ToString.Exclude
    private Ride ride;

    private Double grossFare;
    private Double platformCommission;
    private Double netEarning;

    @CreationTimestamp
    private LocalDateTime createdTime;

    @Version
    private Long version;
}
```

### 8.6 Core Service Logic

**`createEarningRecord(ride)`:**
1. Get the ride's fare as grossFare.
2. Calculate platformCommission = grossFare × commissionRate (from `PlatformConfig.getCommissionRate()`).
3. Calculate netEarning = grossFare - platformCommission.
4. Create and save `DriverEarning` record.

**`getDailySummary(date)`:**
1. Query earnings for the current driver where createdTime is within the given date.
2. Aggregate: sum grossFare, sum commission, sum netEarning, count rides.
3. Return `EarningsSummaryDto`.

**`requestPayout(amount)`:**
1. Verify amount > 0.
2. Verify amount <= total unpaid balance (sum of netEarnings - sum of processed payouts).
3. Create `PayoutRequest` with status PENDING.

**`processPayout(payoutId)` (Admin):**
1. Find PayoutRequest, verify status == APPROVED.
2. Transfer from platform wallet to driver wallet using existing `WalletService.addMoneyToWallet()`.
3. Update status to PROCESSED, set processedAt.

### 8.7 Aggregation Queries

Use `@Query` with `SUM()` and date grouping:

```java
@Query("SELECT new com.github.k2ocabhinav.ubercloneapp.dto.EarningsSummaryDto(" +
       "SUM(e.grossFare), SUM(e.platformCommission), SUM(e.netEarning), COUNT(e), " +
       ":startDate, :endDate) " +
       "FROM DriverEarning e WHERE e.driver = :driver " +
       "AND e.createdTime >= :startDate AND e.createdTime < :endDate")
EarningsSummaryDto getEarningsSummary(@Param("driver") Driver driver,
                                      @Param("startDate") LocalDateTime startDate,
                                      @Param("endDate") LocalDateTime endDate);
```

### 8.8 Test Requirements

| Test Class | Min Scenarios |
|-----------|---------------|
| `DriverEarningsServiceImplTest` | Create earning record, calculate commission correctly, daily/weekly/monthly summaries, zero-ride summary |
| `PayoutServiceImplTest` | Request payout, reject over-balance, approve, reject, process with wallet transfer |
| `DriverEarningsControllerIntegrationTest` | GET earnings history, GET summary, POST payout request, admin approve/reject/process |

### 8.9 Acceptance Criteria

- [ ] `./mvnw test` passes
- [ ] Earning records are auto-created when rides end
- [ ] Commission calculation uses PlatformConfig rate
- [ ] Daily/weekly/monthly summaries work
- [ ] Payout request → approve → process flow works
- [ ] Admin payout management endpoints work
- [ ] Unit + integration tests
- [ ] Swagger docs

---

## 9. Cross-Feature Dependency Map

```
Feature 1 (Notifications)    ← independent, no deps
Feature 2 (Scheduled Rides)  ← independent, no deps
Feature 3 (Promo Codes)      ← independent, no deps
Feature 4 (Fare Estimation)  ← soft dep on Feature 3 (promo discount in estimate)
Feature 5 (Earnings)         ← independent, no deps
```

**Feature 4 ↔ Feature 3 integration:**
- Feature 4 includes a `promoDiscountStub` field (always 0.0) with a code comment marking the integration point.
- After both features merge to `main`, a small follow-up PR wires `promoCodeService.calculateDiscount()` into the fare estimation response.
- This is the ONLY cross-feature dependency. All others are fully independent.

**Post-merge integration PRs** (to be done after features merge):
1. Feature 1 + Feature 2: Add `RIDE_SCHEDULED`, `RIDE_RESCHEDULED`, `RIDE_DISPATCHED` notification events.
2. Feature 1 + Feature 5: Add `PAYOUT_REQUESTED`, `PAYOUT_PROCESSED` notification events.
3. Feature 3 + Feature 4: Wire promo discount calculation into fare estimation response.

---

## 10. Merge Order & Integration Plan

### Recommended merge order (least conflicts first):

1. **Feature 5 (Earnings)** — touches only `DriverServiceImpl.endRide()` and `AdminController`
2. **Feature 1 (Notifications)** — touches `RiderServiceImpl`, `DriverServiceImpl`, `PaymentServiceImpl`, `SecurityConfig`, `pom.xml`
3. **Feature 2 (Scheduled Rides)** — touches `RideRequest`, `RideRequestStatus`, `RiderController`, `AdminController`, `UbercloneappApplication`
4. **Feature 3 (Promo Codes)** — touches `RideRequest`, `Ride`, `RiderServiceImpl`, `User`, `AuthServiceImpl`
5. **Feature 4 (Fare Estimation)** — touches `RiderController`, `DistanceService`, `DistanceServiceOSRMImpl`

### After all merges:

- Run `./mvnw test` on `main`
- Run `./mvnw spring-boot:run` smoke test
- Tag `v1.1.0`
- Fast-forward `production` ← `v1.1.0`

---

*This document is the authoritative feature specification. If anything in this document conflicts with AGENTS.md or CLAUDE.md, this document takes precedence for feature implementation details. AGENTS.md takes precedence for project-wide conventions.*

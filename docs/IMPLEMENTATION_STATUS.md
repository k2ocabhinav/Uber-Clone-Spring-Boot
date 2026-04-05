# Implementation Status & Bug Report — v1.1.0

> **Purpose:** This document captures the current state of all 5 feature branches after their initial implementation. It lists every bug, missing piece, and test gap found during review. Any AI agent or developer can use this document to pick up the remaining work.
>
> **Reference:** See `docs/FEATURE_IMPLEMENTATION_PLAN.md` for the original specifications.

---

## Table of Contents

1. [Branch Status Overview](#1-branch-status-overview)
2. [Feature 5 — Driver Earnings Dashboard](#2-feature-5--driver-earnings-dashboard) (on main)
3. [Feature 2 — Scheduled Rides](#3-feature-2--scheduled-rides)
4. [Feature 4 — Fare Estimation & ETA Preview](#4-feature-4--fare-estimation--eta-preview)
5. [Feature 3 — Promo Code & Discount Engine](#5-feature-3--promo-code--discount-engine)
6. [Feature 1 — Real-Time Notifications](#6-feature-1--real-time-notifications)
7. [Missing Tests Summary](#7-missing-tests-summary)
8. [Merge Order & Checklist](#8-merge-order--checklist)
9. [Post-Merge Integration Tasks](#9-post-merge-integration-tasks)

---

## 1. Branch Status Overview

| # | Feature | Branch | Compiles | Feature Complete | Tests Pass | Security Issues | Merge-Ready |
|---|---------|--------|----------|-----------------|------------|-----------------|-------------|
| 5 | Driver Earnings | `main` (merged) | Yes | Yes | Yes | Minor | Already merged |
| 2 | Scheduled Rides | `feature/scheduled-rides` | **No** | Partial | **No** | None | **Blocked** |
| 4 | Fare Estimation | `feature/fare-estimation-eta` | Yes (no impl) | **No** | Yes (no new tests) | None | **Blocked** |
| 3 | Promo Discount | `main` (merged) | Yes | Yes | Yes | None | Already merged |
| 1 | Notifications | `feature/real-time-notifications` | Yes | Partial | Yes (no new tests) | **Major** | **Blocked** |

---

## 2. Feature 5 — Driver Earnings Dashboard

**Branch:** Already merged to `main` (commit `5ebd0d1`)
**Status:** Functionally complete, minor issues.

### Bugs

#### 2.1 APPROVED payouts not deducted from available balance
- **File:** `services/impl/DriverEarningsServiceImpl.java` — `getAvailableBalance()` method
- **Problem:** Only `PENDING` and `PROCESSED` payouts are deducted. `APPROVED` payouts (approved by admin but not yet processed) are not counted. A driver could request a second payout against funds already earmarked.
- **Fix:** Add `PayoutStatus.APPROVED` to the statuses list:
  ```java
  payoutRequestRepository.getTotalPayoutsByDriverAndStatuses(
      driver, PayoutStatus.PENDING, PayoutStatus.APPROVED, PayoutStatus.PROCESSED)
  ```

#### 2.2 Payout deposits to wallet, not bank
- **File:** `services/impl/PayoutServiceImpl.java` — `processPayout()` method
- **Problem:** `processPayout()` calls `walletService.addMoneyToWallet()`, which moves money within the app wallet. This is not a real bank payout. May be intentional for v1.1.0 scope, but should have a TODO or doc note.
- **Fix (optional):** Add a comment acknowledging this is a placeholder for bank integration.

### Missing Tests

- No controller integration tests for `DriverEarningsController` or the admin payout endpoints in `AdminController`.
- `DriverEarningsServiceImplTest` missing: weekly/monthly summary, `getAvailableBalance`, zero-balance edge case.
- `PayoutServiceImplTest` missing: `approvePayout`, `rejectPayout`, double-spend scenario via the APPROVED gap.

---

## 3. Feature 2 — Scheduled Rides

**Branch:** `feature/scheduled-rides` (4 commits ahead of main)
**Status:** Does not compile. Dispatch logic incomplete.

### Bugs

#### 3.1 CRITICAL — Compilation error: double to BigDecimal
- **File:** `services/impl/ScheduledRideServiceImpl.java`
- **Problem:** `RideFareCalculationStrategy.calculateFare()` returns `double`. The code assigns it directly to a `BigDecimal` variable, and `RideRequest.fare` was changed to `BigDecimal` on this branch. Java cannot auto-widen `double` to `BigDecimal`.
- **Fix:**
  ```java
  // Before (broken):
  BigDecimal fare = rideStrategyManager.rideFareCalculationStrategy().calculateFare(rideRequest);
  // After (fixed):
  double rawFare = rideStrategyManager.rideFareCalculationStrategy().calculateFare(rideRequest);
  BigDecimal fare = BigDecimal.valueOf(rawFare);
  ```

#### 3.2 CRITICAL — Test compilation error: mock type mismatch
- **File:** `test/.../ScheduledRideServiceImplTest.java`
- **Problem:** Tests mock `calculateFare()` to return `new BigDecimal("150.00")`, but the interface returns `double`.
- **Fix:** Change to `thenReturn(150.0)` (double literal).

#### 3.3 Dispatch never notifies matched drivers
- **File:** `services/impl/ScheduledRideServiceImpl.java` — `dispatchScheduledRides()` method
- **Problem:** The method finds matching drivers via `rideStrategyManager.driverMatchingStrategy(...)` and logs them, but does nothing with the result. No notification is sent, no ride offer is created, no driver is contacted. The dispatch is a no-op beyond changing status to `PENDING`.
- **Fix:** After finding `matchedDrivers`, either:
  - Publish a `RideRequestedEvent` (if notifications feature is merged) to notify drivers, OR
  - Comment with `// TODO: notify matched drivers when notification feature is integrated`
  - At minimum, the first matched driver should be associated with the ride request somehow.

#### 3.4 Timezone inconsistency
- **File:** `services/impl/ScheduledRideServiceImpl.java`
- **Problem:** `validateScheduledTime` uses `LocalDateTime.now(ZoneId.of("UTC"))`, but `scheduledTime` in `RideRequest` is a bare `LocalDateTime` (no timezone). If the server is not in UTC, scheduled times could be off by hours.
- **Fix:** Document that all times are UTC, or switch to `Instant`/`ZonedDateTime`.

#### 3.5 @Scheduled not properly disabled in tests
- **File:** `configs/ScheduledRideDispatcher.java`
- **Problem:** `spring.task.scheduling.enabled=false` in test properties does not actually disable `@Scheduled` in Spring Boot. The workaround sets a very long interval (`9999999ms`), which is fragile.
- **Fix:** Add `@ConditionalOnProperty(name = "app.scheduled-ride.dispatch-enabled", havingValue = "true", matchIfMissing = true)` on `ScheduledRideDispatcher`, and set `app.scheduled-ride.dispatch-enabled=false` in test properties.

#### 3.6 No cancellation time window
- **File:** `services/impl/ScheduledRideServiceImpl.java` — `cancelScheduledRide()`, `rescheduleRide()`
- **Problem:** Riders can cancel/reschedule up until the moment of dispatch. No minimum notice period.
- **Severity:** Low — can be added later.

### Missing Tests

- No controller integration tests for the 4 `RiderController` endpoints or the admin endpoint.
- No test for `ScheduledRideDispatcher` scheduling behavior.
- Existing dispatch tests don't verify what happens with matched drivers (because nothing happens).

---

## 4. Feature 4 — Fare Estimation & ETA Preview

**Branch:** `feature/fare-estimation-eta` (2 commits ahead of main)
**Status:** Feature is NOT implemented. Only a request DTO exists.

### Bugs

#### 4.1 CRITICAL — Feature not implemented
- **Problem:** The commit message claims to add `FareEstimateDto`, `FareEstimationService`, `FareEstimationServiceImpl`, and a controller endpoint. **None of these exist on the branch.** Only `FareEstimateRequestDto.java` was created — a dead DTO with no consumer.
- **What's missing (all must be created from scratch):**
  - `dto/FareEstimateDto.java` — response DTO
  - `services/FareEstimationService.java` — interface
  - `services/impl/FareEstimationServiceImpl.java` — implementation
  - `DistanceService.java` — add `calculateDistanceAndDuration()` method
  - `DistanceServiceOSRMImpl.java` — implement `calculateDistanceAndDuration()`
  - `RiderController.java` — add `POST /riders/fare-estimate` endpoint
  - Tests for all of the above
- **Reference:** See `docs/FEATURE_IMPLEMENTATION_PLAN.md` sections 7.3–7.7 for full specification.

#### 4.2 Misplaced PromoCode entity
- **Files on this branch:** `entities/PromoCode.java`, `entities/enums/DiscountType.java`
- **Problem:** These belong to the `feature/promo-discount-engine` branch but are committed here. They'll create a `promo_code` table on boot with no application code to manage it.
- **Fix:** These files should be removed from this branch. They exist correctly on `feature/promo-discount-engine`.

### Missing Tests

- Everything. The feature has no implementation to test.

---

## 5. Feature 3 — Promo Code & Discount Engine

**Branch:** Merged to `main` (commit `733c954`)
**Status:** Feature is fully implemented and tested.

### Completed Work
- **Promo Code Management:** Full CRUD for admin to create and manage promo codes.
- **Validation Engine:** Robust validation for expiry, usage limits, minimum fare, and rider eligibility.
- **Discount Calculation:** Support for both `FLAT` and `PERCENTAGE` discount types.
- **Usage Tracking:** Automatic persistence of usage records to prevent double-dipping.
- **Technical Debt:** Standardized fare calculations to `BigDecimal` across `RiderServiceImpl` and `PromoCodeService`.
- **Security:** Fixed `TestSecurityConfig` and `GlobalExceptionHandler` to properly handle role-based access and 403 errors.

### Resolved Bugs
- **Fare Type Mismatch:** All fare and discount values correctly use `BigDecimal`.
- **Service Integration:** Promo code logic is correctly wired into `RiderServiceImpl.requestRide()`.

### Tests
- `PromoCodeServiceImplTest`: 10 tests covering all business logic (create, validate, apply, usage limits, expiry).
- `PromoCodeControllerIntegrationTest`: 6 tests verifying REST endpoints and role-based security.
- All core service tests (`RiderServiceImplTest`, `DriverServiceImplTest`) updated and passing.

---

## 6. Feature 1 — Real-Time Notifications

**Branch:** `feature/real-time-notifications` (3 commits ahead of main)
**Status:** Most complete branch, but has critical security holes and missing WebSocket push.

### Bugs

#### 6.1 CRITICAL — WebSocket open to unauthenticated connections
- **File:** `configs/SecurityConfig.java`
- **Line:** `.requestMatchers("/ws/**").permitAll()`
- **File:** `configs/WebSocketAuthInterceptor.java`
- **Problem:** The interceptor logs a warning on auth failure but **still passes the message through**:
  ```java
  } catch (Exception e) {
      log.warn("WebSocket auth failed: {}", e.getMessage());
  }
  return message; // passes through even on auth failure
  ```
  An unauthenticated client can connect, subscribe to `/topic/**` destinations, and receive broadcast messages.
- **Fix:** On auth failure, throw `MessageDeliveryException` or return `null` to reject the STOMP frame. For CONNECT frames, return null to reject the connection.

#### 6.2 CRITICAL — WebSocketAuthInterceptor never registered
- **File:** `configs/WebSocketConfig.java`
- **Problem:** `WebSocketConfig` does not override `configureClientInboundChannel()` to register the interceptor. The `WebSocketAuthInterceptor` is a Spring bean that is never invoked by the message broker.
- **Fix:** Add to `WebSocketConfig`:
  ```java
  @Override
  public void configureClientInboundChannel(ChannelRegistration registration) {
      registration.interceptors(webSocketAuthInterceptor);
  }
  ```
  This requires injecting `WebSocketAuthInterceptor` into `WebSocketConfig`.

#### 6.3 CRITICAL — Notifications never pushed via WebSocket
- **File:** `services/impl/NotificationServiceImpl.java`
- **Problem:** `createNotification()` persists to the database but never sends via WebSocket. `SimpMessagingTemplate` is not injected and `convertAndSendToUser()` is never called. The "real-time" feature is REST-only — poll-based, not push.
- **Fix:** Inject `SimpMessagingTemplate` and add push after saving:
  ```java
  @RequiredArgsConstructor
  public class NotificationServiceImpl implements NotificationService {
      private final NotificationRepository notificationRepository;
      private final SimpMessagingTemplate messagingTemplate;
      // ...

      public NotificationDto createNotification(User user, String title, String message,
                                                 NotificationType type, Long referenceId) {
          // ... existing save logic ...
          NotificationDto dto = modelMapper.map(saved, NotificationDto.class);
          messagingTemplate.convertAndSendToUser(
              user.getId().toString(),
              "/queue/notifications",
              dto
          );
          return dto;
      }
  }
  ```

#### 6.4 markAsRead has no ownership check
- **File:** `services/impl/NotificationServiceImpl.java` — `markAsRead(Long notificationId)` method
- **Problem:** Any authenticated user can mark any notification as read by guessing an ID. No verification that the notification belongs to the caller.
- **Fix:** Accept the current user as a parameter and verify `notification.getUser().equals(currentUser)` before marking as read.

#### 6.5 NotificationController throws RuntimeException instead of ResourceNotFoundException
- **File:** `controllers/NotificationController.java` — `getCurrentUser()` method
- **Problem:** Throws plain `RuntimeException("User not found")` instead of `ResourceNotFoundException`. Bypasses `GlobalExceptionHandler` and returns HTTP 500.
- **Fix:** Change to `throw new ResourceNotFoundException("User not found with userId: " + userId)`.

#### 6.6 Unused JwtTokenProvider in WebSocketConfig
- **File:** `configs/WebSocketConfig.java`
- **Problem:** `JwtTokenProvider` is injected via `@RequiredArgsConstructor` but never used. The auth logic is in `WebSocketAuthInterceptor`.
- **Fix:** Remove the unused field.

#### 6.7 Drivers never receive notifications
- **File:** `events/NotificationEventListener.java`
- **Problem:** On `RideAccepted`, `RideStarted`, `RideEnded`, only the rider is notified. Drivers are never informed through this system. The spec (section 4.2) says "Driver gets notified about new ride requests, cancellations, and payments."
- **Fix:** In each event handler, create notifications for both the rider AND the driver (where applicable). For `PaymentProcessedEvent`, notify the driver too.

#### 6.8 CORS wildcard in production
- **File:** `configs/WebSocketConfig.java` — `setAllowedOriginPatterns("*")`
- **Problem:** Allows any origin to connect via WebSocket/SockJS. Should be locked down.
- **Severity:** Medium — acceptable for development, but must be configured per-environment before deployment.

### Missing Tests

- Zero tests for any notification feature code. Needed:
  - `NotificationServiceImplTest` — create, markAsRead with ownership check, markAllAsRead, getUnreadCount
  - `NotificationEventListenerTest` — one test per event type (6 events)
  - `NotificationControllerIntegrationTest` — all REST endpoints
  - WebSocket integration test (connection, subscription, push receipt)

---

## 7. Missing Tests Summary

| Branch | Missing Test Classes | Priority |
|--------|---------------------|----------|
| `main` (earnings) | `DriverEarningsControllerIntegrationTest`, expanded service tests | Medium |
| `feature/scheduled-rides` | `ScheduledRideControllerIntegrationTest`, dispatcher tests | High |
| `feature/fare-estimation-eta` | Everything (feature not implemented) | High |
| `feature/promo-discount-engine` | `PromoCodeServiceImplTest`, `PromoCodeControllerIntegrationTest` | High |
| `feature/real-time-notifications` | `NotificationServiceImplTest`, `NotificationEventListenerTest`, `NotificationControllerIntegrationTest` | High |

---

## 8. Merge Order & Checklist

Recommended merge order (fix all issues on each branch BEFORE merging):

### Step 1: `feature/scheduled-rides` -> `main`
- [ ] Fix BigDecimal compilation error in `ScheduledRideServiceImpl`
- [ ] Fix test mock type mismatch in `ScheduledRideServiceImplTest`
- [ ] Add `@ConditionalOnProperty` to `ScheduledRideDispatcher` for test isolation
- [ ] Add TODO comment in `dispatchScheduledRides()` about notifying matched drivers
- [ ] Add controller integration tests
- [ ] Verify: `cd ubercloneapp && ./mvnw test` passes
- [ ] Merge to main

### Step 2: `feature/fare-estimation-eta` -> `main`
- [ ] Remove misplaced `PromoCode.java` and `DiscountType.java` from this branch
- [ ] Implement `FareEstimateDto`, `FareEstimationService`, `FareEstimationServiceImpl`
- [ ] Add `calculateDistanceAndDuration()` to `DistanceService` and `DistanceServiceOSRMImpl`
- [ ] Add `POST /riders/fare-estimate` endpoint to `RiderController`
- [ ] Add unit tests (`FareEstimationServiceImplTest`)
- [ ] Add integration tests (`FareEstimationControllerIntegrationTest`)
- [ ] Rebase onto main (picks up scheduled-rides changes)
- [ ] Verify: `cd ubercloneapp && ./mvnw test` passes
- [ ] Merge to main

### Step 3: `feature/promo-discount-engine` -> `main`
- [x] Pop the stash: `git stash pop` (may contain missing implementation)
- [x] Implement `PromoCodeService`, `PromoCodeServiceImpl`, `PromoCodeController`
- [x] Add `PromoCodeResultDto`
- [x] Fix or move `FareEstimationServiceImplTest` (depends on classes from fare-estimation branch)
- [x] Add unit tests (`PromoCodeServiceImplTest`)
- [x] Add integration tests (`PromoCodeControllerIntegrationTest`)
- [x] Rebase onto main (picks up fare-estimation and scheduled-rides changes)
- [x] Verify: `cd ubercloneapp && ./mvnw test` passes
- [x] Merge to main

### Step 4: `feature/real-time-notifications` -> `main`
- [ ] Register `WebSocketAuthInterceptor` in `WebSocketConfig.configureClientInboundChannel()`
- [ ] Fix interceptor to reject unauthenticated STOMP connections (return null or throw)
- [ ] Inject `SimpMessagingTemplate` into `NotificationServiceImpl` and push notifications
- [ ] Add ownership check to `markAsRead()`
- [ ] Fix `NotificationController.getCurrentUser()` to throw `ResourceNotFoundException`
- [ ] Remove unused `JwtTokenProvider` from `WebSocketConfig`
- [ ] Add driver notifications in `NotificationEventListener` (both parties notified)
- [ ] Add unit tests (`NotificationServiceImplTest`, `NotificationEventListenerTest`)
- [ ] Add integration tests (`NotificationControllerIntegrationTest`)
- [ ] Rebase onto main (picks up all prior merges)
- [ ] Verify: `cd ubercloneapp && ./mvnw test` passes
- [ ] Merge to main

### Step 5: Post-merge on `main`
- [ ] Fix earnings APPROVED payout gap (section 2.1)
- [ ] Add missing earnings controller integration tests
- [ ] Run full test suite: `cd ubercloneapp && ./mvnw test`
- [ ] Smoke test: `cd ubercloneapp && ./mvnw spring-boot:run`
- [ ] Tag `v1.1.0`

---

## 9. Post-Merge Integration Tasks

These are follow-up PRs after all 5 features are on `main`:

1. **Notifications + Scheduled Rides:** Add `RIDE_SCHEDULED`, `RIDE_RESCHEDULED`, `RIDE_DISPATCHED` notification types. Publish events from `ScheduledRideServiceImpl`.
2. **Notifications + Earnings:** Add `PAYOUT_REQUESTED`, `PAYOUT_PROCESSED` notification types. Publish events from `PayoutServiceImpl`.
3. **Promo Codes + Fare Estimation:** Wire `promoCodeService.calculateDiscount()` into `FareEstimationServiceImpl` response (replace the stub `promoDiscountStub = 0.0`).
4. **Scheduled Rides + Notifications:** In `dispatchScheduledRides()`, publish `RideRequestedEvent` for matched drivers instead of just logging.

---

*Generated from code review on 2026-04-05. Reference the original feature specifications in `docs/FEATURE_IMPLEMENTATION_PLAN.md`.*

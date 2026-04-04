# Implementation Instructions: Uber Clone Backend Completion

## Branch: `feature/complete-backend-jwt-auth`

## Status: PARTIAL IMPLEMENTATION COMPLETE

The following tasks have been **COMPLETED** (do not redo):

### ✅ Already Implemented

**Phase 1: Critical Bugs - ALL COMPLETE**
- [x] `DriverRepository.java` - Fixed `FROM driver` → `FROM drivers`
- [x] `AuthServiceImpl.java` - Removed duplicate `riderService.createNewRider()` call
- [x] `WalletServiceImpl.java` - Implemented `withdrawAllMyMoneyFromWallet(User user)`
- [x] `WalletService.java` - Updated interface to match

**Phase 2: JWT Authentication - PARTIAL**
- [x] `pom.xml` - Added spring-boot-starter-security and JJWT dependencies
- [x] `application.properties` - Added `jwt.secret` and `jwt.expiration`
- [x] `JwtTokenProvider.java` - Created (token generation/validation)
- [x] `JwtAuthenticationFilter.java` - Created (extracts JWT from requests)
- [x] `UserPrincipal.java` - Created (SecurityContext principal)
- [x] `SecurityConfig.java` - Created (Spring Security + BCrypt config)
- [x] `AuthResponseDto.java` - Created (token response DTO)
- [x] `AuthService.java` - Updated interface to return `AuthResponseDto`
- [x] `AuthServiceImpl.java` - Implemented login with JWT + password encoding
- [ ] `AuthController.java` - **NEEDS LOGIN ENDPOINT** (see below)
- [ ] `RiderServiceImpl.java` - **NEEDS JWT user lookup** (see below)
- [ ] `DriverServiceImpl.java` - **NEEDS JWT user lookup** (see below)

---

## REMAINING TASKS FOR LOCAL AGENT

### Task 1: Complete AuthController with Login Endpoint
**File**: `controllers/AuthController.java`

**Add**:
```java
@PostMapping("/login")
ResponseEntity<AuthResponseDto> login(@RequestBody Map<String, String> credentials) {
    AuthResponseDto response = authService.login(credentials.get("email"), credentials.get("password"));
    return ResponseEntity.ok(response);
}
```

Import `AuthResponseDto` and `Map`.

---

### Task 2: Replace Hardcoded User IDs with JWT Lookup

**File**: `services/impl/RiderServiceImpl.java` - method `getCurrentRider()`

**Current code** (around line 122):
```java
// TODO: implement Spring security
return riderRepository.findById(1L).orElseThrow(...)
```

**Replace with**:
```java
import com.github.k2ocabhinav.ubercloneapp.security.UserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

// ... in getCurrentRider() method:
Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
Long userId = principal.getUserId();

Rider rider = riderRepository.findByUserId(userId)
    .orElseThrow(() -> new ResourceNotFoundException("Rider not found for user id: " + userId));
return rider;
```

Also need to add `findByUserId` to `RiderRepository.java`:
```java
Optional<Rider> findByUserId(Long userId);
```

---

**File**: `services/impl/DriverServiceImpl.java` - method `getCurrentDriver()`

**Current code** (around line 156):
```java
return driverRepository.findById(2L).orElseThrow(...)
```

**Replace with**:
```java
Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
Long userId = principal.getUserId();

Driver driver = driverRepository.findByUserId(userId)
    .orElseThrow(() -> new ResourceNotFoundException("Driver not found for user id: " + userId));
return driver;
```

Also need to add `findByUserId` to `DriverRepository.java`:
```java
Optional<Driver> findByUserId(Long userId);
```

---

### Task 3: Add Wallet Balance Validation
**File**: `strategies/impl/WalletPaymentStrategy.java`

**Add at start of `processPayment`**:
```java
if (wallet.getBalance() < payment.getAmount()) {
    throw new RuntimeConflictException("Insufficient wallet balance. Required: "
        + payment.getAmount() + ", Available: " + wallet.getBalance());
}
```

Import `RuntimeConflictException`.

---

### Task 4: Add Default Case to PaymentStrategyManager
**File**: `strategies/PaymentStrategyManager.java`

**Add default case**:
```java
public PaymentStrategy paymentStrategy(PaymentMethod paymentMethod) {
    return switch (paymentMethod) {
        case WALLET -> walletPaymentStrategy;
        case CASH -> cashPaymentStrategy;
        default -> throw new RuntimeConflictException("Unsupported payment method: " + paymentMethod);
    };
}
```

---

### Task 5: Add Driver Endpoints
**File**: `controllers/DriverController.java`

**Add two new endpoints**:
```java
@PutMapping("/updateLocation")
ResponseEntity<DriverDto> updateLocation(@RequestBody PointDto location) {
    Driver driver = driverService.getCurrentDriver();
    driver.setCurrentLocation(GeometryUtil.createPoint(location));
    driver = driverService.createNewDriver(driver);
    return ResponseEntity.ok(modelMapper.map(driver, DriverDto.class));
}

@GetMapping("/availableRideRequests")
ResponseEntity<List<RideRequestDto>> getAvailableRideRequests() {
    Driver driver = driverService.getCurrentDriver();
    List<RideRequest> requests = rideRequestService.findPendingRequestsNearLocation(
        driver.getCurrentLocation());
    return ResponseEntity.ok(requests.stream()
        .map(r -> modelMapper.map(r, RideRequestDto.class))
        .collect(Collectors.toList()));
}
```

**Note**: You may need to add `RideRequestRepository` with a method to find pending requests by location, or add a `RideRequestService` method.

---

### Task 6: (Optional) Add RiderRepository findByUserId
**File**: `repositories/RiderRepository.java`

**If not already present**:
```java
Optional<Rider> findByUserId(Long userId);
```

And update `entities/Rider.java` to ensure it has a `user` field that links to User entity.

---

## Phase 4: Modularization (Optional but Recommended)

These are for making the code more resume-worthy by showing architectural thinking:

### Task 7: Create Abstract Domain Classes
**New Package**: `domain/`

```java
// domain/ServiceProvider.java
@Entity
@DiscriminatorColumn(name = "provider_type")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class ServiceProvider {
    @Id
    Long id;
    Double rating;
    Boolean available;
    Point currentLocation;
    LocalDateTime createdAt;
}
```

```java
// domain/ServiceRequest.java
@Entity
@DiscriminatorColumn(name = "service_type")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class ServiceRequest {
    @Id
    Long id;
    Point pickupLocation;
    Point dropOffLocation;
    BigDecimal fare;
    LocalDateTime createdAt;
}
```

### Task 8: Create Config Classes
**New Files**:
- `configs/FareConfig.java` - surge hours, base fare, per-km rate
- `configs/PlatformConfig.java` - commission rate

### Task 9: Create Architecture Documentation
**New File**: `docs/ARCHITECTURE.md`

Document:
1. Project overview
2. How to add a new fare calculation strategy
3. How to add a new provider matching algorithm
4. Database schema overview

---

## Verification Checklist

After completing remaining tasks, verify:

- [ ] `./mvnw clean install` succeeds
- [ ] `./mvnw test` passes
- [ ] POST `/auth/signup` creates user with encoded password
- [ ] POST `/auth/login` returns JWT token
- [ ] Protected endpoints work with JWT in Authorization header
- [ ] High-rated rider requesting ride works (PostGIS fixed)
- [ ] Wallet payment fails gracefully with insufficient balance
- [ ] Driver can update location
- [ ] Driver can see available ride requests

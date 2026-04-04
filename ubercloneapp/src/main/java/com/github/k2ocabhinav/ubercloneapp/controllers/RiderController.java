package com.github.k2ocabhinav.ubercloneapp.controllers;

import com.github.k2ocabhinav.ubercloneapp.dto.*;
import com.github.k2ocabhinav.ubercloneapp.services.RiderService;
import com.github.k2ocabhinav.ubercloneapp.services.ScheduledRideService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/riders")
@Tag(name = "Rider", description = "Rider ride management")
@SecurityRequirement(name = "bearerAuth")
public class RiderController {

    private final RiderService riderService;
    private final ScheduledRideService scheduledRideService;

    @PostMapping(path = "/requestRide")
    @Operation(summary = "Request an immediate ride")
    public ResponseEntity<RideRequestDto> requestRide(@RequestBody RideRequestDto rideRequestDto){
        return ResponseEntity.ok(riderService.requestRide(rideRequestDto));
    }

    @PostMapping("/cancelRide/{rideId}")
    @Operation(summary = "Cancel an active ride")
    public ResponseEntity<RideDto> cancelRide(@PathVariable Long rideId) {
        return ResponseEntity.ok(riderService.cancelRide(rideId));
    }

    @PostMapping("/rateDriver")
    @Operation(summary = "Rate a driver after a ride")
    public ResponseEntity<DriverDto> rateDriver(@RequestBody RatingDto ratingDto) {
        return ResponseEntity.ok(riderService.rateDriver(ratingDto.getRideId(), ratingDto.getRating()));
    }

    @GetMapping("/getMyProfile")
    @Operation(summary = "Get the current rider's profile")
    public ResponseEntity<RiderDto> getMyProfile() {
        return ResponseEntity.ok(riderService.getMyProfile());
    }

    @GetMapping("/getMyRides")
    @Operation(summary = "Get paginated ride history")
    public ResponseEntity<Page<RideDto>> getAllMyRides(@RequestParam(defaultValue = "0") Integer pageOffset,
                                                       @RequestParam(defaultValue = "10", required = false) Integer pageSize) {
        PageRequest pageRequest = PageRequest.of(pageOffset, pageSize,
                Sort.by(Sort.Direction.DESC, "createdTime", "id"));
        return ResponseEntity.ok(riderService.getAllMyRides(pageRequest));
    }

    @PostMapping("/rateDriver/{rideId}/{rating}")
    @Operation(summary = "Rate a driver using path variables")
    public ResponseEntity<DriverDto> rateDriver(@PathVariable Long rideId, @PathVariable Integer rating) {
        return ResponseEntity.ok(riderService.rateDriver(rideId, rating));
    }

    @PostMapping("/schedule-ride")
    @Operation(summary = "Schedule a ride for a future time")
    public ResponseEntity<ScheduledRideDto> scheduleRide(@RequestBody RideRequestDto rideRequestDto) {
        return ResponseEntity.ok(scheduledRideService.scheduleRide(rideRequestDto));
    }

    @GetMapping("/scheduled-rides")
    @Operation(summary = "Get upcoming scheduled rides")
    public ResponseEntity<Page<ScheduledRideDto>> getMyScheduledRides(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return ResponseEntity.ok(scheduledRideService.getMyScheduledRides(pageRequest));
    }

    @PutMapping("/scheduled-rides/{id}/reschedule")
    @Operation(summary = "Reschedule a scheduled ride to a new time")
    public ResponseEntity<ScheduledRideDto> rescheduleRide(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime newTime) {
        return ResponseEntity.ok(scheduledRideService.rescheduleRide(id, newTime));
    }

    @DeleteMapping("/scheduled-rides/{id}")
    @Operation(summary = "Cancel a scheduled ride")
    public ResponseEntity<ScheduledRideDto> cancelScheduledRide(@PathVariable Long id) {
        return ResponseEntity.ok(scheduledRideService.cancelScheduledRide(id));
    }
}

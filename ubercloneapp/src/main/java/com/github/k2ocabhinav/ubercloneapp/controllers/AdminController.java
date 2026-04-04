package com.github.k2ocabhinav.ubercloneapp.controllers;

import com.github.k2ocabhinav.ubercloneapp.dto.admin.AdminStatsDto;
import com.github.k2ocabhinav.ubercloneapp.dto.admin.DashboardDto;
import com.github.k2ocabhinav.ubercloneapp.dto.admin.PendingDriverDto;
import com.github.k2ocabhinav.ubercloneapp.dto.admin.RevenueReportDto;
import com.github.k2ocabhinav.ubercloneapp.entities.User;
import com.github.k2ocabhinav.ubercloneapp.services.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
@Tag(name = "Admin", description = "Admin management and analytics")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/stats/dashboard")
    @Operation(summary = "Get dashboard overview")
    public ResponseEntity<DashboardDto> getDashboard() {
        return ResponseEntity.ok(adminService.getDashboard());
    }

    @GetMapping("/stats/daily")
    @Operation(summary = "Get daily statistics")
    public ResponseEntity<AdminStatsDto> getDailyStats(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(adminService.getDailyStats(date));
    }

    @GetMapping("/stats/revenue")
    @Operation(summary = "Get revenue report")
    public ResponseEntity<List<RevenueReportDto>> getRevenueReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(adminService.getRevenueReport(startDate, endDate));
    }

    @GetMapping("/drivers/pending")
    @Operation(summary = "Get pending driver approvals")
    public ResponseEntity<Page<PendingDriverDto>> getPendingDrivers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return ResponseEntity.ok(adminService.getPendingDrivers(pageRequest));
    }

    @PostMapping("/drivers/{id}/approve")
    @Operation(summary = "Approve a driver")
    public ResponseEntity<Void> approveDriver(@PathVariable Long id) {
        adminService.approveDriver(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/drivers/{id}/reject")
    @Operation(summary = "Reject a driver")
    public ResponseEntity<Void> rejectDriver(@PathVariable Long id, @RequestBody String reason) {
        adminService.rejectDriver(id, reason);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/users")
    @Operation(summary = "Get all users")
    public ResponseEntity<Page<User>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdTime"));
        return ResponseEntity.ok(adminService.getAllUsers(pageRequest));
    }

    @PostMapping("/users/{id}/deactivate")
    @Operation(summary = "Deactivate a user")
    public ResponseEntity<Void> deactivateUser(@PathVariable Long id) {
        adminService.deactivateUser(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/users/{id}/activate")
    @Operation(summary = "Activate a user")
    public ResponseEntity<Void> activateUser(@PathVariable Long id) {
        adminService.activateUser(id);
        return ResponseEntity.ok().build();
    }
}

package com.github.k2ocabhinav.ubercloneapp.services.impl;

import com.github.k2ocabhinav.ubercloneapp.dto.admin.AdminStatsDto;
import com.github.k2ocabhinav.ubercloneapp.dto.admin.DashboardDto;
import com.github.k2ocabhinav.ubercloneapp.dto.admin.PendingDriverDto;
import com.github.k2ocabhinav.ubercloneapp.dto.admin.RevenueReportDto;
import com.github.k2ocabhinav.ubercloneapp.entities.Driver;
import com.github.k2ocabhinav.ubercloneapp.entities.Payment;
import com.github.k2ocabhinav.ubercloneapp.entities.Ride;
import com.github.k2ocabhinav.ubercloneapp.entities.RideRequest;
import com.github.k2ocabhinav.ubercloneapp.entities.User;
import com.github.k2ocabhinav.ubercloneapp.entities.enums.PaymentStatus;
import com.github.k2ocabhinav.ubercloneapp.entities.enums.RideRequestStatus;
import com.github.k2ocabhinav.ubercloneapp.entities.enums.RideStatus;
import com.github.k2ocabhinav.ubercloneapp.exceptions.RuntimeConflictException;
import com.github.k2ocabhinav.ubercloneapp.repositories.*;
import com.github.k2ocabhinav.ubercloneapp.services.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final DriverRepository driverRepository;
    private final RideRepository rideRepository;
    private final PaymentRepository paymentRepository;
    private final RideRequestRepository rideRequestRepository;

    private static final double PLATFORM_COMMISSION = 0.30;

    @Override
    @Transactional(readOnly = true)
    public DashboardDto getDashboard() {
        long totalUsers = userRepository.count();
        long totalDrivers = driverRepository.count();
        long totalRides = rideRepository.count();
        
        List<Payment> confirmedPayments = paymentRepository.findByPaymentStatus(PaymentStatus.CONFIRMED);
        double totalRevenue = confirmedPayments.stream()
                .mapToDouble(Payment::getAmount)
                .sum();
        
        long activeRides = rideRepository.countByRideStatus(RideStatus.ONGOING);
        
        long pendingApprovals = driverRepository.findAll().stream()
                .filter(d -> d.getRating() == null || d.getRating() < 1.0)
                .count();
        
        Map<String, Long> ridesByStatus = new HashMap<>();
        ridesByStatus.put("COMPLETED", rideRepository.countByRideStatus(RideStatus.ENDED));
        ridesByStatus.put("CANCELLED", rideRepository.countByRideStatus(RideStatus.CANCELLED));
        ridesByStatus.put("ONGOING", activeRides);
        ridesByStatus.put("CONFIRMED", rideRepository.countByRideStatus(RideStatus.CONFIRMED));
        
        return DashboardDto.builder()
                .totalUsers(totalUsers)
                .totalDrivers(totalDrivers)
                .totalRides(totalRides)
                .totalRevenue(totalRevenue)
                .activeRides(activeRides)
                .pendingApprovals(pendingApprovals)
                .ridesByStatus(ridesByStatus)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public AdminStatsDto getDailyStats(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();
        
        List<Ride> dailyRides = rideRepository.findAll().stream()
                .filter(r -> {
                    LocalDateTime created = r.getCreatedTime();
                    return created != null && 
                           created.isAfter(startOfDay) && 
                           created.isBefore(endOfDay);
                })
                .toList();
        
        long totalRides = dailyRides.size();
        long completedRides = dailyRides.stream()
                .filter(r -> r.getRideStatus() == RideStatus.ENDED)
                .count();
        long cancelledRides = dailyRides.stream()
                .filter(r -> r.getRideStatus() == RideStatus.CANCELLED)
                .count();
        
        double totalRevenue = dailyRides.stream()
                .filter(r -> r.getFare() != null)
                .mapToDouble(Ride::getFare)
                .sum();
        
        double platformCommission = totalRevenue * PLATFORM_COMMISSION;
        long totalUsers = userRepository.count();
        long newUsers = userRepository.findAll().stream()
                .filter(u -> {
                    LocalDateTime created = u.getCreatedTime();
                    return created != null && 
                           created.isAfter(startOfDay) && 
                           created.isBefore(endOfDay);
                })
                .count();
        
        long activeDrivers = driverRepository.findAll().stream()
                .filter(Driver::getAvailable)
                .count();
        
        long pendingDriverApprovals = rideRequestRepository.findByRideRequestStatus(RideRequestStatus.PENDING).size();
        
        return AdminStatsDto.builder()
                .date(date)
                .totalRides(totalRides)
                .completedRides(completedRides)
                .cancelledRides(cancelledRides)
                .totalRevenue(totalRevenue)
                .platformCommission(platformCommission)
                .totalUsers(totalUsers)
                .newUsers(newUsers)
                .activeDrivers(activeDrivers)
                .pendingDriverApprovals(pendingDriverApprovals)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RevenueReportDto> getRevenueReport(LocalDate startDate, LocalDate endDate) {
        return startDate.datesUntil(endDate.plusDays(1))
                .map(date -> {
                    AdminStatsDto stats = getDailyStats(date);
                    return RevenueReportDto.builder()
                            .date(date)
                            .grossRevenue(stats.getTotalRevenue())
                            .platformCommission(stats.getPlatformCommission())
                            .driverEarnings(stats.getTotalRevenue() * (1 - PLATFORM_COMMISSION))
                            .rideCount(stats.getCompletedRides())
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PendingDriverDto> getPendingDrivers(Pageable pageable) {
        return driverRepository.findAll(pageable)
                .map(driver -> {
                    User user = driver.getUser();
                    return PendingDriverDto.builder()
                            .driverId(driver.getId())
                            .userId(user != null ? user.getId() : null)
                            .name(user != null ? user.getFirstName() + " " + user.getLastName() : "Unknown")
                            .email(user != null ? user.getEmail() : "Unknown")
                            .phone(user != null ? user.getPhoneNumber() : "Unknown")
                            .rating(driver.getRating())
                            .vehicleId(driver.getVehicleId())
                            .appliedAt(driver.getCreatedAt())
                            .build();
                });
    }

    @Override
    @Transactional
    public void approveDriver(Long driverId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeConflictException("Driver not found"));
        driver.setAvailable(true);
        driverRepository.save(driver);
    }

    @Override
    @Transactional
    public void rejectDriver(Long driverId, String reason) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeConflictException("Driver not found"));
        driver.setAvailable(false);
        driverRepository.save(driver);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    @Transactional
    public void deactivateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeConflictException("User not found"));
        user.setActive(true);  // Set to false to deactivate (Lombok interprets active=true as enabled, so set false to disable)
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void activateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeConflictException("User not found"));
        user.setActive(false);  // Set to true to activate
        userRepository.save(user);
    }
}

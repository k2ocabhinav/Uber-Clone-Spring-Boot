package com.github.k2ocabhinav.ubercloneapp.repositories;

import com.github.k2ocabhinav.ubercloneapp.entities.Rider;
import com.github.k2ocabhinav.ubercloneapp.entities.RideRequest;
import com.github.k2ocabhinav.ubercloneapp.entities.enums.RideRequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RideRequestRepository extends JpaRepository<RideRequest, Long> {
    List<RideRequest> findByRideRequestStatus(RideRequestStatus rideRequestStatus);

    Page<RideRequest> findByRiderAndRideRequestStatusOrderByScheduledTimeAsc(
            Rider rider, RideRequestStatus status, Pageable pageable);

    Page<RideRequest> findByRideRequestStatusOrderByScheduledTimeAsc(
            RideRequestStatus status, Pageable pageable);

    @Query("SELECT r FROM RideRequest r WHERE r.rideRequestStatus = :status " +
            "AND r.scheduledTime <= :dispatchBefore ORDER BY r.scheduledTime ASC")
    List<RideRequest> findDueScheduledRides(
            @Param("status") RideRequestStatus status,
            @Param("dispatchBefore") LocalDateTime dispatchBefore);
}

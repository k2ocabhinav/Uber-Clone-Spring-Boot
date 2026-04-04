package com.github.k2ocabhinav.ubercloneapp.repositories;

import com.github.k2ocabhinav.ubercloneapp.entities.Driver;
import com.github.k2ocabhinav.ubercloneapp.entities.PayoutRequest;
import com.github.k2ocabhinav.ubercloneapp.entities.enums.PayoutStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PayoutRequestRepository extends JpaRepository<PayoutRequest, Long> {
    Page<PayoutRequest> findByDriverOrderByRequestedAtDesc(Driver driver, Pageable pageable);
    
    Page<PayoutRequest> findByStatusOrderByRequestedAtAsc(PayoutStatus status, Pageable pageable);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM PayoutRequest p WHERE p.driver = :driver AND p.status IN (:status1, :status2)")
    Double getTotalPayoutsByDriverAndStatuses(@Param("driver") Driver driver, 
                                              @Param("status1") PayoutStatus status1, 
                                              @Param("status2") PayoutStatus status2);
}

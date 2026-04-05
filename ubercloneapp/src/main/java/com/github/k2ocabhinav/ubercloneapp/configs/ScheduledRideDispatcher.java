package com.github.k2ocabhinav.ubercloneapp.configs;

import com.github.k2ocabhinav.ubercloneapp.services.ScheduledRideService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "app.scheduled-ride.dispatch-enabled", havingValue = "true", matchIfMissing = true)
public class ScheduledRideDispatcher {

    private final ScheduledRideService scheduledRideService;

    @Scheduled(fixedRateString = "${app.scheduled-ride.dispatch-interval-ms:60000}")
    public void dispatchDueRides() {
        int dispatched = scheduledRideService.dispatchScheduledRides();
        if (dispatched > 0) {
            log.info("Dispatcher cycle complete: {} rides dispatched", dispatched);
        }
    }
}

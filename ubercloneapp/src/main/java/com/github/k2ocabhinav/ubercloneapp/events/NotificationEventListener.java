package com.github.k2ocabhinav.ubercloneapp.events;

import com.github.k2ocabhinav.ubercloneapp.entities.Driver;
import com.github.k2ocabhinav.ubercloneapp.entities.Rider;
import com.github.k2ocabhinav.ubercloneapp.entities.enums.NotificationType;
import com.github.k2ocabhinav.ubercloneapp.services.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationEventListener {

    private final NotificationService notificationService;

    @org.springframework.context.event.EventListener
    public void handleRideRequested(RideRequestedEvent event) {
        Rider rider = event.getRideRequest().getRider();
        notificationService.createNotification(
                rider.getUser(),
                "Ride Requested",
                "Your ride request has been submitted. Finding drivers nearby...",
                NotificationType.RIDE_REQUESTED,
                event.getRideRequest().getId()
        );
        log.debug("Notification created for ride request: {}", event.getRideRequest().getId());
    }

    @org.springframework.context.event.EventListener
    public void handleRideAccepted(RideAcceptedEvent event) {
        Rider rider = event.getRide().getRider();
        Driver driver = event.getDriver();
        notificationService.createNotification(
                rider.getUser(),
                "Driver Accepted",
                "Driver " + driver.getUser().getFirstName() + " has accepted your ride request.",
                NotificationType.RIDE_ACCEPTED,
                event.getRide().getId()
        );
        log.debug("Notification created for ride accepted: {}", event.getRide().getId());
    }

    @org.springframework.context.event.EventListener
    public void handleRideStarted(RideStartedEvent event) {
        Rider rider = event.getRide().getRider();
        notificationService.createNotification(
                rider.getUser(),
                "Ride Started",
                "Your ride has started. Enjoy your trip!",
                NotificationType.RIDE_STARTED,
                event.getRide().getId()
        );
        log.debug("Notification created for ride started: {}", event.getRide().getId());
    }

    @org.springframework.context.event.EventListener
    public void handleRideEnded(RideEndedEvent event) {
        Rider rider = event.getRide().getRider();
        notificationService.createNotification(
                rider.getUser(),
                "Ride Ended",
                "Your ride has ended. Thank you for traveling with us!",
                NotificationType.RIDE_ENDED,
                event.getRide().getId()
        );
        log.debug("Notification created for ride ended: {}", event.getRide().getId());
    }

    @org.springframework.context.event.EventListener
    public void handleRideCancelled(RideCancelledEvent event) {
        Rider rider = event.getRide().getRider();
        notificationService.createNotification(
                rider.getUser(),
                "Ride Cancelled",
                "Your ride has been cancelled.",
                NotificationType.RIDE_CANCELLED,
                event.getRide().getId()
        );
        log.debug("Notification created for ride cancelled: {}", event.getRide().getId());
    }

    @org.springframework.context.event.EventListener
    public void handlePaymentProcessed(PaymentProcessedEvent event) {
        Rider rider = event.getPayment().getRide().getRider();
        notificationService.createNotification(
                rider.getUser(),
                "Payment Processed",
                "Payment of $" + event.getPayment().getAmount() + " has been processed.",
                NotificationType.PAYMENT_PROCESSED,
                event.getPayment().getId()
        );
        log.debug("Notification created for payment processed: {}", event.getPayment().getId());
    }
}

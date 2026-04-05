package com.github.k2ocabhinav.ubercloneapp.events;

import com.github.k2ocabhinav.ubercloneapp.entities.RideRequest;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class RideRequestedEvent extends ApplicationEvent {

    private final RideRequest rideRequest;

    public RideRequestedEvent(Object source, RideRequest rideRequest) {
        super(source);
        this.rideRequest = rideRequest;
    }
}

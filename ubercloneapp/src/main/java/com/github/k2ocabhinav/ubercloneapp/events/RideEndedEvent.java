package com.github.k2ocabhinav.ubercloneapp.events;

import com.github.k2ocabhinav.ubercloneapp.entities.Ride;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class RideEndedEvent extends ApplicationEvent {

    private final Ride ride;

    public RideEndedEvent(Object source, Ride ride) {
        super(source);
        this.ride = ride;
    }
}

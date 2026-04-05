package com.github.k2ocabhinav.ubercloneapp.events;

import com.github.k2ocabhinav.ubercloneapp.entities.Ride;
import com.github.k2ocabhinav.ubercloneapp.entities.Driver;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class RideAcceptedEvent extends ApplicationEvent {

    private final Ride ride;
    private final Driver driver;

    public RideAcceptedEvent(Object source, Ride ride, Driver driver) {
        super(source);
        this.ride = ride;
        this.driver = driver;
    }
}

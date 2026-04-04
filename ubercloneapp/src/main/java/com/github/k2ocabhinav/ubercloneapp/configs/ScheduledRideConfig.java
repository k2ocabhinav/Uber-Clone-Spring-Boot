package com.github.k2ocabhinav.ubercloneapp.configs;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.scheduled-ride")
public class ScheduledRideConfig {
    private int minAdvanceMinutes = 30;
    private int maxAdvanceDays = 7;
    private int dispatchWindowMinutes = 15;
}

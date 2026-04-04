package com.github.k2ocabhinav.ubercloneapp.configs;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.platform")
public class PlatformConfig {
    private double commissionRate = 0.30;
    private double driverRatingThreshold = 4.8;
    
    public double getCommissionRate() { return commissionRate; }
    public void setCommissionRate(double commissionRate) { this.commissionRate = commissionRate; }
    public double getDriverRatingThreshold() { return driverRatingThreshold; }
    public void setDriverRatingThreshold(double threshold) { this.driverRatingThreshold = threshold; }
}

package com.github.k2ocabhinav.ubercloneapp.configs;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.fare")
public class FareConfig {
    private double baseFare = 5.0;
    private double perKmRate = 10.0;
    private double surgeStartHour = 18;
    private double surgeEndHour = 21;
    private double surgeMultiplier = 2.0;
    private double highRatedRiderThreshold = 4.8;
    
    public double getBaseFare() { return baseFare; }
    public void setBaseFare(double baseFare) { this.baseFare = baseFare; }
    public double getPerKmRate() { return perKmRate; }
    public void setPerKmRate(double perKmRate) { this.perKmRate = perKmRate; }
    public double getSurgeStartHour() { return surgeStartHour; }
    public void setSurgeStartHour(double surgeStartHour) { this.surgeStartHour = surgeStartHour; }
    public double getSurgeEndHour() { return surgeEndHour; }
    public void setSurgeEndHour(double surgeEndHour) { this.surgeEndHour = surgeEndHour; }
    public double getSurgeMultiplier() { return surgeMultiplier; }
    public void setSurgeMultiplier(double surgeMultiplier) { this.surgeMultiplier = surgeMultiplier; }
    public double getHighRatedRiderThreshold() { return highRatedRiderThreshold; }
    public void setHighRatedRiderThreshold(double threshold) { this.highRatedRiderThreshold = threshold; }
}

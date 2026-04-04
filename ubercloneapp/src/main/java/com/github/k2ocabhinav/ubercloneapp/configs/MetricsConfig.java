package com.github.k2ocabhinav.ubercloneapp.configs;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.atomic.AtomicLong;

@Configuration
public class MetricsConfig {

    @Bean
    public Timer rideRequestTimer(MeterRegistry registry) {
        return Timer.builder("rides_requested")
                .description("Time taken to process ride requests")
                .register(registry);
    }

    @Bean
    public Counter ridesRequestedCounter(MeterRegistry registry) {
        return Counter.builder("rides_requested_total")
                .description("Total number of rides requested")
                .register(registry);
    }

    @Bean
    public Counter ridesCompletedCounter(MeterRegistry registry) {
        return Counter.builder("rides_completed_total")
                .description("Total number of completed rides")
                .register(registry);
    }

    @Bean
    public Counter ridesCancelledCounter(MeterRegistry registry) {
        return Counter.builder("rides_cancelled_total")
                .description("Total number of cancelled rides")
                .register(registry);
    }

    @Bean
    public Counter paymentsProcessedCounter(MeterRegistry registry) {
        return Counter.builder("payments_processed_total")
                .description("Total number of payments processed")
                .register(registry);
    }

    @Bean
    public Counter paymentsFailedCounter(MeterRegistry registry) {
        return Counter.builder("payments_failed_total")
                .description("Total number of failed payments")
                .register(registry);
    }

    @Bean
    public AtomicLong activeRidesGauge(MeterRegistry registry) {
        AtomicLong activeRides = new AtomicLong(0);
        Gauge.builder("active_rides", activeRides, AtomicLong::get)
                .description("Currently active rides")
                .register(registry);
        return activeRides;
    }

    @Bean
    public AtomicLong availableDriversGauge(MeterRegistry registry) {
        AtomicLong availableDrivers = new AtomicLong(0);
        Gauge.builder("available_drivers", availableDrivers, AtomicLong::get)
                .description("Number of available drivers")
                .register(registry);
        return availableDrivers;
    }
}
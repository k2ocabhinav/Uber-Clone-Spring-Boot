package com.github.k2ocabhinav.ubercloneapp.actuator;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;

@Component("application")
public class ApplicationHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        long heapUsed = memoryBean.getHeapMemoryUsage().getUsed();
        long heapMax = memoryBean.getHeapMemoryUsage().getMax();
        double heapPercent = (double) heapUsed / heapMax * 100;
        
        Health.Builder builder = Health.up()
                .withDetail("javaVersion", System.getProperty("java.version"))
                .withDetail("uptime", ManagementFactory.getRuntimeMXBean().getUptime() / 1000 + "s")
                .withDetail("heapUsed", formatBytes(heapUsed))
                .withDetail("heapPercent", String.format("%.2f%%", heapPercent));
        
        if (heapPercent > 90) {
            builder = Health.down();
        }
        
        return builder.build();
    }
    
    private String formatBytes(long bytes) {
        long mb = bytes / (1024 * 1024);
        return mb + " MB";
    }
}
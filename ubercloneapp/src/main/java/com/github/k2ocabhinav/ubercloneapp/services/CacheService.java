package com.github.k2ocabhinav.ubercloneapp.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class CacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String DRIVER_LOCATION_PREFIX = "driver:location:";
    private static final String AVAILABLE_DRIVERS_KEY = "drivers:available";
    private static final String DASHBOARD_CACHE_KEY = "admin:dashboard";
    private static final Duration DEFAULT_TTL = Duration.ofMinutes(5);

    public void cacheDriverLocation(Long driverId, Object location) {
        redisTemplate.opsForValue().set(
                DRIVER_LOCATION_PREFIX + driverId, 
                location, 
                Duration.ofSeconds(30));
    }

    public Object getDriverLocation(Long driverId) {
        return redisTemplate.opsForValue().get(DRIVER_LOCATION_PREFIX + driverId);
    }

    public void cacheAvailableDriversCount(Long count) {
        redisTemplate.opsForValue().set(AVAILABLE_DRIVERS_KEY, count, DEFAULT_TTL);
    }

    public Long getAvailableDriversCount() {
        Object count = redisTemplate.opsForValue().get(AVAILABLE_DRIVERS_KEY);
        return count != null ? ((Number) count).longValue() : null;
    }

    public void cacheDashboard(Object dashboard) {
        redisTemplate.opsForValue().set(DASHBOARD_CACHE_KEY, dashboard, DEFAULT_TTL);
    }

    public Object getCachedDashboard() {
        return redisTemplate.opsForValue().get(DASHBOARD_CACHE_KEY);
    }

    public void evictDashboardCache() {
        redisTemplate.delete(DASHBOARD_CACHE_KEY);
    }

    public void evictDriverLocation(Long driverId) {
        redisTemplate.delete(DRIVER_LOCATION_PREFIX + driverId);
    }

    public void evictAvailableDriversCache() {
        redisTemplate.delete(AVAILABLE_DRIVERS_KEY);
    }
}

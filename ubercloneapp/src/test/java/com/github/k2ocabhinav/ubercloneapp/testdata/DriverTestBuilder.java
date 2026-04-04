package com.github.k2ocabhinav.ubercloneapp.testdata;

import com.github.k2ocabhinav.ubercloneapp.entities.Driver;
import com.github.k2ocabhinav.ubercloneapp.entities.User;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;

import java.time.LocalDateTime;

public class DriverTestBuilder {
    private static final GeometryFactory GF = new GeometryFactory(new PrecisionModel(), 4326);

    private Long id = 1L;
    private User user;
    private String vehicleId = "ABC123";
    private Double rating = 5.0;
    private Boolean available = true;
    private Boolean active = true;
    private LocalDateTime createdAt = LocalDateTime.now();
    private Point currentLocation = GF.createPoint(new Coordinate(-73.935242, 40.730610));

    public DriverTestBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public DriverTestBuilder withUser(User user) {
        this.user = user;
        return this;
    }

    public DriverTestBuilder withVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
        return this;
    }

    public DriverTestBuilder withRating(Double rating) {
        this.rating = rating;
        return this;
    }

    public DriverTestBuilder available() {
        this.available = true;
        return this;
    }

    public DriverTestBuilder unavailable() {
        this.available = false;
        return this;
    }

    public DriverTestBuilder active() {
        this.active = true;
        return this;
    }

    public DriverTestBuilder inactive() {
        this.active = false;
        return this;
    }

    public DriverTestBuilder withCurrentLocation(double lon, double lat) {
        this.currentLocation = GF.createPoint(new Coordinate(lon, lat));
        return this;
    }

    public Driver build() {
        return Driver.builder()
                .id(id)
                .user(user)
                .vehicleId(vehicleId)
                .rating(rating)
                .available(available)
                .active(active)
                .createdAt(createdAt)
                .currentLocation(currentLocation)
                .build();
    }

    public static DriverTestBuilder aDriver() {
        return new DriverTestBuilder();
    }

    public static DriverTestBuilder anAvailableDriver() {
        return aDriver().available();
    }

    public static DriverTestBuilder anUnavailableDriver() {
        return aDriver().unavailable();
    }
}

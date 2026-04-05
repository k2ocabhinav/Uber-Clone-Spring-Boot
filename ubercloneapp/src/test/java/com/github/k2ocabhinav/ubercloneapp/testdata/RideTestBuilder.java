package com.github.k2ocabhinav.ubercloneapp.testdata;

import com.github.k2ocabhinav.ubercloneapp.entities.Ride;
import com.github.k2ocabhinav.ubercloneapp.entities.Driver;
import com.github.k2ocabhinav.ubercloneapp.entities.Rider;
import com.github.k2ocabhinav.ubercloneapp.entities.enums.PaymentMethod;
import com.github.k2ocabhinav.ubercloneapp.entities.enums.RideStatus;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class RideTestBuilder {
    private static final GeometryFactory GF = new GeometryFactory(new PrecisionModel(), 4326);

    private Long id = 1L;
    private Point pickupLocation = GF.createPoint(new Coordinate(-73.935242, 40.730610));
    private Point dropOffLocation = GF.createPoint(new Coordinate(-73.935242, 40.740610));
    private LocalDateTime createdTime = LocalDateTime.now();
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private Rider rider;
    private Driver driver;
    private PaymentMethod paymentMethod = PaymentMethod.WALLET;
    private RideStatus rideStatus = RideStatus.CONFIRMED;
    private BigDecimal fare = BigDecimal.valueOf(50.0);
    private String otp = "1234";

    public RideTestBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public RideTestBuilder withPickupLocation(double lon, double lat) {
        this.pickupLocation = GF.createPoint(new Coordinate(lon, lat));
        return this;
    }

    public RideTestBuilder withDropOffLocation(double lon, double lat) {
        this.dropOffLocation = GF.createPoint(new Coordinate(lon, lat));
        return this;
    }

    public RideTestBuilder withCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
        return this;
    }

    public RideTestBuilder withStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
        return this;
    }

    public RideTestBuilder withEndedAt(LocalDateTime endedAt) {
        this.endedAt = endedAt;
        return this;
    }

    public RideTestBuilder withRider(Rider rider) {
        this.rider = rider;
        return this;
    }

    public RideTestBuilder withDriver(Driver driver) {
        this.driver = driver;
        return this;
    }

    public RideTestBuilder withPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
        return this;
    }

    public RideTestBuilder withStatus(RideStatus rideStatus) {
        this.rideStatus = rideStatus;
        return this;
    }

    public RideTestBuilder withFare(BigDecimal fare) {
        this.fare = fare;
        return this;
    }

    public RideTestBuilder withFare(Double fare) {
        this.fare = BigDecimal.valueOf(fare);
        return this;
    }

    public RideTestBuilder withOtp(String otp) {
        this.otp = otp;
        return this;
    }

    public Ride build() {
        return Ride.builder()
                .id(id)
                .pickupLocation(pickupLocation)
                .dropOffLocation(dropOffLocation)
                .createdTime(createdTime)
                .startedAt(startedAt)
                .endedAt(endedAt)
                .rider(rider)
                .driver(driver)
                .paymentMethod(paymentMethod)
                .rideStatus(rideStatus)
                .fare(fare)
                .otp(otp)
                .build();
    }

    public static RideTestBuilder aRide() {
        return new RideTestBuilder();
    }

    public static RideTestBuilder aConfirmedRide() {
        return aRide().withStatus(RideStatus.CONFIRMED);
    }

    public static RideTestBuilder anOngoingRide() {
        return aRide().withStatus(RideStatus.ONGOING);
    }

    public static RideTestBuilder aCompletedRide() {
        return aRide().withStatus(RideStatus.ENDED);
    }
}

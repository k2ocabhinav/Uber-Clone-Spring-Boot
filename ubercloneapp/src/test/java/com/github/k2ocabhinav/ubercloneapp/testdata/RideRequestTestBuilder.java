package com.github.k2ocabhinav.ubercloneapp.testdata;

import com.github.k2ocabhinav.ubercloneapp.entities.RideRequest;
import com.github.k2ocabhinav.ubercloneapp.entities.Rider;
import com.github.k2ocabhinav.ubercloneapp.entities.enums.PaymentMethod;
import com.github.k2ocabhinav.ubercloneapp.entities.enums.RideRequestStatus;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class RideRequestTestBuilder {
    private Long id;
    private Point pickupLocation;
    private Point dropOffLocation;
    private LocalDateTime requestedTime = LocalDateTime.now();
    private LocalDateTime scheduledTime;
    private Rider rider;
    private PaymentMethod paymentMethod = PaymentMethod.WALLET;
    private RideRequestStatus rideRequestStatus = RideRequestStatus.PENDING;
    private BigDecimal fare = BigDecimal.valueOf(50.0);

    public RideRequestTestBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public RideRequestTestBuilder withPickupLocation(double x, double y) {
        GeometryFactory factory = new GeometryFactory(new PrecisionModel(), 4326);
        this.pickupLocation = factory.createPoint(new Coordinate(x, y));
        return this;
    }

    public RideRequestTestBuilder withDropOffLocation(double x, double y) {
        GeometryFactory factory = new GeometryFactory(new PrecisionModel(), 4326);
        this.dropOffLocation = factory.createPoint(new Coordinate(x, y));
        return this;
    }

    public RideRequestTestBuilder withScheduledTime(LocalDateTime scheduledTime) {
        this.scheduledTime = scheduledTime;
        return this;
    }

    public RideRequestTestBuilder withRider(Rider rider) {
        this.rider = rider;
        return this;
    }

    public RideRequestTestBuilder withStatus(RideRequestStatus rideRequestStatus) {
        this.rideRequestStatus = rideRequestStatus;
        return this;
    }

    public RideRequestTestBuilder withFare(Double fare) {
        this.fare = BigDecimal.valueOf(fare);
        return this;
    }

    public RideRequestTestBuilder withFare(BigDecimal fare) {
        this.fare = fare;
        return this;
    }

    public RideRequest build() {
        if (pickupLocation == null) withPickupLocation(77.1, 28.1);
        if (dropOffLocation == null) withDropOffLocation(77.2, 28.2);

        return RideRequest.builder()
                .id(id)
                .pickupLocation(pickupLocation)
                .dropOffLocation(dropOffLocation)
                .requestedTime(requestedTime)
                .scheduledTime(scheduledTime)
                .rider(rider)
                .paymentMethod(paymentMethod)
                .rideRequestStatus(rideRequestStatus)
                .fare(fare)
                .build();
    }

    public static RideRequestTestBuilder aRideRequest() {
        return new RideRequestTestBuilder();
    }
}

package com.github.k2ocabhinav.ubercloneapp.testdata;

import com.github.k2ocabhinav.ubercloneapp.entities.RideRequest;
import com.github.k2ocabhinav.ubercloneapp.entities.Rider;
import com.github.k2ocabhinav.ubercloneapp.entities.enums.PaymentMethod;
import com.github.k2ocabhinav.ubercloneapp.entities.enums.RideRequestStatus;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;

import java.time.LocalDateTime;

public class RideRequestTestBuilder {
    private static final GeometryFactory GF = new GeometryFactory(new PrecisionModel(), 4326);

    private Long id = 1L;
    private Point pickupLocation = GF.createPoint(new Coordinate(-73.935242, 40.730610));
    private Point dropOffLocation = GF.createPoint(new Coordinate(-73.935242, 40.740610));
    private LocalDateTime requestedTime = LocalDateTime.now();
    private Rider rider;
    private PaymentMethod paymentMethod = PaymentMethod.WALLET;
    private RideRequestStatus rideRequestStatus = RideRequestStatus.PENDING;
    private Double fare = 50.0;

    public RideRequestTestBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public RideRequestTestBuilder withPickupLocation(double lon, double lat) {
        this.pickupLocation = GF.createPoint(new Coordinate(lon, lat));
        return this;
    }

    public RideRequestTestBuilder withDropOffLocation(double lon, double lat) {
        this.dropOffLocation = GF.createPoint(new Coordinate(lon, lat));
        return this;
    }

    public RideRequestTestBuilder withRequestedTime(LocalDateTime requestedTime) {
        this.requestedTime = requestedTime;
        return this;
    }

    public RideRequestTestBuilder withRider(Rider rider) {
        this.rider = rider;
        return this;
    }

    public RideRequestTestBuilder withPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
        return this;
    }

    public RideRequestTestBuilder withStatus(RideRequestStatus rideRequestStatus) {
        this.rideRequestStatus = rideRequestStatus;
        return this;
    }

    public RideRequestTestBuilder withFare(Double fare) {
        this.fare = fare;
        return this;
    }

    public RideRequest build() {
        return RideRequest.builder()
                .id(id)
                .pickupLocation(pickupLocation)
                .dropOffLocation(dropOffLocation)
                .requestedTime(requestedTime)
                .rider(rider)
                .paymentMethod(paymentMethod)
                .rideRequestStatus(rideRequestStatus)
                .fare(fare)
                .build();
    }

    public static RideRequestTestBuilder aRideRequest() {
        return new RideRequestTestBuilder();
    }

    public static RideRequestTestBuilder aPendingRideRequest() {
        return aRideRequest().withStatus(RideRequestStatus.PENDING);
    }

    public static RideRequestTestBuilder anAcceptedRideRequest() {
        return aRideRequest().withStatus(RideRequestStatus.ACCEPTED);
    }
}

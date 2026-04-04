package com.github.k2ocabhinav.ubercloneapp.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(indexes = {
        @Index(name = "idx_rating_rider", columnList = "rider_id"),
        @Index(name = "idx_rating_driver", columnList = "driver_id"),
        @Index(name = "idx_rating_ride", columnList = "ride_id")
})
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Exclude
    @OneToOne(fetch = FetchType.LAZY)
    private Ride ride;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    private Rider rider;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    private Driver driver;

    private Integer driverRating; //rating for the driver
    private Integer riderRating; //rating for the rider

    @Version
    private Long version;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Rating that)) return false;
        return id != null && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
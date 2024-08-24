package com.github.k2ocabhinav.ubercloneapp.repositories;

import com.github.k2ocabhinav.ubercloneapp.entities.Driver;
import com.github.k2ocabhinav.ubercloneapp.entities.Rating;
import com.github.k2ocabhinav.ubercloneapp.entities.Ride;
import com.github.k2ocabhinav.ubercloneapp.entities.Rider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    List<Rating> findByRider(Rider rider);
    List<Rating> findByDriver(Driver driver);

    Optional<Rating> findByRide(Ride ride);
}

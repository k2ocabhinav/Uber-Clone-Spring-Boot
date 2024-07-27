package com.github.k2ocabhinav.ubercloneapp.repositories;

import com.github.k2ocabhinav.ubercloneapp.entities.Driver;
import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {

    /*
     * ST_Distance(Point1, Point2)
     * ST_DWithin(Point1, 10000)
     * */
    @Query(value = "SELECT d.*, ST_Distance(d.current_location, :pickupLocation) AS distance " +
            "FROM drivers AS d " +
            "WHERE d.available = true AND ST_DWithin(d.current_location, :pickup_location, 10000) " +
            "ORDER BY distance " +
            "LIMIT 10",
            nativeQuery = true
    )
    List<Driver> findTenNearestDrivers(Point pickupLocation);
}

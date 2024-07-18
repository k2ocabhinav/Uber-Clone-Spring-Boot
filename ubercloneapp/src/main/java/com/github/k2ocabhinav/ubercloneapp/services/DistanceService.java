package com.github.k2ocabhinav.ubercloneapp.services;

import org.locationtech.jts.geom.Point;

public interface DistanceService {
    /*
    * Using OSRM API for distance calculation
    */
    double calculateDistance(Point src, Point dest);
}

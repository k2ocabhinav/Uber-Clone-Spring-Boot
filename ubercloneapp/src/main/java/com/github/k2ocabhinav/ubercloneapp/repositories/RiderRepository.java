package com.github.k2ocabhinav.ubercloneapp.repositories;

import com.github.k2ocabhinav.ubercloneapp.entities.Rider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RiderRepository extends JpaRepository<Rider, Long> {
}

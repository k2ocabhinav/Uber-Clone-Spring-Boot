package com.github.k2ocabhinav.ubercloneapp.repositories;

import com.github.k2ocabhinav.ubercloneapp.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}

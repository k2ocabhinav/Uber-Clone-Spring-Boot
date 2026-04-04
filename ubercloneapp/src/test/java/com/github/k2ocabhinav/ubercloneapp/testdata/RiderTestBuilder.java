package com.github.k2ocabhinav.ubercloneapp.testdata;

import com.github.k2ocabhinav.ubercloneapp.entities.Rider;
import com.github.k2ocabhinav.ubercloneapp.entities.User;

public class RiderTestBuilder {
    private Long id = 1L;
    private User user;
    private Double rating = 5.0;

    public RiderTestBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public RiderTestBuilder withUser(User user) {
        this.user = user;
        return this;
    }

    public RiderTestBuilder withRating(Double rating) {
        this.rating = rating;
        return this;
    }

    public Rider build() {
        return Rider.builder()
                .id(id)
                .user(user)
                .rating(rating)
                .build();
    }

    public static RiderTestBuilder aRider() {
        return new RiderTestBuilder();
    }
}

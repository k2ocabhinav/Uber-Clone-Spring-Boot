package com.github.k2ocabhinav.ubercloneapp.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.security.Principal;

@Getter
@AllArgsConstructor
public class UserPrincipal implements Principal {
    private final Long userId;
    private final String email;
    private final String role;

    @Override
    public String getName() {
        return email;
    }
}

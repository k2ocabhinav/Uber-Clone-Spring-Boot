package com.github.k2ocabhinav.ubercloneapp.services;

import com.github.k2ocabhinav.ubercloneapp.dto.DriverDto;
import com.github.k2ocabhinav.ubercloneapp.dto.SignupDto;
import com.github.k2ocabhinav.ubercloneapp.dto.UserDto;

public interface AuthService {

//  Returns Tokens
    String login(String email, String password);

    UserDto signup(SignupDto signupDto);

    DriverDto onboardNewDriver(Long userId);


}

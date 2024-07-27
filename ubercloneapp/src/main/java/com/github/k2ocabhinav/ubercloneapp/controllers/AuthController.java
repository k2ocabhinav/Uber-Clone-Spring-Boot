package com.github.k2ocabhinav.ubercloneapp.controllers;

import com.github.k2ocabhinav.ubercloneapp.dto.SignupDto;
import com.github.k2ocabhinav.ubercloneapp.dto.UserDto;
import com.github.k2ocabhinav.ubercloneapp.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    UserDto signUp(@RequestBody SignupDto signupDto){
        return authService.signup(signupDto);
    }

}

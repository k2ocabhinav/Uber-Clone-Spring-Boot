package com.github.k2ocabhinav.ubercloneapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverDto {

    private UserDto user;
    private Double rating;
}

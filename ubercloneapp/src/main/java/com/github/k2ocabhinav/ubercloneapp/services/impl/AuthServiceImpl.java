package com.github.k2ocabhinav.ubercloneapp.services.impl;

import com.github.k2ocabhinav.ubercloneapp.dto.DriverDto;
import com.github.k2ocabhinav.ubercloneapp.dto.SignupDto;
import com.github.k2ocabhinav.ubercloneapp.dto.UserDto;
import com.github.k2ocabhinav.ubercloneapp.entities.Driver;
import com.github.k2ocabhinav.ubercloneapp.entities.User;
import com.github.k2ocabhinav.ubercloneapp.entities.enums.Role;
import com.github.k2ocabhinav.ubercloneapp.exceptions.ResourceNotFoundException;
import com.github.k2ocabhinav.ubercloneapp.exceptions.RuntimeConflictException;
import com.github.k2ocabhinav.ubercloneapp.repositories.UserRepository;
import com.github.k2ocabhinav.ubercloneapp.services.AuthService;
import com.github.k2ocabhinav.ubercloneapp.services.DriverService;
import com.github.k2ocabhinav.ubercloneapp.services.RiderService;
import com.github.k2ocabhinav.ubercloneapp.services.WalletService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Set;

import static com.github.k2ocabhinav.ubercloneapp.entities.enums.Role.DRIVER;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final RiderService riderService;
    private final WalletService walletService;
    private final DriverService driverService;

    @Override
    public String login(String email, String password) {
        return "";
    }

    @Override
    @Transactional
    public UserDto signup(SignupDto signupDto){

        User user = userRepository.findByEmail(signupDto.getEmail()).orElse(null);
        if(user != null)
            throw new RuntimeConflictException("Cannot signup, User already exists with email "+signupDto.getEmail());

        User mappedUser = modelMapper.map(signupDto, User.class);
        mappedUser.setRoles(Set.of(Role.RIDER));
        User savedUser = userRepository.save(mappedUser);

//      CREATE USER RELATED ENTITIES
//      1. Rider Entity
        riderService.createNewRider(savedUser);

//      TODO 2. Add Wallet related service ✅
        riderService.createNewRider(savedUser);
        walletService.createNewWallet(savedUser);


        return modelMapper.map(savedUser, UserDto.class);
    }

    @Override
    public DriverDto onboardNewDriver(Long userId, String vehicleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id "+userId));

        if(user.getRoles().contains(DRIVER))
            throw new RuntimeConflictException("User with id "+userId+" is already a Driver");

        Driver createDriver = Driver.builder()
                .user(user)
                .rating(0.0)
                .vehicleId(vehicleId)
                .available(true)
                .build();
        user.getRoles().add(DRIVER);
        userRepository.save(user);
        Driver savedDriver = driverService.createNewDriver(createDriver);
        return modelMapper.map(savedDriver, DriverDto.class);
    }
}

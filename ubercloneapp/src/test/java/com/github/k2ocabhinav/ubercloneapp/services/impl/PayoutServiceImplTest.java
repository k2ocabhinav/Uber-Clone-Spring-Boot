package com.github.k2ocabhinav.ubercloneapp.services.impl;

import com.github.k2ocabhinav.ubercloneapp.dto.PayoutRequestDto;
import com.github.k2ocabhinav.ubercloneapp.entities.Driver;
import com.github.k2ocabhinav.ubercloneapp.entities.PayoutRequest;
import com.github.k2ocabhinav.ubercloneapp.entities.User;
import com.github.k2ocabhinav.ubercloneapp.entities.enums.PayoutStatus;
import com.github.k2ocabhinav.ubercloneapp.entities.enums.TransactionMethod;
import com.github.k2ocabhinav.ubercloneapp.exceptions.RuntimeConflictException;
import com.github.k2ocabhinav.ubercloneapp.repositories.DriverRepository;
import com.github.k2ocabhinav.ubercloneapp.repositories.PayoutRequestRepository;
import com.github.k2ocabhinav.ubercloneapp.security.UserPrincipal;
import com.github.k2ocabhinav.ubercloneapp.services.DriverEarningsService;
import com.github.k2ocabhinav.ubercloneapp.services.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PayoutServiceImplTest {

    @Mock
    private PayoutRequestRepository payoutRequestRepository;

    @Mock
    private DriverEarningsService driverEarningsService;

    @Mock
    private DriverRepository driverRepository;

    @Mock
    private WalletService walletService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private PayoutServiceImpl payoutService;

    private Driver driver;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        driver = new Driver();
        driver.setId(1L);
        driver.setUser(user);
    }

    private void mockSecurityContext() {
        UserPrincipal principal = new UserPrincipal(1L, "user@test.com", "password");
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(principal);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(driverRepository.findByUserId(1L)).thenReturn(Optional.of(driver));
    }

    @Test
    void requestPayout_ShouldSuccess_WhenBalanceIsSufficient() {
        mockSecurityContext();
        when(driverEarningsService.getAvailableBalance()).thenReturn(500.0);

        PayoutRequest savedRequest = new PayoutRequest();
        savedRequest.setId(1L);
        savedRequest.setStatus(PayoutStatus.PENDING);
        savedRequest.setAmount(100.0);

        PayoutRequestDto expectedDto = new PayoutRequestDto();
        expectedDto.setStatus(PayoutStatus.PENDING);

        when(payoutRequestRepository.save(any(PayoutRequest.class))).thenReturn(savedRequest);
        when(modelMapper.map(savedRequest, PayoutRequestDto.class)).thenReturn(expectedDto);

        PayoutRequestDto result = payoutService.requestPayout(100.0);

        assertThat(result.getStatus()).isEqualTo(PayoutStatus.PENDING);
        verify(payoutRequestRepository).save(argThat(req -> req.getAmount() == 100.0 && req.getStatus() == PayoutStatus.PENDING));
    }

    @Test
    void requestPayout_ShouldThrowException_WhenBalanceIsInsufficient() {
        when(driverEarningsService.getAvailableBalance()).thenReturn(50.0);

        RuntimeException ex = assertThrows(RuntimeConflictException.class, () -> {
            payoutService.requestPayout(100.0);
        });

        assertThat(ex.getMessage()).isEqualTo("Insufficient earnings balance for payout request");
        verifyNoInteractions(payoutRequestRepository);
    }

    @Test
    void processPayout_ShouldTransferMoneyAndMarkAsProcessed() {
        PayoutRequest request = new PayoutRequest();
        request.setId(1L);
        request.setDriver(driver);
        request.setAmount(200.0);
        request.setStatus(PayoutStatus.APPROVED);

        when(payoutRequestRepository.findById(1L)).thenReturn(Optional.of(request));
        
        PayoutRequestDto expectedDto = new PayoutRequestDto();
        expectedDto.setStatus(PayoutStatus.PROCESSED);
        
        when(payoutRequestRepository.save(request)).thenReturn(request);
        when(modelMapper.map(request, PayoutRequestDto.class)).thenReturn(expectedDto);

        PayoutRequestDto result = payoutService.processPayout(1L);

        assertThat(result.getStatus()).isEqualTo(PayoutStatus.PROCESSED);
        verify(walletService).addMoneyToWallet(eq(user), eq(200.0), anyString(), isNull(), eq(TransactionMethod.BANKING));
        assertThat(request.getStatus()).isEqualTo(PayoutStatus.PROCESSED);
    }
}

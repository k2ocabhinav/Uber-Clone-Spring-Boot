package com.github.k2ocabhinav.ubercloneapp.services.impl;

import com.github.k2ocabhinav.ubercloneapp.entities.Payment;
import com.github.k2ocabhinav.ubercloneapp.entities.Ride;
import com.github.k2ocabhinav.ubercloneapp.entities.enums.PaymentMethod;
import com.github.k2ocabhinav.ubercloneapp.entities.enums.PaymentStatus;
import com.github.k2ocabhinav.ubercloneapp.exceptions.ResourceNotFoundException;
import com.github.k2ocabhinav.ubercloneapp.repositories.PaymentRepository;
import com.github.k2ocabhinav.ubercloneapp.strategies.PaymentStrategy;
import com.github.k2ocabhinav.ubercloneapp.strategies.PaymentStrategyManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentStrategyManager paymentStrategyManager;

    @Mock
    private PaymentStrategy paymentStrategy;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    private PaymentServiceImpl paymentService;

    private Ride testRide;
    private Payment testPayment;

    @BeforeEach
    void setUp() {
        paymentService = new PaymentServiceImpl(paymentRepository, paymentStrategyManager, eventPublisher);

        testRide = Ride.builder()
                .id(1L)
                .fare(100.0)
                .paymentMethod(PaymentMethod.CASH)
                .build();

        testPayment = Payment.builder()
                .id(1L)
                .ride(testRide)
                .paymentMethod(PaymentMethod.CASH)
                .amount(100.0)
                .paymentStatus(PaymentStatus.PENDING)
                .build();
    }

    @Test
    @DisplayName("Process payment should process existing payment")
    void processPayment_WithExistingPayment_ShouldProcessPayment() {
        when(paymentRepository.findByRide(testRide)).thenReturn(Optional.of(testPayment));
        when(paymentStrategyManager.paymentStrategy(PaymentMethod.CASH)).thenReturn(paymentStrategy);

        paymentService.processPayment(testRide);

        verify(paymentStrategy).processPayment(testPayment);
    }

    @Test
    @DisplayName("Process payment for non-existent payment should throw exception")
    void processPayment_WithNoPayment_ShouldThrowException() {
        when(paymentRepository.findByRide(testRide)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.processPayment(testRide))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Payment not found");
    }

    @Test
    @DisplayName("Create new payment should save and return payment")
    void createNewPayment_ShouldSaveAndReturnPayment() {
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment p = invocation.getArgument(0);
            p.setId(1L);
            return p;
        });

        Payment result = paymentService.createNewPayment(testRide);

        assertThat(result).isNotNull();
        assertThat(result.getAmount()).isEqualTo(100.0);
        assertThat(result.getPaymentStatus()).isEqualTo(PaymentStatus.PENDING);
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    @DisplayName("Create new payment with different payment method")
    void createNewPayment_WithWalletMethod_ShouldSaveWithWallet() {
        testRide.setPaymentMethod(PaymentMethod.WALLET);

        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment p = invocation.getArgument(0);
            p.setId(1L);
            return p;
        });

        Payment result = paymentService.createNewPayment(testRide);

        assertThat(result.getPaymentMethod()).isEqualTo(PaymentMethod.WALLET);
    }

    @Test
    @DisplayName("Update payment status should update and save payment")
    void updatePaymentStatus_ShouldUpdateAndSavePayment() {
        when(paymentRepository.save(testPayment)).thenReturn(testPayment);

        paymentService.updatePaymentStatus(testPayment, PaymentStatus.CONFIRMED);

        assertThat(testPayment.getPaymentStatus()).isEqualTo(PaymentStatus.CONFIRMED);
        verify(paymentRepository).save(testPayment);
    }
}
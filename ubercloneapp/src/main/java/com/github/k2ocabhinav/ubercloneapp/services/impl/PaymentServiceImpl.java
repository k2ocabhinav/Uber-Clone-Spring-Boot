package com.github.k2ocabhinav.ubercloneapp.services.impl;

import com.github.k2ocabhinav.ubercloneapp.entities.Payment;
import com.github.k2ocabhinav.ubercloneapp.entities.Ride;
import com.github.k2ocabhinav.ubercloneapp.entities.enums.PaymentStatus;
import com.github.k2ocabhinav.ubercloneapp.events.PaymentProcessedEvent;
import com.github.k2ocabhinav.ubercloneapp.exceptions.ResourceNotFoundException;
import com.github.k2ocabhinav.ubercloneapp.repositories.PaymentRepository;
import com.github.k2ocabhinav.ubercloneapp.services.PaymentService;
import com.github.k2ocabhinav.ubercloneapp.strategies.PaymentStrategyManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentStrategyManager paymentStrategyManager;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public void processPayment(Ride ride) {
        Payment payment = paymentRepository.findByRide(ride)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for ride with id: "+ride.getId()));
        paymentStrategyManager.paymentStrategy(payment.getPaymentMethod()).processPayment(payment);

        eventPublisher.publishEvent(new PaymentProcessedEvent(this, payment));
    }

    @Override
    @Transactional
    public Payment createNewPayment(Ride ride) {
        Payment payment = Payment.builder()
                .ride(ride)
                .paymentMethod(ride.getPaymentMethod())
                .amount(ride.getFare())
                .paymentStatus(PaymentStatus.PENDING)
                .build();
        return paymentRepository.save(payment);
    }

    @Override
    @Transactional
    public void updatePaymentStatus(Payment payment, PaymentStatus status) {
        payment.setPaymentStatus(status);
        paymentRepository.save(payment);
    }
}

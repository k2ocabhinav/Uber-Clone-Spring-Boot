package com.github.k2ocabhinav.ubercloneapp.strategies;

import com.github.k2ocabhinav.ubercloneapp.entities.Payment;

public interface PaymentStrategy {
    void processPayment(Payment payment);
}

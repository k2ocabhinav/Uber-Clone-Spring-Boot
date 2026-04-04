package com.github.k2ocabhinav.ubercloneapp.strategies.impl;

import com.github.k2ocabhinav.ubercloneapp.configs.PlatformConfig;
import com.github.k2ocabhinav.ubercloneapp.entities.Driver;
import com.github.k2ocabhinav.ubercloneapp.entities.Payment;
import com.github.k2ocabhinav.ubercloneapp.entities.enums.PaymentStatus;
import com.github.k2ocabhinav.ubercloneapp.entities.enums.TransactionMethod;
import com.github.k2ocabhinav.ubercloneapp.repositories.PaymentRepository;
import com.github.k2ocabhinav.ubercloneapp.services.WalletService;
import com.github.k2ocabhinav.ubercloneapp.strategies.PaymentStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CashPaymentStrategy implements PaymentStrategy {

    private final WalletService walletService;
    private final PaymentRepository paymentRepository;
    private final PlatformConfig platformConfig;

    @Override
    public void processPayment(Payment payment) {
        Driver driver = payment.getRide().getDriver();

        double platformCommission = payment.getAmount() * platformConfig.getCommissionRate();

        walletService.deductMoneyFromWallet(driver.getUser(), platformCommission, null,
                payment.getRide(), TransactionMethod.RIDE);

        payment.setPaymentStatus(PaymentStatus.CONFIRMED);
        paymentRepository.save(payment);
    }
}
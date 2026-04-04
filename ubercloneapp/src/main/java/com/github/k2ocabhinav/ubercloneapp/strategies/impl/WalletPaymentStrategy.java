package com.github.k2ocabhinav.ubercloneapp.strategies.impl;

import com.github.k2ocabhinav.ubercloneapp.configs.PlatformConfig;
import com.github.k2ocabhinav.ubercloneapp.entities.Driver;
import com.github.k2ocabhinav.ubercloneapp.entities.Payment;
import com.github.k2ocabhinav.ubercloneapp.entities.Rider;
import com.github.k2ocabhinav.ubercloneapp.entities.enums.PaymentStatus;
import com.github.k2ocabhinav.ubercloneapp.entities.enums.TransactionMethod;
import com.github.k2ocabhinav.ubercloneapp.repositories.PaymentRepository;
import com.github.k2ocabhinav.ubercloneapp.services.WalletService;
import com.github.k2ocabhinav.ubercloneapp.strategies.PaymentStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class WalletPaymentStrategy implements PaymentStrategy {

    private final WalletService walletService;
    private final PaymentRepository paymentRepository;
    private final PlatformConfig platformConfig;

    @Override
    @Transactional
    public void processPayment(Payment payment) {
        Driver driver = payment.getRide().getDriver();
        Rider rider = payment.getRide().getRider();

        walletService.deductMoneyFromWallet(rider.getUser(),
                payment.getAmount(), null, payment.getRide(), TransactionMethod.RIDE);

        double driversCut = payment.getAmount() * (1 - platformConfig.getCommissionRate());

        walletService.addMoneyToWallet(driver.getUser(),
                driversCut, null, payment.getRide(), TransactionMethod.RIDE);

        payment.setPaymentStatus(PaymentStatus.CONFIRMED);
        paymentRepository.save(payment);
    }
}

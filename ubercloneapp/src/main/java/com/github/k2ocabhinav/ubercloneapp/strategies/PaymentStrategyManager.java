package com.github.k2ocabhinav.ubercloneapp.strategies;

import com.github.k2ocabhinav.ubercloneapp.entities.enums.PaymentMethod;
import com.github.k2ocabhinav.ubercloneapp.exceptions.RuntimeConflictException;
import com.github.k2ocabhinav.ubercloneapp.strategies.impl.CashPaymentStrategy;
import com.github.k2ocabhinav.ubercloneapp.strategies.impl.WalletPaymentStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentStrategyManager {

    private final WalletPaymentStrategy walletPaymentStrategy;
    private final CashPaymentStrategy cashPaymentStrategy;

    public PaymentStrategy paymentStrategy(PaymentMethod paymentMethod) {
        return switch (paymentMethod) {
            case WALLET -> walletPaymentStrategy;
            case CASH -> cashPaymentStrategy;
            default -> throw new RuntimeConflictException("Unsupported payment method: " + paymentMethod);
        };
    }
}

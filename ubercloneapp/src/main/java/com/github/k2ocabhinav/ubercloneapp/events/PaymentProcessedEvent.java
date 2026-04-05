package com.github.k2ocabhinav.ubercloneapp.events;

import com.github.k2ocabhinav.ubercloneapp.entities.Payment;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PaymentProcessedEvent extends ApplicationEvent {

    private final Payment payment;

    public PaymentProcessedEvent(Object source, Payment payment) {
        super(source);
        this.payment = payment;
    }
}

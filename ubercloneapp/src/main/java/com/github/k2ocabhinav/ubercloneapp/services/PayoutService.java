package com.github.k2ocabhinav.ubercloneapp.services;

import com.github.k2ocabhinav.ubercloneapp.dto.PayoutRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;

public interface PayoutService {
    PayoutRequestDto requestPayout(BigDecimal amount);
    PayoutRequestDto approvePayout(Long payoutId, String adminNote);
    PayoutRequestDto rejectPayout(Long payoutId, String adminNote);
    PayoutRequestDto processPayout(Long payoutId);
    Page<PayoutRequestDto> getPendingPayouts(PageRequest pageRequest);
    Page<PayoutRequestDto> getMyPayouts(PageRequest pageRequest);
}

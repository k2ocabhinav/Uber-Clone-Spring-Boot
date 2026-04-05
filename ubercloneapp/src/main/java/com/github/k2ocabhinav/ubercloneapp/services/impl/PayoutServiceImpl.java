package com.github.k2ocabhinav.ubercloneapp.services.impl;

import com.github.k2ocabhinav.ubercloneapp.dto.PayoutRequestDto;
import com.github.k2ocabhinav.ubercloneapp.entities.Driver;
import com.github.k2ocabhinav.ubercloneapp.entities.PayoutRequest;
import com.github.k2ocabhinav.ubercloneapp.entities.enums.PayoutStatus;
import com.github.k2ocabhinav.ubercloneapp.entities.enums.TransactionMethod;
import com.github.k2ocabhinav.ubercloneapp.exceptions.ResourceNotFoundException;
import com.github.k2ocabhinav.ubercloneapp.exceptions.RuntimeConflictException;
import com.github.k2ocabhinav.ubercloneapp.repositories.DriverRepository;
import com.github.k2ocabhinav.ubercloneapp.repositories.PayoutRequestRepository;
import com.github.k2ocabhinav.ubercloneapp.security.UserPrincipal;
import com.github.k2ocabhinav.ubercloneapp.services.DriverEarningsService;
import com.github.k2ocabhinav.ubercloneapp.services.PayoutService;
import com.github.k2ocabhinav.ubercloneapp.services.WalletService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PayoutServiceImpl implements PayoutService {

    private final PayoutRequestRepository payoutRequestRepository;
    private final DriverEarningsService driverEarningsService;
    private final DriverRepository driverRepository;
    private final WalletService walletService;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public PayoutRequestDto requestPayout(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeConflictException("Payout amount must be greater than zero");
        }

        BigDecimal availableBalance = driverEarningsService.getAvailableBalance();
        if (amount.compareTo(availableBalance) > 0) {
            throw new RuntimeConflictException("Insufficient earnings balance for payout request");
        }

        Driver driver = getCurrentDriver();

        PayoutRequest payoutRequest = PayoutRequest.builder()
                .driver(driver)
                .amount(amount)
                .status(PayoutStatus.PENDING)
                .build();

        return modelMapper.map(payoutRequestRepository.save(payoutRequest), PayoutRequestDto.class);
    }

    @Override
    @Transactional
    public PayoutRequestDto approvePayout(Long payoutId, String adminNote) {
        PayoutRequest request = payoutRequestRepository.findById(payoutId)
                .orElseThrow(() -> new ResourceNotFoundException("Payout request not found"));

        if (request.getStatus() != PayoutStatus.PENDING) {
            throw new RuntimeConflictException("Can only approve PENDING requests");
        }

        request.setStatus(PayoutStatus.APPROVED);
        request.setAdminNote(adminNote);
        return modelMapper.map(payoutRequestRepository.save(request), PayoutRequestDto.class);
    }

    @Override
    @Transactional
    public PayoutRequestDto rejectPayout(Long payoutId, String adminNote) {
        PayoutRequest request = payoutRequestRepository.findById(payoutId)
                .orElseThrow(() -> new ResourceNotFoundException("Payout request not found"));

        if (request.getStatus() != PayoutStatus.PENDING) {
            throw new RuntimeConflictException("Can only reject PENDING requests");
        }

        request.setStatus(PayoutStatus.REJECTED);
        request.setAdminNote(adminNote);
        return modelMapper.map(payoutRequestRepository.save(request), PayoutRequestDto.class);
    }

    @Override
    @Transactional
    public PayoutRequestDto processPayout(Long payoutId) {
        PayoutRequest request = payoutRequestRepository.findById(payoutId)
                .orElseThrow(() -> new ResourceNotFoundException("Payout request not found"));

        if (request.getStatus() != PayoutStatus.APPROVED) {
            throw new RuntimeConflictException("Can only process APPROVED requests");
        }

        walletService.addMoneyToWallet(request.getDriver().getUser(), request.getAmount(), 
                "PAYOUT_" + UUID.randomUUID().toString(), null, TransactionMethod.BANKING);

        request.setStatus(PayoutStatus.PROCESSED);
        request.setProcessedAt(LocalDateTime.now());
        return modelMapper.map(payoutRequestRepository.save(request), PayoutRequestDto.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PayoutRequestDto> getPendingPayouts(PageRequest pageRequest) {
        return payoutRequestRepository.findByStatusOrderByRequestedAtAsc(PayoutStatus.PENDING, pageRequest)
                .map(request -> modelMapper.map(request, PayoutRequestDto.class));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PayoutRequestDto> getMyPayouts(PageRequest pageRequest) {
        Driver driver = getCurrentDriver();
        return payoutRequestRepository.findByDriverOrderByRequestedAtDesc(driver, pageRequest)
                .map(request -> modelMapper.map(request, PayoutRequestDto.class));
    }

    private Driver getCurrentDriver() {
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return driverRepository.findByUserId(principal.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Driver profile not found"));
    }
}

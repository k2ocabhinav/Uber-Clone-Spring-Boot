package com.github.k2ocabhinav.ubercloneapp.services.impl;

import com.github.k2ocabhinav.ubercloneapp.dto.PromoCodeDto;
import com.github.k2ocabhinav.ubercloneapp.dto.PromoCodeResultDto;
import com.github.k2ocabhinav.ubercloneapp.entities.PromoCode;
import com.github.k2ocabhinav.ubercloneapp.entities.PromoCodeUsage;
import com.github.k2ocabhinav.ubercloneapp.entities.User;
import com.github.k2ocabhinav.ubercloneapp.entities.enums.DiscountType;
import com.github.k2ocabhinav.ubercloneapp.exceptions.ResourceNotFoundException;
import com.github.k2ocabhinav.ubercloneapp.exceptions.RuntimeConflictException;
import com.github.k2ocabhinav.ubercloneapp.repositories.PromoCodeRepository;
import com.github.k2ocabhinav.ubercloneapp.repositories.PromoCodeUsageRepository;
import com.github.k2ocabhinav.ubercloneapp.services.PromoCodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PromoCodeServiceImpl implements PromoCodeService {

    private final PromoCodeRepository promoCodeRepository;
    private final PromoCodeUsageRepository promoCodeUsageRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public PromoCodeDto createPromoCode(PromoCodeDto promoCodeDto) {
        if (promoCodeRepository.existsByCode(promoCodeDto.getCode())) {
            throw new RuntimeConflictException("Promo code already exists: " + promoCodeDto.getCode());
        }

        PromoCode promoCode = modelMapper.map(promoCodeDto, PromoCode.class);
        promoCode.setCurrentUses(0);
        
        PromoCode saved = promoCodeRepository.save(promoCode);
        log.info("Created new promo code: {}", saved.getCode());
        return modelMapper.map(saved, PromoCodeDto.class);
    }

    @Override
    @Transactional
    public PromoCodeDto updatePromoCode(Long id, PromoCodeDto promoCodeDto) {
        PromoCode existing = getPromoCodeEntity(id);

        // Update fields that are allowed to change
        existing.setDescription(promoCodeDto.getDescription());
        existing.setStartDate(promoCodeDto.getStartDate());
        existing.setEndDate(promoCodeDto.getEndDate());
        existing.setMaxUses(promoCodeDto.getMaxUses());
        existing.setMinRideFare(promoCodeDto.getMinRideFare());
        if (promoCodeDto.getActive() != null) {
            existing.setActive(promoCodeDto.getActive());
        }

        PromoCode saved = promoCodeRepository.save(existing);
        log.info("Updated promo code id: {}", id);
        return modelMapper.map(saved, PromoCodeDto.class);
    }

    @Override
    @Transactional
    public PromoCodeDto deactivatePromoCode(Long id) {
        PromoCode existing = getPromoCodeEntity(id);
        existing.setActive(false);
        PromoCode saved = promoCodeRepository.save(existing);
        log.info("Deactivated promo code id: {}", id);
        return modelMapper.map(saved, PromoCodeDto.class);
    }

    @Override
    @Transactional
    public PromoCodeResultDto validateAndApplyPromo(String code, User user, BigDecimal originalFare) {
        if (code == null || code.trim().isEmpty()) {
            return PromoCodeResultDto.builder().valid(false).message("No promo code provided").build();
        }

        PromoCode promoCode = promoCodeRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Promo code not found: " + code));

        if (!promoCode.getActive()) {
            throw new RuntimeConflictException("Promo code is inactive");
        }

        LocalDate now = LocalDate.now();
        if (promoCode.getStartDate() != null && now.isBefore(promoCode.getStartDate())) {
            throw new RuntimeConflictException("Promo code is not yet valid");
        }
        if (promoCode.getEndDate() != null && now.isAfter(promoCode.getEndDate())) {
            throw new RuntimeConflictException("Promo code has expired");
        }

        if (promoCode.getMaxUses() != null && promoCode.getCurrentUses() >= promoCode.getMaxUses()) {
            throw new RuntimeConflictException("Promo code usage limit reached");
        }

        if (promoCode.getMinRideFare() != null && originalFare.compareTo(promoCode.getMinRideFare()) < 0) {
            throw new RuntimeConflictException("Ride fare does not meet minimum requirement for promo code");
        }

        if (promoCodeUsageRepository.existsByPromoCodeAndUser(promoCode, user)) {
            throw new RuntimeConflictException("You have already used this promo code");
        }

        BigDecimal discountAmount;
        if (promoCode.getDiscountType() == DiscountType.FLAT) {
            discountAmount = promoCode.getDiscountValue();
        } else {
            // Percentage discount
            discountAmount = originalFare.multiply(promoCode.getDiscountValue())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        }

        // Ensure discount doesn't exceed original fare
        if (discountAmount.compareTo(originalFare) > 0) {
            discountAmount = originalFare;
        }

        BigDecimal discountedFare = originalFare.subtract(discountAmount);

        // Record usage
        PromoCodeUsage usage = PromoCodeUsage.builder()
                .promoCode(promoCode)
                .user(user)
                .discountApplied(discountAmount)
                .usedAt(LocalDateTime.now())
                .build();
        promoCodeUsageRepository.save(usage);

        // Increment current uses
        promoCode.setCurrentUses(promoCode.getCurrentUses() + 1);
        promoCodeRepository.save(promoCode);

        log.info("Applied promo code {} for user {}, discount: {}", code, user.getId(), discountAmount);

        return PromoCodeResultDto.builder()
                .valid(true)
                .discountAmount(discountAmount)
                .originalFare(originalFare)
                .discountedFare(discountedFare)
                .message("Promo code applied successfully")
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PromoCodeDto> getActivePromoCodes() {
        return promoCodeRepository.findByActiveTrue().stream()
                .filter(p -> p.getEndDate() == null || !LocalDate.now().isAfter(p.getEndDate()))
                .map(p -> modelMapper.map(p, PromoCodeDto.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PromoCodeDto> getAllPromoCodes() {
        return promoCodeRepository.findAll().stream()
                .map(p -> modelMapper.map(p, PromoCodeDto.class))
                .collect(Collectors.toList());
    }

    private PromoCode getPromoCodeEntity(Long id) {
        return promoCodeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promo code not found with id: " + id));
    }
}

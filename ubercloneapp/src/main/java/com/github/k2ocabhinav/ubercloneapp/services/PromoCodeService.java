package com.github.k2ocabhinav.ubercloneapp.services;

import com.github.k2ocabhinav.ubercloneapp.dto.PromoCodeDto;
import com.github.k2ocabhinav.ubercloneapp.dto.PromoCodeResultDto;
import com.github.k2ocabhinav.ubercloneapp.entities.User;

import java.math.BigDecimal;
import java.util.List;

public interface PromoCodeService {
    PromoCodeDto createPromoCode(PromoCodeDto promoCodeDto);
    PromoCodeDto updatePromoCode(Long id, PromoCodeDto promoCodeDto);
    PromoCodeDto deactivatePromoCode(Long id);
    PromoCodeResultDto validateAndApplyPromo(String code, User user, BigDecimal originalFare);
    List<PromoCodeDto> getActivePromoCodes();
    List<PromoCodeDto> getAllPromoCodes();
}

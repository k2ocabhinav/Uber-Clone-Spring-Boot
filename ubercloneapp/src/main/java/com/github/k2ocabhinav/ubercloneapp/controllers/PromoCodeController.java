package com.github.k2ocabhinav.ubercloneapp.controllers;

import com.github.k2ocabhinav.ubercloneapp.dto.PromoCodeDto;
import com.github.k2ocabhinav.ubercloneapp.services.PromoCodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Promo Codes", description = "Promo code management and retrieval")
public class PromoCodeController {

    private final PromoCodeService promoCodeService;

    // --- Admin Endpoints ---

    @PostMapping("/api/v1/admin/promo-codes")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new promo code (Admin)")
    public ResponseEntity<PromoCodeDto> createPromoCode(@RequestBody PromoCodeDto promoCodeDto) {
        return ResponseEntity.ok(promoCodeService.createPromoCode(promoCodeDto));
    }

    @GetMapping("/api/v1/admin/promo-codes")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all promo codes (Admin)")
    public ResponseEntity<List<PromoCodeDto>> getAllPromoCodes() {
        return ResponseEntity.ok(promoCodeService.getAllPromoCodes());
    }

    @PutMapping("/api/v1/admin/promo-codes/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update a promo code (Admin)")
    public ResponseEntity<PromoCodeDto> updatePromoCode(@PathVariable Long id, @RequestBody PromoCodeDto promoCodeDto) {
        return ResponseEntity.ok(promoCodeService.updatePromoCode(id, promoCodeDto));
    }

    @PostMapping("/api/v1/admin/promo-codes/{id}/deactivate")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Deactivate a promo code (Admin)")
    public ResponseEntity<PromoCodeDto> deactivatePromoCode(@PathVariable Long id) {
        return ResponseEntity.ok(promoCodeService.deactivatePromoCode(id));
    }

    // --- Rider / Public Endpoints ---

    @GetMapping("/promo-codes/active")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('RIDER', 'ADMIN')")
    @Operation(summary = "Get active promo codes (Rider)")
    public ResponseEntity<List<PromoCodeDto>> getActivePromoCodes() {
        return ResponseEntity.ok(promoCodeService.getActivePromoCodes());
    }
}

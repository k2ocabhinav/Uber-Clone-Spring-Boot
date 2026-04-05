package com.github.k2ocabhinav.ubercloneapp.repositories;

import com.github.k2ocabhinav.ubercloneapp.entities.PromoCode;
import com.github.k2ocabhinav.ubercloneapp.entities.PromoCodeUsage;
import com.github.k2ocabhinav.ubercloneapp.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PromoCodeUsageRepository extends JpaRepository<PromoCodeUsage, Long> {
    boolean existsByPromoCodeAndUser(PromoCode promoCode, User user);
    int countByPromoCode(PromoCode promoCode);
}

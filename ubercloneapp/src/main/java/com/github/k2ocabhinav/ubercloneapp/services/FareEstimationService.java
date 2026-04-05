package com.github.k2ocabhinav.ubercloneapp.services;

import com.github.k2ocabhinav.ubercloneapp.dto.FareEstimateDto;
import com.github.k2ocabhinav.ubercloneapp.dto.FareEstimateRequestDto;

public interface FareEstimationService {
    FareEstimateDto estimateFare(FareEstimateRequestDto fareEstimateRequestDto);
}

package com.affiliateLink.offer.internal.dto;

import com.affiliateLink.offer.internal.entities.Campaign;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CampaignResponse(
        Long id,
        Long offerId,
        String name,
        LocalDate startDate,
        LocalDate endDate,
        BigDecimal budget,
        Campaign.PayoutModel payoutModel,
        BigDecimal payoutValue,
        Campaign.Status status
) {}
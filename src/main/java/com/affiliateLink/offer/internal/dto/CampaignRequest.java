package com.affiliateLink.offer.internal.dto;

import com.affiliateLink.offer.internal.entities.Campaign;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CampaignRequest(
        @NotBlank String name,
        @NotNull LocalDate startDate,
        @NotNull LocalDate endDate,
        @NotNull @Positive BigDecimal budget,
        @NotNull Campaign.PayoutModel payoutModel,
        @NotNull BigDecimal payoutValue
) {}
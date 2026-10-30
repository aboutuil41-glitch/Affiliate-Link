package com.affiliateLink.offer.internal.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public record OfferDetailResponse(
        Long id,
        Long advertiserId,
        String title,
        String description,
        String landingUrl,
        String niche,
        Set<String> tags,
        LocalDateTime createdAt,
        List<CampaignResponse> campaigns
) {}
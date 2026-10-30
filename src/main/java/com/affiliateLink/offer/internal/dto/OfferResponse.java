package com.affiliateLink.offer.internal.dto;

import java.time.LocalDateTime;
import java.util.Set;

public record OfferResponse(
        Long id,
        Long advertiserId,
        String title,
        String description,
        String landingUrl,
        String niche,
        Set<String> tags,
        LocalDateTime createdAt
) {}
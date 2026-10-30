package com.affiliateLink.offer.internal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record OfferUpdateRequest(
        @NotBlank @Size(max = 255) String title,
        String description,
        @NotBlank @Size(max = 500) String landingUrl,
        @NotBlank @Size(max = 100) String niche,
        Set<@NotBlank String> tags
) {}
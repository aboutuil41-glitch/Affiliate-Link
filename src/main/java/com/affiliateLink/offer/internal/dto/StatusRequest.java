package com.affiliateLink.offer.internal.dto;

import com.affiliateLink.offer.internal.entities.Campaign;
import jakarta.validation.constraints.NotNull;

public record StatusRequest(@NotNull Campaign.Status status) {}
package com.affiliateLink.profile.internal.dto;

import com.affiliateLink.profile.internal.entities.Profile;
import jakarta.validation.constraints.NotNull;

public record StatusRequest(@NotNull Profile.Status status) {}
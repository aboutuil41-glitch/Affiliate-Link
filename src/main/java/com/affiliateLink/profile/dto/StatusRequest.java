package com.affiliateLink.profile.dto;

import com.affiliateLink.profile.entities.Profile;
import jakarta.validation.constraints.NotNull;

public record StatusRequest(@NotNull Profile.Status status) {}
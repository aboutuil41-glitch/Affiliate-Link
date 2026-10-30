package com.affiliateLink.offer.internal.services;

import com.affiliateLink.common.exception.BusinessRuleException;
import com.affiliateLink.common.exception.ResourceNotFoundException;
import com.affiliateLink.profile.ProfileApi;
import org.springframework.stereotype.Component;

@Component
class AdvertiserChecker {

    private final ProfileApi profileApi;

    AdvertiserChecker(ProfileApi profileApi) {
        this.profileApi = profileApi;
    }

    void requireActive(Long advertiserId) {
        if (profileApi.getAdvertiser(advertiserId).isEmpty()) {
            throw new ResourceNotFoundException("Advertiser not found: " + advertiserId);
        }
        if (!profileApi.isActive(advertiserId)) {
            throw new BusinessRuleException("Advertiser is not active: " + advertiserId);
        }
    }
}
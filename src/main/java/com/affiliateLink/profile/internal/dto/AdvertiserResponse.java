package com.affiliateLink.profile.internal.dto;

import com.affiliateLink.profile.internal.entities.Advertiser;
import com.affiliateLink.profile.internal.entities.Profile;

public record AdvertiserResponse(
        String email,
        Profile.Status status,
        String companyName,
        String sector,
        String website
) {
    public static AdvertiserResponse from(Advertiser A){
        return new AdvertiserResponse(
                A.getEmail(), A.getStatus(),
                A.getCompanyName(), A.getSector(), A.getWebsite());
    }
}

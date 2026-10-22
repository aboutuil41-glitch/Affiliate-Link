package com.affiliateLink.profile.dto;

import com.affiliateLink.profile.entities.Advertiser;
import com.affiliateLink.profile.entities.Profile;

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

package com.affiliateLink.offer.internal.services;

import com.affiliateLink.offer.internal.repositories.OfferRepository;
import com.affiliateLink.profile.ProfileApi;
import com.affiliateLink.profile.internal.repositories.AdvertiserRepository;

public class test {
    private final OfferRepository offerRepository;
    private final ProfileApi profileApi;

    public test(OfferRepository offerRepository, ProfileApi profileApi) {
        this.offerRepository = offerRepository;
        this.profileApi = profileApi;
    }
}

package com.affiliateLink.profile;

import java.util.Optional;

public interface ProfileApi {
    Optional<AdvertiserInfo> getAdvertiser(Long id);
    boolean isActive(Long AdvertiserId);
}

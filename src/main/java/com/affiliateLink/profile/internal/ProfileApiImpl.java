package com.affiliateLink.profile.internal;

import com.affiliateLink.profile.AdvertiserInfo;
import com.affiliateLink.profile.ProfileApi;
import com.affiliateLink.profile.internal.entities.Profile;
import com.affiliateLink.profile.internal.repositories.AdvertiserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ProfileApiImpl implements ProfileApi {

    private final AdvertiserRepository ar;

    public ProfileApiImpl(AdvertiserRepository ar) {
        this.ar = ar;
    }

    @Override
    public Optional<AdvertiserInfo> getAdvertiser(Long id) {
        return ar.findById(id)
                .map(advertiser ->
                new AdvertiserInfo(advertiser.getId(), advertiser.getStatus().name()));
    }

    @Override
    public boolean isActive(Long AdvertiserId) {
        return ar.findById(AdvertiserId)
                .map(advertiser
                -> advertiser.getStatus() == Profile.Status.ACTIVE)
                .orElse(false);
    }
}

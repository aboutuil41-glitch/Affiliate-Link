package com.affiliateLink.profile.services;

import com.affiliateLink.common.exception.ResourceNotFoundException;
import com.affiliateLink.profile.entities.Profile;
import com.affiliateLink.profile.repositories.ProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfileService {
    private final ProfileRepository profileRepository;

    public ProfileService(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Transactional
    public void changeStatus(Long id, Profile.Status newStatus) {
        Profile profile = profileRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Profile not found: " + id));
        profile.changeStatus(newStatus);
    }
}
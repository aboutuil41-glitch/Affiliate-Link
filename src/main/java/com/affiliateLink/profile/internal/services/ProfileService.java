package com.affiliateLink.profile.internal.services;

import com.affiliateLink.common.exception.ResourceNotFoundException;
import com.affiliateLink.profile.ProfileSuspended;
import com.affiliateLink.profile.internal.entities.Profile;
import com.affiliateLink.profile.internal.repositories.ProfileRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfileService {
    private final ProfileRepository profileRepository;
    private final ApplicationEventPublisher eventPublisher;

    public ProfileService(ProfileRepository profileRepository, ApplicationEventPublisher eventPublisher) {
        this.profileRepository = profileRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public void changeStatus(Long id, Profile.Status newStatus) {
        Profile profile = profileRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Profile not found: " + id));
        profile.changeStatus(newStatus);

        System.out.println(">>> changeStatus called, new status = " + newStatus);

        if (newStatus == Profile.Status.SUSPENDED){
            System.out.println(">>> publishing event");
            eventPublisher.publishEvent(new ProfileSuspended(id));
        }
    }
}
package com.affiliateLink.profile.controller;

import com.affiliateLink.profile.entities.Advertiser;
import com.affiliateLink.profile.entities.Profile;
import com.affiliateLink.profile.entities.Publisher;
import com.affiliateLink.profile.repositories.ProfileRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class test {
    private final ProfileRepository profileRepository;
    public test(ProfileRepository profileRepository){
        this.profileRepository = profileRepository;
    }
    @GetMapping("/test/seed")
    public String seed(){
        if(profileRepository.count()>0){
            return "Already Seeded";
        }
        profileRepository.save(new Publisher("Sara", "Amrani", Publisher.Niche.GAMING,
                "Tech reviewer", "Morocco", "sara@test.com"));
        profileRepository.save(new Advertiser("Acme", "Retail",
                "https://acme.com", "acme@test.com"));
        return "Seeded";
    }

    @GetMapping("/test/all")
    public List<Profile> all() {
        return profileRepository.findAll();
    }

}

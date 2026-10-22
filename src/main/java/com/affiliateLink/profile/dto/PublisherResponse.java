package com.affiliateLink.profile.dto;

import com.affiliateLink.profile.entities.Profile;
import com.affiliateLink.profile.entities.Publisher;

import java.time.LocalDate;

    public record PublisherResponse(
            String email,
            Profile.Status status,
            LocalDate createdAt,
            String firstName,
            String lastName,
            Publisher.Niche niche,
            String bio,
            String country
    ) {
    public static PublisherResponse from(Publisher p){
        return new PublisherResponse(
                p.getEmail(), p.getStatus(), p.getCreatedAt(),
                p.getFirstName(), p.getLastName(), p.getNiche(),
                p.getBio(), p.getCountry());
    }
}


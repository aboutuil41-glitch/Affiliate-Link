package com.affiliateLink.profile.internal.repositories;

import com.affiliateLink.profile.internal.entities.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    boolean existsByEmail(String email);
}

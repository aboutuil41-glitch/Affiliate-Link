package com.affiliateLink.profile.repositories;

import com.affiliateLink.profile.entities.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Profile, Long> { }

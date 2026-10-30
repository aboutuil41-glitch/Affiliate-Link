package com.affiliateLink.offer.internal.repositories;

import com.affiliateLink.offer.internal.entities.Campaign;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CampaignRepository extends JpaRepository<Campaign, Long> {

    List<Campaign> findByStatus(Campaign.Status status);

    List<Campaign> findByOfferIdAndStatus(Long offerId, Campaign.Status status);
}
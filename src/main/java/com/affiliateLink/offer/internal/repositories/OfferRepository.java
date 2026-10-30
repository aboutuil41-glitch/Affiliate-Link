package com.affiliateLink.offer.internal.repositories;

import com.affiliateLink.offer.internal.entities.Offer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OfferRepository extends JpaRepository<Offer, Long> {

    Page<Offer> findByNicheIgnoreCase(String niche, Pageable pageable);

    Page<Offer> findByAdvertiserId(Long advertiserId, Pageable pageable);

    Page<Offer> findByTagsName(String tagName, Pageable pageable);

    Page<Offer> findByTitleContainingIgnoreCase(String keyword, Pageable pageable);
}
package com.affiliateLink.offer.internal.services;

import com.affiliateLink.common.exception.ResourceNotFoundException;
import com.affiliateLink.offer.internal.dto.OfferDetailResponse;
import com.affiliateLink.offer.internal.dto.OfferRequest;
import com.affiliateLink.offer.internal.dto.OfferResponse;
import com.affiliateLink.offer.internal.dto.OfferUpdateRequest;
import com.affiliateLink.offer.internal.entities.Offer;
import com.affiliateLink.offer.internal.entities.Tag;
import com.affiliateLink.offer.internal.mappers.OfferMapper;
import com.affiliateLink.offer.internal.repositories.OfferRepository;
import com.affiliateLink.offer.internal.repositories.TagRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@Transactional
public class OfferService {

    private final OfferRepository offerRepository;
    private final TagRepository tagRepository;
    private final AdvertiserChecker advertiserChecker;
    private final OfferMapper offerMapper;

    public OfferService(OfferRepository offerRepository, TagRepository tagRepository,
                        AdvertiserChecker advertiserChecker, OfferMapper offerMapper) {
        this.offerRepository = offerRepository;
        this.tagRepository = tagRepository;
        this.advertiserChecker = advertiserChecker;
        this.offerMapper = offerMapper;
    }

    public OfferResponse create(OfferRequest request) {
        advertiserChecker.requireActive(request.advertiserId());
        Offer offer = offerMapper.toEntity(request);
        offer.setTags(getOrCreateTags(request.tags()));
        return offerMapper.toResponse(offerRepository.save(offer));
    }

    public OfferResponse update(Long id, OfferUpdateRequest request) {
        Offer offer = findOffer(id);
        offerMapper.updateEntity(request, offer);
        offer.setTags(getOrCreateTags(request.tags()));
        return offerMapper.toResponse(offer);
    }

    @Transactional(readOnly = true)
    public OfferDetailResponse get(Long id) {
        return offerMapper.toDetailResponse(findOffer(id));
    }

    // Filters by ONE thing at a time (first one given wins)
    @Transactional(readOnly = true)
    public Page<OfferResponse> search(String niche, Long advertiserId, String tag,
                                      String keyword, Pageable pageable) {
        Page<Offer> offers;
        if (niche != null && !niche.isBlank()) {
            offers = offerRepository.findByNicheIgnoreCase(niche.trim(), pageable);
        } else if (advertiserId != null) {
            offers = offerRepository.findByAdvertiserId(advertiserId, pageable);
        } else if (tag != null && !tag.isBlank()) {
            offers = offerRepository.findByTagsName(tag.trim().toLowerCase(), pageable);
        } else if (keyword != null && !keyword.isBlank()) {
            offers = offerRepository.findByTitleContainingIgnoreCase(keyword.trim(), pageable);
        } else {
            offers = offerRepository.findAll(pageable);
        }
        return offers.map(offerMapper::toResponse);
    }

    private Offer findOffer(Long id) {
        return offerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Offer not found: " + id));
    }

    private Set<Tag> getOrCreateTags(Set<String> names) {
        Set<Tag> tags = new HashSet<>();
        if (names == null) {
            return tags;
        }
        for (String raw : names) {
            String name = raw.trim().toLowerCase();
            if (name.isEmpty()) {
                continue;
            }
            Tag tag = tagRepository.findByName(name).orElse(null);
            if (tag == null) {
                tag = new Tag();
                tag.setName(name);
                tag = tagRepository.save(tag);
            }
            tags.add(tag);
        }
        return tags;
    }
}
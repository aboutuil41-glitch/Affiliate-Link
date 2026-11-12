package com.affiliateLink.offer.internal.services;

import com.affiliateLink.common.exception.BadRequestException;
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
import jakarta.persistence.criteria.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class OfferService {

    private static final Logger log = LoggerFactory.getLogger(OfferService.class);
    private static final Set<String> SORTABLE = Set.of("title", "createdAt", "niche");

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
        Offer saved = offerRepository.save(offer);
        log.info("Offer {} created for advertiser {}", saved.getId(), saved.getAdvertiserId());
        return offerMapper.toResponse(saved);
    }

    public OfferResponse update(Long id, OfferUpdateRequest request) {
        Offer offer = findOffer(id);
        offerMapper.updateEntity(request, offer);
        offer.setTags(getOrCreateTags(request.tags()));
        log.info("Offer {} updated", id);
        return offerMapper.toResponse(offer);
    }

    @Transactional(readOnly = true)
    public OfferDetailResponse get(Long id) {
        return offerMapper.toDetailResponse(findOffer(id));
    }

    // All filters are optional and can be combined
    @Transactional(readOnly = true)
    public Page<OfferResponse> search(String niche, Long advertiserId, String tag,
                                      String keyword, Pageable pageable) {
        for (Sort.Order order : pageable.getSort()) {
            if (!SORTABLE.contains(order.getProperty())) {
                throw new BadRequestException("Invalid sort field: " + order.getProperty());
            }
        }

        Specification<Offer> spec = (root, query, cb) -> {
            List<Predicate> filters = new ArrayList<>();
            if (niche != null && !niche.isBlank()) {
                filters.add(cb.equal(cb.lower(root.get("niche")), niche.trim().toLowerCase()));
            }
            if (advertiserId != null) {
                filters.add(cb.equal(root.get("advertiserId"), advertiserId));
            }
            if (tag != null && !tag.isBlank()) {
                filters.add(cb.equal(root.join("tags").get("name"), tag.trim().toLowerCase()));
                query.distinct(true);
            }
            if (keyword != null && !keyword.isBlank()) {
                filters.add(cb.like(cb.lower(root.get("title")),
                        "%" + keyword.trim().toLowerCase() + "%"));
            }
            return cb.and(filters.toArray(new Predicate[0]));
        };

        return offerRepository.findAll(spec, pageable).map(offerMapper::toResponse);
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
        // Clean the names first: lowercase, trimmed, no blanks, no duplicates
        Set<String> cleanNames = new HashSet<>();
        for (String raw : names) {
            String name = raw.trim().toLowerCase();
            if (!name.isEmpty()) {
                cleanNames.add(name);
            }
        }

        for (String name : cleanNames) {
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
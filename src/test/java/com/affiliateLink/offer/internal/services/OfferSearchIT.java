package com.affiliateLink.offer.internal.services;

import com.affiliateLink.AbstractMySqlIT;
import com.affiliateLink.offer.internal.dto.OfferResponse;
import com.affiliateLink.offer.internal.entities.Offer;
import com.affiliateLink.offer.internal.entities.Tag;
import com.affiliateLink.offer.internal.mappers.OfferMapper;
import com.affiliateLink.offer.internal.repositories.OfferRepository;
import com.affiliateLink.offer.internal.repositories.TagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests the offer search against a REAL MySQL database (Docker must be running).
 * Each test is rolled back, so tests don't affect each other.
 * Test data uses "zz-" / "Zz" values so it never mixes with your seed data.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // use the Docker DB, not H2
class OfferSearchIT extends AbstractMySqlIT {

    @Autowired OfferRepository offerRepository;
    @Autowired TagRepository tagRepository;

    OfferService service;
    final List<String> mappedTitles = new ArrayList<>(); // titles in the order the service returned them

    @BeforeEach
    void setUp() {
        // The mapper is a fake that just remembers each title it was asked to map
        OfferMapper offerMapper = mock(OfferMapper.class);
        when(offerMapper.toResponse(any(Offer.class))).thenAnswer(invocation -> {
            mappedTitles.add(((Offer) invocation.getArgument(0)).getTitle());
            return mock(OfferResponse.class);
        });

        service = new OfferService(offerRepository, tagRepository, mock(AdvertiserChecker.class), offerMapper);

        // Given: 3 offers
        Tag fit = tag("zz-fit");
        Tag office = tag("zz-office");
        offer("Zz Running Shoes", "zz-sport", 900L, fit);
        offer("Zz Yoga Mat",      "zz-sport", 901L, fit);
        offer("Zz Laptop Stand",  "zz-tech",  900L, office);
    }

    // ---------- each criterion alone ----------

    @Test
    void shouldFindOffersByNicheAlone() {
        Page<OfferResponse> result = service.search("ZZ-Sport", null, null, null, PageRequest.of(0, 20));

        assertThat(result.getTotalElements()).isEqualTo(2);
    }

    @Test
    void shouldFindOffersByAdvertiserAlone() {
        Page<OfferResponse> result = service.search(null, 900L, null, null, PageRequest.of(0, 20));

        assertThat(result.getTotalElements()).isEqualTo(2);
    }

    @Test
    void shouldFindOffersByTagAlone() {
        Page<OfferResponse> result = service.search(null, null, "ZZ-FIT", null, PageRequest.of(0, 20));

        assertThat(result.getTotalElements()).isEqualTo(2);
    }

    @Test
    void shouldFindOffersByKeywordInTitleAlone() {
        Page<OfferResponse> result = service.search(null, null, null, "zz yoga", PageRequest.of(0, 20));

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(mappedTitles).containsExactly("Zz Yoga Mat");
    }

    // ---------- a combination ----------

    @Test
    void shouldCombineNicheAndAdvertiser() {
        Page<OfferResponse> result = service.search("zz-sport", 900L, null, null, PageRequest.of(0, 20));

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(mappedTitles).containsExactly("Zz Running Shoes");
    }

    // ---------- no criteria ----------

    @Test
    void shouldReturnAllOffersWhenNoCriteria() {
        Page<OfferResponse> result = service.search(null, null, null, null, PageRequest.of(0, 100));

        // our 3 offers plus whatever seed data exists
        assertThat(result.getTotalElements()).isGreaterThanOrEqualTo(3);
        assertThat(mappedTitles).contains("Zz Running Shoes", "Zz Yoga Mat", "Zz Laptop Stand");
    }

    // ---------- pagination ----------

    @Test
    void shouldPaginateResults() {
        Page<OfferResponse> firstPage = service.search(null, null, null, "zz", PageRequest.of(0, 2));
        Page<OfferResponse> secondPage = service.search(null, null, null, "zz", PageRequest.of(1, 2));

        assertThat(firstPage.getTotalElements()).isEqualTo(3);
        assertThat(firstPage.getTotalPages()).isEqualTo(2);
        assertThat(firstPage.getContent()).hasSize(2);
        assertThat(secondPage.getContent()).hasSize(1);
    }

    // ---------- sorting ----------

    @Test
    void shouldSortByTitleAscending() {
        service.search(null, null, null, "zz", PageRequest.of(0, 20, Sort.by("title").ascending()));

        assertThat(mappedTitles)
                .containsExactly("Zz Laptop Stand", "Zz Running Shoes", "Zz Yoga Mat");
    }

    @Test
    void shouldSortByTitleDescending() {
        service.search(null, null, null, "zz", PageRequest.of(0, 20, Sort.by("title").descending()));

        assertThat(mappedTitles)
                .containsExactly("Zz Yoga Mat", "Zz Running Shoes", "Zz Laptop Stand");
    }

    // ---------- helpers ----------

    private Tag tag(String name) {
        Tag tag = new Tag();
        tag.setName(name);
        return tagRepository.save(tag);
    }

    private void offer(String title, String niche, Long advertiserId, Tag... tags) {
        Offer offer = new Offer(advertiserId, title, "description", "https://example.com", niche);
        offer.setTags(new HashSet<>(Arrays.asList(tags)));
        offerRepository.save(offer);
    }
}
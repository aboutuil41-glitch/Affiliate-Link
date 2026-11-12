package com.affiliateLink.offer.internal.services;

import com.affiliateLink.common.exception.BadRequestException;
import com.affiliateLink.common.exception.BusinessRuleException;
import com.affiliateLink.common.exception.ResourceNotFoundException;
import com.affiliateLink.offer.internal.dto.OfferRequest;
import com.affiliateLink.offer.internal.dto.OfferResponse;
import com.affiliateLink.offer.internal.dto.OfferUpdateRequest;
import com.affiliateLink.offer.internal.entities.Offer;
import com.affiliateLink.offer.internal.entities.Tag;
import com.affiliateLink.offer.internal.mappers.OfferMapper;
import com.affiliateLink.offer.internal.repositories.OfferRepository;
import com.affiliateLink.offer.internal.repositories.TagRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OfferServiceTest {

    @Mock OfferRepository offerRepository;
    @Mock TagRepository tagRepository;
    @Mock AdvertiserChecker advertiserChecker;
    @Mock OfferMapper offerMapper;

    @InjectMocks OfferService service;

    // ---------- create: advertiser rule ----------

    @Test
    void shouldRefuseOfferWhenAdvertiserIsNotActive() {
        // Given
        OfferRequest request = request(1L, Set.of("sport"));
        doThrow(new BusinessRuleException("Advertiser is not active"))
                .when(advertiserChecker).requireActive(1L);

        // When / Then
        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(BusinessRuleException.class);
        verify(offerRepository, never()).save(any());
    }

    @Test
    void shouldRefuseOfferWhenAdvertiserDoesNotExist() {
        // Given
        OfferRequest request = request(99L, Set.of("sport"));
        doThrow(new ResourceNotFoundException("Advertiser not found: 99"))
                .when(advertiserChecker).requireActive(99L);

        // When / Then
        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(offerRepository, never()).save(any());
    }

    // ---------- create: tag rules ----------

    @Test
    void shouldStoreTagsInLowercaseAndTrimmed() {
        // Given: both tags are new
        when(tagRepository.findByName("fitness")).thenReturn(Optional.empty());
        when(tagRepository.findByName("health")).thenReturn(Optional.empty());
        when(tagRepository.save(any(Tag.class))).thenAnswer(i -> i.getArgument(0));

        // When
        Set<Tag> tags = createOfferWithTags(Set.of("  Fitness ", "HEALTH"));

        // Then
        assertThat(tags).extracting(Tag::getName)
                .containsExactlyInAnyOrder("fitness", "health");
    }

    @Test
    void shouldNotDuplicateTagsThatDifferOnlyByCase() {
        // Given: "Sport" and "sport " are the same tag
        when(tagRepository.findByName("sport")).thenReturn(Optional.empty());
        when(tagRepository.save(any(Tag.class))).thenAnswer(i -> i.getArgument(0));

        // When
        Set<Tag> tags = createOfferWithTags(Set.of("Sport", "sport "));

        // Then: one tag, created once
        assertThat(tags).hasSize(1);
        verify(tagRepository, times(1)).save(any(Tag.class));
    }

    @Test
    void shouldReuseExistingTagInsteadOfCreatingANewOne() {
        // Given: the tag "sport" already exists (shared with other offers)
        Tag existing = new Tag();
        existing.setName("sport");
        when(tagRepository.findByName("sport")).thenReturn(Optional.of(existing));

        // When
        Set<Tag> tags = createOfferWithTags(Set.of("Sport"));

        // Then
        assertThat(tags).containsExactly(existing);
        verify(tagRepository, never()).save(any(Tag.class));
    }

    @Test
    void shouldIgnoreBlankTags() {
        // When
        Set<Tag> tags = createOfferWithTags(Set.of("   "));

        // Then
        assertThat(tags).isEmpty();
        verify(tagRepository, never()).save(any(Tag.class));
    }

    @Test
    void shouldCreateOfferWithNoTagsWhenTagsAreNull() {
        // When
        Set<Tag> tags = createOfferWithTags(null);

        // Then
        assertThat(tags).isEmpty();
    }

    // ---------- update ----------

    @Test
    void shouldThrowNotFoundWhenUpdatingUnknownOffer() {
        // Given
        when(offerRepository.findById(42L)).thenReturn(Optional.empty());
        OfferUpdateRequest request = mock(OfferUpdateRequest.class);

        // When / Then
        assertThatThrownBy(() -> service.update(42L, request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldNormalizeTagsWhenUpdatingAnOffer() {
        // Given
        Offer offer = mock(Offer.class);
        when(offerRepository.findById(1L)).thenReturn(Optional.of(offer));
        OfferUpdateRequest request = mock(OfferUpdateRequest.class);
        when(request.tags()).thenReturn(Set.of("  GYM "));
        when(tagRepository.findByName("gym")).thenReturn(Optional.empty());
        when(tagRepository.save(any(Tag.class))).thenAnswer(i -> i.getArgument(0));

        // When
        service.update(1L, request);

        // Then
        ArgumentCaptor<Set<Tag>> captor = ArgumentCaptor.captor();
        verify(offer).setTags(captor.capture());
        assertThat(captor.getValue()).extracting(Tag::getName).containsExactly("gym");
    }

    // ---------- get ----------

    @Test
    void shouldThrowNotFoundWhenOfferDoesNotExist() {
        // Given
        when(offerRepository.findById(42L)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> service.get(42L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ---------- search ----------

    @Test
    void shouldRejectSortOnAFieldThatIsNotAllowed() {
        // Given
        Pageable pageable = PageRequest.of(0, 10, Sort.by("banana"));

        // When / Then
        assertThatThrownBy(() -> service.search(null, null, null, null, pageable))
                .isInstanceOf(BadRequestException.class);
        verify(offerRepository, never())
                .findAll(ArgumentMatchers.<Specification<Offer>>any(), any(Pageable.class));
    }

    @Test
    void shouldAcceptSortOnAllowedFields() {
        // Given
        Offer offer = mock(Offer.class);
        OfferResponse response = mock(OfferResponse.class);
        Page<Offer> page = new PageImpl<>(List.of(offer));
        when(offerRepository.findAll(ArgumentMatchers.<Specification<Offer>>any(), any(Pageable.class)))
                .thenReturn(page);
        when(offerMapper.toResponse(offer)).thenReturn(response);

        for (String field : List.of("title", "createdAt", "niche")) {
            // When
            Page<OfferResponse> result = service.search(null, null, null, null,
                    PageRequest.of(0, 10, Sort.by(field)));

            // Then
            assertThat(result.getContent()).containsExactly(response);
        }
    }

    @Test
    void shouldSearchWithNoCriteriaAtAll() {
        // Given
        Offer offer = mock(Offer.class);
        OfferResponse response = mock(OfferResponse.class);
        when(offerRepository.findAll(ArgumentMatchers.<Specification<Offer>>any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(offer)));
        when(offerMapper.toResponse(offer)).thenReturn(response);

        // When
        Page<OfferResponse> result = service.search(null, null, null, null, PageRequest.of(0, 20));

        // Then
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    // ---------- helpers ----------

    // Runs create() with the given tag names and returns the tags the offer ended up with
    private Set<Tag> createOfferWithTags(Set<String> tagNames) {
        OfferRequest request = request(1L, tagNames);
        Offer offer = mock(Offer.class);
        when(offerMapper.toEntity(request)).thenReturn(offer);
        when(offerRepository.save(offer)).thenReturn(offer);

        service.create(request);

        ArgumentCaptor<Set<Tag>> captor = ArgumentCaptor.captor();
        verify(offer).setTags(captor.capture());
        return captor.getValue();
    }

    // OfferRequest is faked so we don't depend on its constructor
    private OfferRequest request(Long advertiserId, Set<String> tags) {
        OfferRequest r = mock(OfferRequest.class);
        lenient().when(r.advertiserId()).thenReturn(advertiserId);
        lenient().when(r.tags()).thenReturn(tags);
        return r;
    }
}
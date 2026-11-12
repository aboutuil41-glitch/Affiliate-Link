package com.affiliateLink.offer.internal.services;
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

}
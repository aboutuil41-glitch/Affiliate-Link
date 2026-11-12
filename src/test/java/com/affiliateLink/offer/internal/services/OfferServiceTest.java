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

}
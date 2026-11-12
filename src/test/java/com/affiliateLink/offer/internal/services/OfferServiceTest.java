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

}
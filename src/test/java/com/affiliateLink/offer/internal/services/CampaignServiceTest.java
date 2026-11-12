package com.affiliateLink.offer.internal.services;
class CampaignServiceTest {

    @Mock CampaignRepository campaignRepository;
    @Mock OfferRepository offerRepository;
    @Mock AdvertiserChecker advertiserChecker;
    @Mock CampaignMapper campaignMapper;

    @InjectMocks CampaignService service;

    @Test
    void shouldThrowNotFoundWhenCampaignDoesNotExist() {
        when(campaignRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.changeStatus(99L, Campaign.Status.ACTIVE))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldPauseTheOtherActiveCampaignWhenActivating() {
        Offer offer = mock(Offer.class);
        when(offer.getId()).thenReturn(5L);
        Campaign toActivate = campaign(1L, Campaign.Status.DRAFT, offer);
        Campaign alreadyActive = campaign(2L, Campaign.Status.ACTIVE, offer);

        when(campaignRepository.findById(1L)).thenReturn(Optional.of(toActivate));
        when(campaignRepository.findByOfferIdAndStatus(5L, Campaign.Status.ACTIVE))
                .thenReturn(List.of(toActivate, alreadyActive));

        service.changeStatus(1L, Campaign.Status.ACTIVE);

        assertThat(toActivate.getStatus()).isEqualTo(Campaign.Status.ACTIVE);
        assertThat(alreadyActive.getStatus()).isEqualTo(Campaign.Status.PAUSED);
    }

    @Test
    void shouldRefuseForbiddenStatusChange() {
        // Given: an ended campaign can't go anywhere
        Offer offer = mock(Offer.class);
        Campaign ended = campaign(1L, Campaign.Status.ENDED, offer);
        when(campaignRepository.findById(1L)).thenReturn(Optional.of(ended));

        // When / Then
        assertThatThrownBy(() -> service.changeStatus(1L, Campaign.Status.ACTIVE))
                .isInstanceOf(BusinessRuleException.class);
        assertThat(ended.getStatus()).isEqualTo(Campaign.Status.ENDED);
    }

    // ---------- create ----------

    @Test
    void shouldThrowNotFoundWhenOfferDoesNotExist() {
        // Given
        when(offerRepository.findById(7L)).thenReturn(Optional.empty());
        CampaignRequest request = request(today(), today().plusDays(10), "100",
                Campaign.PayoutModel.CPC, "5");

        // When / Then
        assertThatThrownBy(() -> service.create(7L, request))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(campaignRepository, never()).save(any());
    }

    @Test
    void shouldRefuseCampaignWhenAdvertiserIsNotActive() {
        // Given
        Offer offer = mock(Offer.class);
        when(offer.getAdvertiserId()).thenReturn(1L);
        when(offerRepository.findById(7L)).thenReturn(Optional.of(offer));
        doThrow(new BusinessRuleException("Advertiser is not active"))
                .when(advertiserChecker).requireActive(1L);
        CampaignRequest request = request(today(), today().plusDays(10), "100",
                Campaign.PayoutModel.CPC, "5");

        // When / Then
        assertThatThrownBy(() -> service.create(7L, request))
                .isInstanceOf(BusinessRuleException.class);
        verify(campaignRepository, never()).save(any());
    }

    @Test
    void shouldCreateCampaignWhenRequestIsValid() {
        // Given
        Offer offer = mock(Offer.class);
        when(offerRepository.findById(7L)).thenReturn(Optional.of(offer));
        CampaignRequest request = request(today(), today().plusDays(10), "100",
                Campaign.PayoutModel.CPC, "5");
        Campaign entity = new Campaign();
        CampaignResponse response = mock(CampaignResponse.class);
        when(campaignMapper.toEntity(request)).thenReturn(entity);
        when(campaignRepository.save(entity)).thenReturn(entity);
        when(campaignMapper.toResponse(entity)).thenReturn(response);

        // When
        CampaignResponse result = service.create(7L, request);

        // Then
        assertThat(result).isSameAs(response);
        assertThat(entity.getOffer()).isSameAs(offer);
        verify(campaignRepository).save(entity);
    }

    // ---------- validation rules ----------

    @Test
    void shouldRejectEndDateEqualToStartDate() {
        assertCreateIsRejected(request(today(), today(), "100", Campaign.PayoutModel.CPC, "5"));
    }

    @Test
    void shouldRejectEndDateBeforeStartDate() {
        assertCreateIsRejected(
                request(today(), today().minusDays(1), "100", Campaign.PayoutModel.CPC, "5"));
    }

    @Test
    void shouldRejectBudgetOfZero() {
        assertCreateIsRejected(
                request(today(), today().plusDays(10), "0", Campaign.PayoutModel.CPC, "5"));
    }

    @ParameterizedTest(name = "{0} with value {1} is rejected")
    @CsvSource({
            "CPC, 0",
            "CPC, -1",
            "CPL, 0",
            "CPA_FIXED, 0",
            "CPA_PERCENT, 0",
            "CPA_PERCENT, -5",
            "CPA_PERCENT, 100.01",
            "CPA_PERCENT, 101"
    })
    void shouldRejectInvalidPayout(Campaign.PayoutModel model, BigDecimal value) {
        assertCreateIsRejected(
                request(today(), today().plusDays(10), "100", model, value.toPlainString()));
    }

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


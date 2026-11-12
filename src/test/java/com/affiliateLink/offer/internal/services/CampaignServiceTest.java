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


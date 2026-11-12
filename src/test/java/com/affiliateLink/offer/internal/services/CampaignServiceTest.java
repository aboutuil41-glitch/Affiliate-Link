package com.affiliateLink.offer.internal.services;
class CampaignServiceTest {

    @Mock CampaignRepository campaignRepository;
    @Mock OfferRepository offerRepository;
    @Mock AdvertiserChecker advertiserChecker;
    @Mock CampaignMapper campaignMapper;

    @InjectMocks CampaignService service;


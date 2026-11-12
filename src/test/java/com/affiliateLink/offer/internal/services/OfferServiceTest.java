package com.affiliateLink.offer.internal.services;
@ExtendWith(MockitoExtension.class)
class OfferServiceTest {

    @Mock OfferRepository offerRepository;
    @Mock TagRepository tagRepository;
    @Mock AdvertiserChecker advertiserChecker;
    @Mock OfferMapper offerMapper;

    @InjectMocks OfferService service;

    // ---------- create: advertiser rule ----------

}
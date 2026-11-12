package com.affiliateLink.offer.internal.services;
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

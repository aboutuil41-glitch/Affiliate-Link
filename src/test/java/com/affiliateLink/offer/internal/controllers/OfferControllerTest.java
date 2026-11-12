package com.affiliateLink.offer.internal.controllers;
class OfferControllerTest {

    @Autowired MockMvc mvc;

    @MockitoBean OfferService offerService; // fake service, Spring wires it into the controller

    private static final String VALID_OFFER = """
            {"advertiserId":1,"title":"Yoga Mat","description":"Non-slip mat",
             "landingUrl":"https://fitgear.com/yoga","niche":"fitness","tags":["fitness"]}
            """;

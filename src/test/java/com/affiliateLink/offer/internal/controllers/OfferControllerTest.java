package com.affiliateLink.offer.internal.controllers;
class OfferControllerTest {

    @Autowired MockMvc mvc;

    @MockitoBean OfferService offerService; // fake service, Spring wires it into the controller

    private static final String VALID_OFFER = """
            {"advertiserId":1,"title":"Yoga Mat","description":"Non-slip mat",
             "landingUrl":"https://fitgear.com/yoga","niche":"fitness","tags":["fitness"]}
            """;

    // ---------- POST /api/offres ----------

    @Test
    void shouldReturn201WhenOfferIsValid() throws Exception {
        mvc.perform(post("/api/offres")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_OFFER))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldReturn400WhenOfferBodyIsInvalid() throws Exception {
        mvc.perform(post("/api/offres")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }


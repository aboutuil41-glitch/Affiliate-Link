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

    @Test
    void shouldReturn409WhenAdvertiserIsNotActive() throws Exception {
        when(offerService.create(any())).thenThrow(new BusinessRuleException("Advertiser is not active"));

        mvc.perform(post("/api/offres")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_OFFER))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void shouldReturn404WhenAdvertiserDoesNotExist() throws Exception {
        when(offerService.create(any())).thenThrow(new ResourceNotFoundException("Advertiser not found"));

        mvc.perform(post("/api/offres")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_OFFER))
                .andExpect(status().isNotFound());
    }

    // ---------- GET /api/offres ----------

    @Test
    void shouldReturn200WhenSearchingOffers() throws Exception {
        when(offerService.search(any(), any(), any(), any(), any())).thenReturn(Page.empty());

        mvc.perform(get("/api/offres?niche=fitness"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturn400WhenSortFieldIsInvalid() throws Exception {
        when(offerService.search(any(), any(), any(), any(), any()))
                .thenThrow(new BadRequestException("Invalid sort field: banana"));

        mvc.perform(get("/api/offres?sort=banana"))
                .andExpect(status().isBadRequest());
    }

    // ---------- GET /api/offres/{id} ----------

    @Test
    void shouldReturn200WhenOfferExists() throws Exception {
        mvc.perform(get("/api/offres/1"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturn404WhenOfferDoesNotExist() throws Exception {
        when(offerService.get(99L)).thenThrow(new ResourceNotFoundException("Offer not found: 99"));

        mvc.perform(get("/api/offres/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }

    // ---------- PUT /api/offres/{id} ----------

    @Test
    void shouldReturn200WhenOfferIsUpdated() throws Exception {
        mvc.perform(put("/api/offres/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_OFFER))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturn400WhenUpdateBodyIsInvalid() throws Exception {
        mvc.perform(put("/api/offres/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn404WhenUpdatingUnknownOffer() throws Exception {
        when(offerService.update(anyLong(), any()))
                .thenThrow(new ResourceNotFoundException("Offer not found: 99"));

        mvc.perform(put("/api/offres/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_OFFER))
                .andExpect(status().isNotFound());
    }
}
package com.affiliateLink.offer.internal.controllers;
class CampaignControllerTest {

    @Autowired MockMvc mvc;

    @MockitoBean CampaignService campaignService; // fake service

    private static final String VALID_CAMPAIGN = """
            {"name":"Black Friday","startDate":"2026-11-20","endDate":"2026-12-05",
             "budget":4000,"payoutModel":"CPA_PERCENT","payoutValue":10}
            """;

    // ---------- POST /api/offres/{offerId}/campaigns ----------

    @Test
    void shouldReturn201WhenCampaignIsValid() throws Exception {
        mvc.perform(post("/api/offres/1/campaigns")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_CAMPAIGN))
                .andExpect(status().isCreated());
    }

package com.affiliateLink.offer.internal.controllers;

import com.affiliateLink.common.exception.BusinessRuleException;
import com.affiliateLink.common.exception.ResourceNotFoundException;
import com.affiliateLink.offer.internal.entities.Campaign;
import com.affiliateLink.offer.internal.services.CampaignService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CampaignController.class)
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

    @Test
    void shouldReturn400WhenCampaignBodyIsInvalid() throws Exception {
        mvc.perform(post("/api/offres/1/campaigns")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn404WhenOfferDoesNotExist() throws Exception {
        when(campaignService.create(anyLong(), any()))
                .thenThrow(new ResourceNotFoundException("Offer not found: 99"));

        mvc.perform(post("/api/offres/99/campaigns")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_CAMPAIGN))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn409WhenAdvertiserIsNotActive() throws Exception {
        when(campaignService.create(anyLong(), any()))
                .thenThrow(new BusinessRuleException("Advertiser is not active"));

        mvc.perform(post("/api/offres/1/campaigns")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_CAMPAIGN))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").exists());
    }

    // ---------- GET /api/campaigns ----------

    @Test
    void shouldReturn200WhenListingCampaigns() throws Exception {
        when(campaignService.list(Campaign.Status.ACTIVE)).thenReturn(List.of());

        mvc.perform(get("/api/campaigns?status=ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    // ---------- PATCH /api/campaigns/{id}/status ----------

    @Test
    void shouldReturn204WhenStatusIsChanged() throws Exception {
        mvc.perform(patch("/api/campaigns/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"ACTIVE\"}"))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturn404WhenCampaignDoesNotExist() throws Exception {
        doThrow(new ResourceNotFoundException("Campaign not found: 99"))
                .when(campaignService).changeStatus(99L, Campaign.Status.ACTIVE);

        mvc.perform(patch("/api/campaigns/99/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"ACTIVE\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn409WhenStatusChangeIsForbidden() throws Exception {
        doThrow(new BusinessRuleException("Cannot change campaign status from ENDED to ACTIVE"))
                .when(campaignService).changeStatus(1L, Campaign.Status.ACTIVE);

        mvc.perform(patch("/api/campaigns/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"ACTIVE\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").exists());
    }
}
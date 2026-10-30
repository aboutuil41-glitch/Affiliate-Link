package com.affiliateLink.offer.internal.controllers;

import com.affiliateLink.offer.internal.dto.CampaignRequest;
import com.affiliateLink.offer.internal.dto.CampaignResponse;
import com.affiliateLink.offer.internal.dto.StatusRequest;
import com.affiliateLink.offer.internal.entities.Campaign;
import com.affiliateLink.offer.internal.services.CampaignService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CampaignController {

    private final CampaignService campaignService;

    public CampaignController(CampaignService campaignService) {
        this.campaignService = campaignService;
    }

    @PostMapping("/offres/{offerId}/campaigns")
    public ResponseEntity<CampaignResponse> create(@PathVariable Long offerId,
                                                   @Valid @RequestBody CampaignRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(campaignService.create(offerId, request));
    }

    @GetMapping("/campaigns")
    public List<CampaignResponse> list(@RequestParam(required = false) Campaign.Status status) {
        return campaignService.list(status);
    }

    @PatchMapping("/campaigns/{id}/status")
    public ResponseEntity<Void> changeStatus(@PathVariable Long id,
                                             @Valid @RequestBody StatusRequest request) {
        campaignService.changeStatus(id, request.status());
        return ResponseEntity.noContent().build();
    }
}
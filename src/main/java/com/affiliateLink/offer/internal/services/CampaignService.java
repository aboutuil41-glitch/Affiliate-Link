package com.affiliateLink.offer.internal.services;

import com.affiliateLink.common.exception.BadRequestException;
import com.affiliateLink.common.exception.BusinessRuleException;
import com.affiliateLink.common.exception.ResourceNotFoundException;
import com.affiliateLink.offer.internal.dto.CampaignRequest;
import com.affiliateLink.offer.internal.dto.CampaignResponse;
import com.affiliateLink.offer.internal.entities.Campaign;
import com.affiliateLink.offer.internal.entities.Offer;
import com.affiliateLink.offer.internal.mappers.CampaignMapper;
import com.affiliateLink.offer.internal.repositories.CampaignRepository;
import com.affiliateLink.offer.internal.repositories.OfferRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;


@Service
@Transactional
public class CampaignService {

    private static final Logger log = LoggerFactory.getLogger(CampaignService.class);

    private final CampaignRepository campaignRepository;
    private final OfferRepository offerRepository;
    private final AdvertiserChecker advertiserChecker;
    private final CampaignMapper campaignMapper;

    public CampaignService(CampaignRepository campaignRepository, OfferRepository offerRepository,
                           AdvertiserChecker advertiserChecker, CampaignMapper campaignMapper) {
        this.campaignRepository = campaignRepository;
        this.offerRepository = offerRepository;
        this.advertiserChecker = advertiserChecker;
        this.campaignMapper = campaignMapper;
    }

    public CampaignResponse create(Long offerId, CampaignRequest request) {
        Offer offer = offerRepository.findById(offerId)
                .orElseThrow(() -> new ResourceNotFoundException("Offer not found: " + offerId));
        advertiserChecker.requireActive(offer.getAdvertiserId());
        validate(request);

        Campaign campaign = campaignMapper.toEntity(request);
        campaign.setOffer(offer);
        Campaign saved = campaignRepository.save(campaign);
        log.info("Campaign {} created for offer {}", saved.getId(), offerId);
        return campaignMapper.toResponse(saved);
    }

    public void changeStatus(Long campaignId, Campaign.Status target) {
        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found: " + campaignId));

        Campaign.Status from = campaign.getStatus();
        try {
            campaign.changeStatus(target);
        } catch (BusinessRuleException e) {
            log.warn("Campaign {} status change refused: {} -> {}", campaignId, from, target);
            throw e;
        }

        // Only one ACTIVE campaign per offer: pause the others
        if (target == Campaign.Status.ACTIVE) {
            List<Campaign> active = campaignRepository
                    .findByOfferIdAndStatus(campaign.getOffer().getId(), Campaign.Status.ACTIVE);
            for (Campaign other : active) {
                if (!other.getId().equals(campaignId)) {
                    other.changeStatus(Campaign.Status.PAUSED);
                    log.info("Campaign {} paused because campaign {} was activated",
                            other.getId(), campaignId);
                }
            }
        }
        log.info("Campaign {} status changed: {} -> {}", campaignId, from, target);
    }

    @Transactional(readOnly = true)
    public List<CampaignResponse> list(Campaign.Status status) {
        List<Campaign> campaigns = (status == null)
                ? campaignRepository.findAll()
                : campaignRepository.findByStatus(status);
        return campaigns.stream().map(campaignMapper::toResponse).toList();
    }

    private void validate(CampaignRequest r) {
        if (!r.endDate().isAfter(r.startDate())) {
            throw new BadRequestException("End date must be after start date");
        }
        if (r.budget().signum() <= 0) {
            throw new BadRequestException("Budget must be greater than 0");
        }
        if (r.payoutValue().signum() <= 0) {
            throw new BadRequestException("Payout value must be greater than 0");
        }
        if (r.payoutModel() == Campaign.PayoutModel.CPA_PERCENT
                && r.payoutValue().compareTo(new BigDecimal("100")) > 0) {
            throw new BadRequestException("Percentage must be between 0 and 100");
        }
    }
}
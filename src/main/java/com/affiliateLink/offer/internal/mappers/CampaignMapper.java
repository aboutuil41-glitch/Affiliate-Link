package com.affiliateLink.offer.internal.mappers;

import com.affiliateLink.offer.internal.dto.CampaignRequest;
import com.affiliateLink.offer.internal.dto.CampaignResponse;
import com.affiliateLink.offer.internal.entities.Campaign;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CampaignMapper {

    @Mapping(target = "offerId", source = "offer.id")
    CampaignResponse toResponse(Campaign campaign);

    @Mapping(target = "offer", ignore = true)
    Campaign toEntity(CampaignRequest request);
}
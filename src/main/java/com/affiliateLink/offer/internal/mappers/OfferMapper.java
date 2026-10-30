package com.affiliateLink.offer.internal.mappers;

import com.affiliateLink.offer.internal.dto.OfferDetailResponse;
import com.affiliateLink.offer.internal.dto.OfferRequest;
import com.affiliateLink.offer.internal.dto.OfferResponse;
import com.affiliateLink.offer.internal.dto.OfferUpdateRequest;
import com.affiliateLink.offer.internal.entities.Offer;
import com.affiliateLink.offer.internal.entities.Tag;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = CampaignMapper.class)
public interface OfferMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "campaigns", ignore = true)
    @Mapping(target = "tags", ignore = true)
    Offer toEntity(OfferRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "advertiserId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "campaigns", ignore = true)
    @Mapping(target = "tags", ignore = true)
    void updateEntity(OfferUpdateRequest request, @MappingTarget Offer offer);

    OfferResponse toResponse(Offer offer);

    OfferDetailResponse toDetailResponse(Offer offer);

    default String tagToString(Tag tag) {
        return tag.getName();
    }
}
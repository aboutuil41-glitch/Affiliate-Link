package com.affiliateLink.profile.services;


import com.affiliateLink.common.exception.BusinessRuleException;
import com.affiliateLink.common.exception.ResourceNotFoundException;
import com.affiliateLink.profile.dto.AdvertiserRequest;
import com.affiliateLink.profile.dto.AdvertiserResponse;
import com.affiliateLink.profile.dto.PublisherResponse;
import com.affiliateLink.profile.entities.Advertiser;
import com.affiliateLink.profile.entities.Publisher;
import com.affiliateLink.profile.repositories.AdvertiserRepository;
import com.affiliateLink.profile.repositories.ProfileRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdvertiserService {
    private final AdvertiserRepository ar;
    private final ProfileRepository pr;

    public AdvertiserService(AdvertiserRepository ar, ProfileRepository pr){
        this.pr = pr;
        this.ar = ar;
    }

    public AdvertiserResponse create(AdvertiserRequest request){
        if (pr.existsByEmail(request.email())) {
            throw new BusinessRuleException("Email already in use: " + request.email());
        }
            Advertiser advertiser = new Advertiser(request.companyName(), request.sector(), request.website(), request.email());
        return AdvertiserResponse.from(ar.save(advertiser));
    }

    public List<AdvertiserResponse> getAll(){
        return ar.findAll().stream().map(AdvertiserResponse::from).toList();
    }

    public AdvertiserResponse getById(Long id){
        Advertiser advertiser = ar.findById(id).orElseThrow(() -> new ResourceNotFoundException("Publisher not found: " + id));
        return AdvertiserResponse.from(advertiser);
    }
}

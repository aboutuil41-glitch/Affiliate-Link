package com.affiliateLink.profile.internal.services;


import com.affiliateLink.common.exception.BusinessRuleException;
import com.affiliateLink.common.exception.ResourceNotFoundException;
import com.affiliateLink.profile.internal.dto.AdvertiserRequest;
import com.affiliateLink.profile.internal.dto.AdvertiserResponse;
import com.affiliateLink.profile.internal.entities.Advertiser;
import com.affiliateLink.profile.internal.repositories.AdvertiserRepository;
import com.affiliateLink.profile.internal.repositories.ProfileRepository;
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

package com.affiliateLink.profile.services;


import com.affiliateLink.profile.dto.AdvertiserRequest;
import com.affiliateLink.profile.dto.AdvertiserResponse;
import com.affiliateLink.profile.dto.PublisherResponse;
import com.affiliateLink.profile.entities.Advertiser;
import com.affiliateLink.profile.entities.Publisher;
import com.affiliateLink.profile.repositories.AdvertiserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdvertiserService {
    private final AdvertiserRepository ar;

    public AdvertiserService(AdvertiserRepository ar){
        this.ar = ar;
    }

    public AdvertiserResponse create(AdvertiserRequest request){
        Advertiser advertiser = new Advertiser(request.companyName(), request.sector(), request.website(), request.email());
        return AdvertiserResponse.from(ar.save(advertiser));
    }

    public List<AdvertiserResponse> getAll(){
        return ar.findAll().stream().map(AdvertiserResponse::from).toList();
    }

    public AdvertiserResponse getById(Long id){
        Advertiser advertiser = ar.findById(id).orElseThrow();
        return AdvertiserResponse.from(advertiser);
    }
}

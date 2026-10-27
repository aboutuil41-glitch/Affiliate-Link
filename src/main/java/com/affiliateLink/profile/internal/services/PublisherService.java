package com.affiliateLink.profile.internal.services;

import com.affiliateLink.common.exception.BusinessRuleException;
import com.affiliateLink.common.exception.ResourceNotFoundException;
import com.affiliateLink.profile.internal.dto.PublisherRequest;
import com.affiliateLink.profile.internal.dto.PublisherResponse;
import com.affiliateLink.profile.internal.entities.Publisher;
import com.affiliateLink.profile.internal.repositories.ProfileRepository;
import com.affiliateLink.profile.internal.repositories.PublisherRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PublisherService {
    private final PublisherRepository pr;
    private final ProfileRepository por;

    public PublisherService(PublisherRepository pr, ProfileRepository por){
        this.pr = pr;
        this.por = por;
    }

    public PublisherResponse create(PublisherRequest request){
        if (por.existsByEmail(request.email())) {
            throw new BusinessRuleException("Email already in use: " + request.email());
        }
        Publisher publisher = new Publisher(request.firstName(), request.lastName(), request.niche(), request.bio(), request.country(), request.email());
        return PublisherResponse.from(pr.save(publisher));
    }

    public List<PublisherResponse> getAll(){
        return pr.findAll().stream().map(PublisherResponse::from).toList();
    }

    public PublisherResponse getById(Long id){
        Publisher publisher = pr.findById(id).orElseThrow(() -> new ResourceNotFoundException("Publisher not found: " + id));
        return PublisherResponse.from(publisher);
    }
}

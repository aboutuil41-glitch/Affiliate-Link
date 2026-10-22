package com.affiliateLink.profile.services;

import com.affiliateLink.profile.dto.PublisherRequest;
import com.affiliateLink.profile.dto.PublisherResponse;
import com.affiliateLink.profile.entities.Publisher;
import com.affiliateLink.profile.repositories.PublisherRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PublisherService {
    private final PublisherRepository pr;

    public PublisherService(PublisherRepository pr){
        this.pr = pr;
    }

    public PublisherResponse create(PublisherRequest request){
        Publisher publisher = new Publisher(request.firstName(), request.lastName(), request.niche(), request.bio(), request.country(), request.email());
        return PublisherResponse.from(pr.save(publisher));
    }

    public List<PublisherResponse> getAll(){
        return pr.findAll().stream().map(PublisherResponse::from).toList();
    }

    public PublisherResponse getById(Long id){
        Publisher publisher = pr.findById(id).orElseThrow();
        return PublisherResponse.from(publisher);
    }
}

package com.affiliateLink.profile.services;
import com.affiliateLink.profile.repositories.PublisherRepository;
@Service
public class PublisherService {
    public PublisherService(PublisherRepository pr){
        this.pr = pr;
    }
}

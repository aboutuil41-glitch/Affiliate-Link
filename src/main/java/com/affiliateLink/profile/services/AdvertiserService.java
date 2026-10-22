package com.affiliateLink.profile.services;
@Service
public class AdvertiserService {
    private final AdvertiserRepository ar;

    public AdvertiserService(AdvertiserRepository ar){
        this.ar = ar;
    }

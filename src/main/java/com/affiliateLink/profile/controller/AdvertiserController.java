package com.affiliateLink.profile.controller;
@RestController
@RequestMapping("/api/advertiser")
public class AdvertiserController {
    private final AdvertiserService as;

    public AdvertiserController(AdvertiserService as) {
        this.as = as;
    }
}

package com.affiliateLink.profile.controller;
@RestController
@RequestMapping("/api/publishers")
public class PublisherController {
    private final PublisherService ps;

    public PublisherController(PublisherService ps){
        this.ps = ps;
    }
}

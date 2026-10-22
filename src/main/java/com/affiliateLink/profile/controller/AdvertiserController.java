package com.affiliateLink.profile.controller;


import com.affiliateLink.profile.dto.AdvertiserRequest;
import com.affiliateLink.profile.dto.AdvertiserResponse;
import com.affiliateLink.profile.dto.PublisherResponse;
import com.affiliateLink.profile.services.AdvertiserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/advertiser")
public class AdvertiserController {
    private final AdvertiserService as;

    public AdvertiserController(AdvertiserService as) {
        this.as = as;
    }

    @PostMapping
    public AdvertiserResponse create(@RequestBody @Valid AdvertiserRequest request){
        return as.create(request);
    }

    @GetMapping
    public List<AdvertiserResponse> getAll(){
        return as.getAll();
    }

    @GetMapping("/{id}")
    public AdvertiserResponse getById(@PathVariable Long id){
        return as.getById(id);
    }
}

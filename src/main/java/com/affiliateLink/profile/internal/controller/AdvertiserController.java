package com.affiliateLink.profile.internal.controller;


import com.affiliateLink.profile.internal.dto.AdvertiserRequest;
import com.affiliateLink.profile.internal.dto.AdvertiserResponse;
import com.affiliateLink.profile.internal.services.AdvertiserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/advertisers")
public class AdvertiserController {
    private final AdvertiserService as;

    public AdvertiserController(AdvertiserService as) {
        this.as = as;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
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

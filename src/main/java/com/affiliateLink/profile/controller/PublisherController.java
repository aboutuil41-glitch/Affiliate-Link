package com.affiliateLink.profile.controller;


import com.affiliateLink.profile.dto.PublisherRequest;
import com.affiliateLink.profile.dto.PublisherResponse;
import com.affiliateLink.profile.services.PublisherService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/publishers")
public class PublisherController {
    private final PublisherService ps;

    public PublisherController(PublisherService ps){
        this.ps = ps;
    }

    @PostMapping
    public PublisherResponse create(@RequestBody @Valid PublisherRequest pr){
        return ps.create(pr);
    }

    @GetMapping
    public List<PublisherResponse> getAll(){
        return ps.getAll();
    }

    @GetMapping("/{id}")
    public PublisherResponse getById(@PathVariable Long id){
        return ps.getById(id);
    }
}

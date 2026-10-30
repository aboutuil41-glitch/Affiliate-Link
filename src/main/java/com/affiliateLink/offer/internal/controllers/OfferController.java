package com.affiliateLink.offer.internal.controllers;

import com.affiliateLink.offer.internal.dto.*;
import com.affiliateLink.offer.internal.services.OfferService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/offres")
public class OfferController {

    private final OfferService offerService;

    public OfferController(OfferService offerService) {
        this.offerService = offerService;
    }

    @PostMapping
    public ResponseEntity<OfferResponse> create(@Valid @RequestBody OfferRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(offerService.create(request));
    }

    @GetMapping
    public Page<OfferResponse> search(
            @RequestParam(required = false) String niche,
            @RequestParam(required = false) Long advertiserId,
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {
        return offerService.search(niche, advertiserId, tag, keyword, pageable);
    }

    @GetMapping("/{id}")
    public OfferDetailResponse get(@PathVariable Long id) {
        return offerService.get(id);
    }

    @PutMapping("/{id}")
    public OfferResponse update(@PathVariable Long id,
                                @Valid @RequestBody OfferUpdateRequest request) {
        return offerService.update(id, request);
    }
}
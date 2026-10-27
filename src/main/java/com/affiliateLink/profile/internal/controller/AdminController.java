package com.affiliateLink.profile.internal.controller;

import com.affiliateLink.profile.internal.dto.StatusRequest;
import com.affiliateLink.profile.internal.services.ProfileService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/profiles")
public class AdminController {
    private final ProfileService profileService;

    public AdminController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @PatchMapping("/{id}/status")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changeStatus(@PathVariable Long id, @RequestBody @Valid StatusRequest request) {
        profileService.changeStatus(id, request.status());
    }
}
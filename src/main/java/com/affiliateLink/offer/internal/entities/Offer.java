package com.affiliateLink.offer.internal.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "offers")
@Getter
@Setter
@NoArgsConstructor
public class Offer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "advertiser_id", nullable = false)
    private Long advertiserId;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "landing_url", nullable = false, length = 500)
    private String landingUrl;

    @Column(nullable = false)
    private String niche;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;


    @OneToMany(mappedBy = "offer", cascade = CascadeType.ALL)
    private List<Campaign> campaigns = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "offer_tags",
            joinColumns = @JoinColumn(name = "offer_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();


    public Offer(Long advertiserId, String title, String description,
                 String landingUrl, String niche) {
        this.advertiserId = advertiserId;
        this.title = title;
        this.description = description;
        this.landingUrl = landingUrl;
        this.niche = niche;
    }
    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public void addCampaign(Campaign campaign) {
        campaigns.add(campaign);
        campaign.setOffer(this);
    }
}
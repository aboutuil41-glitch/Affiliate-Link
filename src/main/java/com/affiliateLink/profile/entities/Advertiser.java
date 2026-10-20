package com.affiliateLink.profile.entities;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "Advertisers")
public class Advertiser extends Profile{

    @Column(nullable = false)
    String companyName;

    @Column(nullable = false)
    String sector;

    @Column(nullable = false)
    String webSite;

    protected Advertiser(){}

    public Advertiser(String companyName, String sector, String webSite, String email) {
        this.companyName = companyName;
        this.sector = sector;
        this.webSite = webSite;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getWebSite() {
        return webSite;
    }

    public void setWebSite(String webSite) {
        this.webSite = webSite;
    }
}

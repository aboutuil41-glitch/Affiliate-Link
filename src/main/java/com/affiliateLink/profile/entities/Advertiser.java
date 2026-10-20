package com.affiliateLink.profile.entities;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "advertisers")
public class Advertiser extends Profile{

    @Column(nullable = false)
    String companyName;

    @Column(nullable = false)
    String sector;

    @Column(nullable = false)
    String website;

    protected Advertiser(){}

    public Advertiser(String companyName, String sector, String website, String email) {
        super(email);
        this.companyName = companyName;
        this.sector = sector;
        this.website = website;
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

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }
}

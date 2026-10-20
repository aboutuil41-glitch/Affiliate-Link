package com.affiliateLink.profile.entities;


import jakarta.persistence.*;

@Entity
@Table(name = "publishers")
public class Publisher extends Profile{

    @Column(nullable = false)
    String firstName;

    @Column(nullable = false)
    String lastName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    Niche niche;

    String bio;

    @Column(nullable = false)
    String country;


    public enum Niche{
        EATING,
        GAMING
    }

    protected Publisher() {}

    public Publisher(String firstName, String lastName, Niche niche, String bio, String country, String email) {
        super(email);
        this.firstName = firstName;
        this.lastName = lastName;
        this.niche = niche;
        this.bio = bio;
        this.country = country;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Niche getNiche() {
        return niche;
    }

    public void setNiche(Niche niche) {
        this.niche = niche;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}

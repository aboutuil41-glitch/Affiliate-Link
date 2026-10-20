package com.affiliateLink.profile.entities;

import jakarta.persistence.*;

import java.time.LocalDate;


@Entity
@Table(name = "profiles")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false, unique = true)
    String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    Status status;

    @Column(nullable = false)
    LocalDate createdAt;

    public enum Status{
        PENDING,
        ACTIVE,
        SUSPENDED
    }

    protected Profile() {}

    public Profile(String email) {
        this.email = email;
        this.status = Status.PENDING;
        this.createdAt = LocalDate.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }
}

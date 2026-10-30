package com.affiliateLink.offer.internal.entities;

import com.affiliateLink.common.exception.BusinessRuleException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "campaigns")
@Getter
@Setter
@NoArgsConstructor
public class Campaign {

    public enum Status {
        DRAFT, ACTIVE, PAUSED, ENDED;

        public boolean canTransitionTo(Status target) {
            return switch (this) {
                case DRAFT -> target == ACTIVE;
                case ACTIVE -> target == PAUSED || target == ENDED;
                case PAUSED -> target == ACTIVE || target == ENDED;
                case ENDED -> false;
            };
        }
    }

    public enum PayoutModel {
        CPC, CPL, CPA_FIXED, CPA_PERCENT
    }

    @Setter(AccessLevel.NONE)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private BigDecimal budget;

    @Enumerated(EnumType.STRING)
    @Column(name = "payout_model", nullable = false)
    private PayoutModel payoutModel;

    @Column(name = "payout_value", nullable = false)
    private BigDecimal payoutValue;

    @Setter(AccessLevel.NONE)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.DRAFT;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "offer_id", nullable = false)
    private Offer offer;

    public void changeStatus(Status target) {
        if (!status.canTransitionTo(target)) {
            throw new BusinessRuleException(
                    "Cannot change campaign status from " + status + " to " + target);
        }
        if (target == Status.ACTIVE && endDate.isBefore(LocalDate.now())) {
            throw new BusinessRuleException("Campaign end date has passed, it cannot be activated");
        }
        this.status = target;
    }
}
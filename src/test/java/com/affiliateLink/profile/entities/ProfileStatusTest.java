package com.affiliateLink.profile.entities;

import com.affiliateLink.common.exception.BusinessRuleException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProfileStatusTest {

    private Publisher newPublisher() {
        return new Publisher("Sara", "Amrani", Publisher.Niche.GAMING,
                "bio", "Morocco", "sara@test.com");
    }

    @Test
    void newAccountIsPending() {
        assertEquals(Profile.Status.PENDING, newPublisher().getStatus());
    }

    @Test
    void pendingToActive() {
        Publisher p = newPublisher();
        p.changeStatus(Profile.Status.ACTIVE);
        assertEquals(Profile.Status.ACTIVE, p.getStatus());
    }

    @Test
    void activeToSuspended() {
        Publisher p = newPublisher();
        p.changeStatus(Profile.Status.ACTIVE);
        p.changeStatus(Profile.Status.SUSPENDED);
        assertEquals(Profile.Status.SUSPENDED, p.getStatus());
    }

    @Test
    void suspendedToActive() {
        Publisher p = newPublisher();
        p.changeStatus(Profile.Status.ACTIVE);
        p.changeStatus(Profile.Status.SUSPENDED);
        p.changeStatus(Profile.Status.ACTIVE);
        assertEquals(Profile.Status.ACTIVE, p.getStatus());
    }

    @Test
    void pendingToSuspendedIsRejected() {
        Publisher p = newPublisher();
        assertThrows(BusinessRuleException.class,
                () -> p.changeStatus(Profile.Status.SUSPENDED));
    }

    @Test
    void activeToPendingIsRejected() {
        Publisher p = newPublisher();
        p.changeStatus(Profile.Status.ACTIVE);
        assertThrows(BusinessRuleException.class,
                () -> p.changeStatus(Profile.Status.PENDING));
    }

    @Test
    void suspendedToPendingIsRejected() {
        Publisher p = newPublisher();
        p.changeStatus(Profile.Status.ACTIVE);
        p.changeStatus(Profile.Status.SUSPENDED);
        assertThrows(BusinessRuleException.class,
                () -> p.changeStatus(Profile.Status.PENDING));
    }

    @Test
    void failedTransitionKeepsOldStatus() {
        Publisher p = newPublisher();
        assertThrows(BusinessRuleException.class,
                () -> p.changeStatus(Profile.Status.SUSPENDED));
        assertEquals(Profile.Status.PENDING, p.getStatus());
    }
}
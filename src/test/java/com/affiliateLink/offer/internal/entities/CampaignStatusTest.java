package com.affiliateLink.offer.internal.entities;

import com.affiliateLink.common.exception.BusinessRuleException;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


public class CampaignStatusTest {

    @Test
    void shouldAllowDraftToActive(){
        Campaign campaign = new Campaign();
        campaign.setEndDate(LocalDate.now().plusDays(10));


        campaign.changeStatus(Campaign.Status.ACTIVE);

        assertThat(campaign.getStatus()).isEqualTo(Campaign.Status.ACTIVE);
    }

    @Test
    void shouldRejectDraftToPaused(){
        Campaign campaign = new Campaign();
        campaign.setEndDate(LocalDate.now().plusDays(10));

        assertThatThrownBy(() -> campaign.changeStatus(Campaign.Status.PAUSED)).isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void shouldRejectActivationWhenEndDateIsPast() {
        Campaign campaign = new Campaign();
        campaign.setEndDate(LocalDate.now().minusDays(10));

        assertThatThrownBy(() -> campaign.changeStatus(Campaign.Status.ACTIVE)).isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void shouldAllowActivationWhenEndDateIsToday() {
        Campaign campaign = new Campaign();
        campaign.setEndDate(LocalDate.now());

        campaign.changeStatus(Campaign.Status.ACTIVE);

        assertThat(campaign.getStatus()).isEqualTo(Campaign.Status.ACTIVE);
    }

    @ParameterizedTest(name = "{0} -> {1} is allowed")
    @CsvSource({
            "DRAFT, ACTIVE",
            "ACTIVE, PAUSED",
            "ACTIVE, ENDED",
            "PAUSED, ACTIVE",
            "PAUSED, ENDED"
    })
    void shouldAllowTransition(Campaign.Status from, Campaign.Status to) {
        Campaign campaign = campaignWithStatus(from);

        campaign.changeStatus(to);

        assertThat(campaign.getStatus()).isEqualTo(to);
    }

    @ParameterizedTest(name = "{0} -> {1} is forbidden")
    @CsvSource({
            "DRAFT, DRAFT",
            "DRAFT, PAUSED",
            "DRAFT, ENDED",
            "ACTIVE, DRAFT",
            "ACTIVE, ACTIVE",
            "PAUSED, DRAFT",
            "PAUSED, PAUSED",
            "ENDED, DRAFT",
            "ENDED, ACTIVE",
            "ENDED, PAUSED",
            "ENDED, ENDED"
    })
    void shouldRejectTransition(Campaign.Status from, Campaign.Status to) {
        Campaign campaign = campaignWithStatus(from);

        assertThatThrownBy(() -> campaign.changeStatus(to))
                .isInstanceOf(BusinessRuleException.class);
        assertThat(campaign.getStatus()).isEqualTo(from);
    }

    private Campaign campaignWithStatus(Campaign.Status status) {
        Campaign campaign = new Campaign();
        campaign.setEndDate(LocalDate.now().plusDays(30));
        ReflectionTestUtils.setField(campaign, "status", status);
        return campaign;
    }

    }

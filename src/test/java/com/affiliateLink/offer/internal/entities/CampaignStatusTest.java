package com.affiliateLink.offer.internal.entities;
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


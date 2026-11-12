package com.affiliateLink.offer.internal.entities;
public class CampaignStatusTest {

    @Test
    void shouldAllowDraftToActive(){
        Campaign campaign = new Campaign();
        campaign.setEndDate(LocalDate.now().plusDays(10));


        campaign.changeStatus(Campaign.Status.ACTIVE);

        assertThat(campaign.getStatus()).isEqualTo(Campaign.Status.ACTIVE);
    }

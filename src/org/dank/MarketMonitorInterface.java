package org.dank;

import tau.tac.adx.demand.Campaign;
import tau.tac.adx.report.adn.MarketSegment;
import tau.tac.adx.report.demand.CampaignOpportunityMessage;

import java.util.Collection;
import java.util.Set;

/**
 *
 * Allocated to: Vlad
 *
 * This is effectively a wrapper on the histogram.
 *
 *  Keeps track of all running campaigns including their:
 *      market_segments
 *      reach
 *
 * Should be updated with each daily message
 *
 * Created by Stefa on 29/11/2016.
 */
interface MarketMonitor {

    Collection<Campaign> getAllCampaignsOnDay(int day);

    Collection<CampaignOpportunityMessage> getAllCampaigns();

    void addCampaign(CampaignOpportunityMessage new_camp);

    int getRequiredImpressionsOnDay(int day);

    Set<MarketSegment> getTargetSegmentsOnDay(int day);

    static MarketMonitor getInstance(){
        return null;
    }

}

package org.dank;

import org.dank.CampaignDecider;


import org.dank.MarketMonitor;
import org.dank.entities.Campaign;
import org.dank.tables.UserPopulationProbTable;
import org.dank.tables.UserPopulations;
import tau.tac.adx.report.adn.MarketSegment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

/**
 * A implementation of {@link CampaignDecider} that determines if the campaign should be
 * bid on based on two criteria:
 *
 *  1. There will be enough impressions for the campaign over the entire duration of the campaign
 *  2. For each day of the campaign, there is a big enough gap in the market for the new campaign
 *
 * Created by EKREM on 1.12.2016.
 * Edited by: Ekrem, Stefan
 */
public class Decider implements CampaignDecider {

    private MarketMonitor monitor;
    private UserPopulations userPopulations;
    private int currentDay;

    public Decider(MarketMonitor monitor){
        this.monitor = monitor;
        userPopulations = new UserPopulations();

    }

    /*
    Step 1 : check if potential imps per day(specified Market Segment) > overall imps per day(RC) +
    */
    @Override
    public boolean shouldBidOnCampaign(Campaign incoming,int currentDay) {
        this.currentDay = currentDay;

        long campaignLength = incoming.getDayEnd() - incoming.getDayStart();
        long campaignReachImps = incoming.getReachImps();

        /*TODO try and error on the simulation to find out the "golden" ratio for the function of MinNumImp = ratio * userPop
        */
        long averageImpressionsForSegment = (long) (userPopulations.percentOfPopulation(incoming.getTargetSegment()) * 10000);

        return false;
    }

    /**
     *
     * Given total number of impressions generated over the length of the campaign;
     * is that enough for the incoming campaign?
     *
     * @param incomingCamp -- The campaign we are decing whether to accept
     * @return true -- There is enough impressions for the incomingCamp
     */
    protected boolean isEnoughImpressionsForNewCampaign(Campaign incomingCamp){
        return true;
    }

    /**
     * By iterating over each day of the campaign and summing the required impressions (per day)
     * of each campaign with overlapping market segments. Is there enough remaining impressions (per day)
     * to support the incoming campaign.
     *
     * @param incomingCamp -- The campaign we are decing whether to accept
     * @return true -- There is a enough
     */
    protected boolean isEnoughRemainingImpressionsPerDay(Campaign incomingCamp){
        return true;
    }


}

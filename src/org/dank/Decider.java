package org.dank;

import org.dank.CampaignDecider;


import org.dank.MarketMonitor;
import org.dank.entities.Campaign;
import tau.tac.adx.report.adn.MarketSegment;

import java.util.*;

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
    private long currentDay;

    protected Decider(MarketMonitor monitor){
        this.monitor = monitor;

    }


    @Override
    public boolean shouldBidOnCampaign(Campaign incoming) {
        return isEnoughImpressionsForNewCampaign(incoming) &&
                isEnoughRemainingImpressionsPerDay(incoming);
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
        long avail_imps = getImpressionsPerDayFor(incomingCamp.getTargetSegment()) * incomingCamp.getLength();

        return avail_imps >= incomingCamp.getReachImps();
    }

    /**
     * By iterating over each day of the campaign and summing the required impressions (per day)
     * of each campaign with overlapping market segments. Is there enough remaining impressions (per day)
     * to support the incoming campaign.
     *
     * @param incomingCamp -- The campaign we are decing whether to accept
     * @return true -- There is a enough per day
     */
    protected boolean isEnoughRemainingImpressionsPerDay(Campaign incomingCamp){
        long avail_imps_pd = getImpressionsPerDayFor(incomingCamp.getTargetSegment());
        long ori_IC_pd = incomingCamp.getReachImps()/incomingCamp.getLength();
        long todays_required_imps = 0;

        // look at each day of the incoming camp
        for (long today = incomingCamp.getDayStart(); today <= incomingCamp.getDayEnd(); today++){
            //Look at each campaign that runs on that day
            for (Campaign running_camp : this.monitor.getAllCampaignsOnDay((int)today)){

                // If they compete, then add up the required impressions
                if (isCompeting(running_camp, incomingCamp)) {
                    todays_required_imps += running_camp.getReachImps()/ running_camp.getLength();
                }
            }

            if (todays_required_imps + ori_IC_pd > avail_imps_pd){
                return false;
            }
        }

        // If we reached this line, that implies there was no day where imp demmand exceeded imp supply
        // Therefore there is enough imps generated each day for our incoming campaign
        return true;
    }


    /**
     * Determines if the two given {@link Campaign}s are competing by seeing if their target markets intersect
     * @return true -- They do compete
     */
    private static boolean isCompeting(Campaign RC, Campaign IC){
        Set<MarketSegment> targetSegment_RC = RC.getTargetSegment();
        Set<MarketSegment> targetSegment_IC = IC.getTargetSegment();
        targetSegment_RC.retainAll(targetSegment_IC);

        return targetSegment_RC.size() > 0;
    }

    /**
     * Estimate the number of impressions that will generated for market segment that the
     * incoming campaign is targeting.
     *
     * NOTE: This currently assumes 1 imp per user per day (version 1)
     *
     * v1: 1 imp per user per day
     * v2: avg(1..max_imps_per_visit) imps per user per day
     * v3: avg determined by data collection
     *
     * @param userType -- The campaign, who's target market we will look at
     * @return an estimate to the number of impressions that will be generated for the market segment
     */
    private long getImpressionsPerDayFor(Set<MarketSegment> userType){

        long population = MarketSegment.usersInMarketSegments().get(userType);

        // TODO: determine the true number of impressions generated each day by either:
        // TODO: [1] look at the docs/code to find a value somewhere
        // TODO  [2] or run the server like 10 times and store the imps generated per day

        return population;
    }

}

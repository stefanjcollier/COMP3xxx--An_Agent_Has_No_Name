package org.dank;

import org.dank.CampaignDecider;


import org.dank.MarketMonitor;
import org.dank.entities.Campaign;
import org.dank.tables.UserPopulationProbTable;
import org.dank.tables.UserPopulations;
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
    private UserPopulations userPopulations;
    private long currentDay;

    public Decider(MarketMonitor monitor){
        this.monitor = monitor;
        userPopulations = new UserPopulations();

    }

    /*
    Step 1 : check if potential imps per day(specified Market Segment) > overall imps per day(RC) +
    */
    @Override
    public boolean shouldBidOnCampaign(Campaign incoming,long currentDay) {
        this.currentDay = currentDay;

        long campaignLength = incoming.getDayEnd() - incoming.getDayStart();
        long campaignReachImps = incoming.getReachImps();

        /*TODO try and error on the simulation to find out the "golden" ratio for the function of MinNumImp = ratio * userPop
        */
        if(isEnoughImpressionsForNewCampaign(incoming)){
            if(isEnoughRemainingImpressionsPerDay(incoming)){
                    return true;
            }
        }

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
        // Get estimated impressions per day times by the IC.length
        // multiply it by the percentage of population that have the same market seg := avail_imps
        // reach required for IC := ori_IC
        //
        // return avail_imps >= ori_IC

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
        // Get estimated impressions per day
        // multiply it by the percentage of population that have the same market seg := avail_imps_pd
        // determine imps per day needed for IC := ori_IC_pd
        //
        // FOR EACH day IN RANGE( IC.start -> IC.END)
        //    # Find all required imps needed that day
        //    todays_required_imps := 0
        //    FOR EACH camp in all_RCs
        //       IF camp.is_running_on(day)
        //       THEN all_req_imps_pd += camp.req_imps_pd()
        //    # determine if there are enough imps available per day
        //    IF (ori_IC_pd + all_req_imps_pd) > avail
        //    THEN return false
        // #If we reach the end of the loop, therefore there must be enough each day
        // return true
        long avail_imps_pd = getImpressionsPerDayFor(incomingCamp.getTargetSegment());
        long ori_IC = incomingCamp.getReachImps()/incomingCamp.getLength();
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

            if (todays_required_imps + ori_IC > avail_imps_pd){
                return false;
            }
        }
        /*
        //calcaulates average Imps needed for all the running campaigns
        long sumOfAverageImpsNeeded = 0;

        Set<MarketSegment> temp = null;

        for(Campaign c : getRunningCampaignsDuring(incomingCamp)){
            temp = c.getTargetSegment();
            temp.retainAll(incomingCamp.getTargetSegment());

            sumOfAverageImpsNeeded +=  getImpressionsPerDayFor(temp) / (c.getDayEnd() - c.getDayStart());

        }
        return avail_imps_pd > ori_IC + sumOfAverageImpsNeeded;
         */

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

        long population = userPopulations.getPopulation();
        double percentOfPop = userPopulations.percentOfPopulation(userType);

        // TODO: determine the true number of impressions generated each day by either:
        // TODO: [1] look at the docs/code to find a value somewhere
        // TODO  [2] or run the server like 10 times and store the imps generated per day

        return (long)(population * percentOfPop);
    }


    //gets all the running campaign during th IC
    private ArrayList<Campaign> getRunningCampaignsDuring(Campaign IC){
        ArrayList<Campaign> retVal = new ArrayList<Campaign>();

        Set<MarketSegment> targetSegment = IC.getTargetSegment();
        Set<MarketSegment> temp = null;
        for(Campaign c : this.monitor.getAllCampaigns()){
            temp = c.getTargetSegment();
            //checks if the campaign is still running or not ?
            if(c.getDayEnd() > IC.getDayStart()){
                temp.retainAll(targetSegment);

                //check if they targeting the similar segments?
                if(temp.size() > 0){

                    retVal.add(c);
                }
            }

        }

        return retVal;
    }
}

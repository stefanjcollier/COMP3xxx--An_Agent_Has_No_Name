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
 * Created by EKREM on 1.12.2016.
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

}

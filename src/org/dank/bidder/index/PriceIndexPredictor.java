package org.dank.bidder.index;

import org.dank.entities.Campaign;
import tau.tac.adx.report.adn.MarketSegment;

/**
 * Created by vlad on 04/12/2016.
 */
public class PriceIndexPredictor {

    private static PriceIndexPredictor INSTANCE;

    private PriceIndexPredictor() {};


    public double estimatePriceForSegmentNextDay(Campaign campaign) {
        double userPopulation = MarketSegment.usersInMarketSegments().get(campaign.getTargetSegment());
        double reach = (double) campaign.getReachImps();
        double period = (double) (campaign.getLength());

        return reach / (period * userPopulation);
    }

    public double estimatePriceForSegmentMultipleDays(Campaign campaign) {
        double userPopulation = MarketSegment.usersInMarketSegments().get(campaign.getTargetSegment());
        double reach = (double) campaign.getReachImps();
        double period = (double) (campaign.getLength());

        return reach / (period * userPopulation);
    }



    public static PriceIndexPredictor getInstance(){
        if (INSTANCE == null) {
            INSTANCE = new PriceIndexPredictor();
        }
        return INSTANCE;
    }
}

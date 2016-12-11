package org.dank.bidder.index;

import org.dank.MarketMonitor;
import org.dank.entities.Campaign;
import org.omg.CORBA.PUBLIC_MEMBER;
import tau.tac.adx.report.adn.MarketSegment;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by vlad on 04/12/2016.
 */
public class PriceIndexPredictor {

    private static PriceIndexPredictor INSTANCE;
    private MarketMonitor monitor;

    public PriceIndexPredictor(MarketMonitor monitor) {
        this.monitor = monitor;
    }


    public double estimatePriceForSegmentNextDay(MarketSegment s, long day) {

        double popularity = 0;

        ArrayList<Campaign> campaigns = (ArrayList<Campaign>) this.monitor.getAllCampaignsOnDay((int)day);

        for (Campaign campaign : campaigns) {

            double userPopulation = MarketSegment.usersInMarketSegments().get(campaign.getTargetSegment());
            double reach = (double) campaign.getReachImps();
            double period = (double) (campaign.getLength());

            popularity += (reach / (userPopulation * period));

        }
        return popularity;
    }

    public double estimatePriceForSegmentMultipleDays(Campaign incomingCamp){
        return this.estimatePriceForSegmentMultipleDays(
                incomingCamp.getTargetSegment(),
                incomingCamp.getDayStart(),
                incomingCamp.getDayEnd()
                );
    }

    public double estimatePriceForSegmentMultipleDays(Set<MarketSegment> targetSegment, long startDay, long endDay) {

        //Sum_s_t W(s) . pop(s,t)
        double popularity = 0;

        for (MarketSegment userSegment : targetSegment) {
            for (long day = startDay; day <= endDay; day++) {
                // W(s)
                Set<MarketSegment> thisSegment = MarketSegment.compundMarketSegment1(userSegment);
                long segmentPopulation = MarketSegment.usersInMarketSegments().get(thisSegment);

                //pop(s,t)
                double segmentPopularity = this.estimatePriceForSegmentNextDay(userSegment,day);

                popularity += segmentPopulation * segmentPopularity;
            }
        }

        //  div by |T| . W(S)
        popularity /= ((endDay - startDay) * MarketSegment.usersInMarketSegments().get(targetSegment));

        return popularity;
    }

}

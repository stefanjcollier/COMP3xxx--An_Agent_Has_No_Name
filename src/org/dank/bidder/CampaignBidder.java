package org.dank.bidder;

import org.State;
import org.dank.bidder.index.PriceIndexPredictor;
import org.dank.entities.Campaign;
import tau.tac.adx.report.adn.MarketSegment;

import java.util.Set;
import java.util.TreeSet;
import java.util.function.Supplier;

/**
 *  This class determines the value of the bid for any given bid
 *
 * Created by vlad on 04/12/2016.
 */
public class CampaignBidder {

    private static CampaignBidder INSTANCE;

    PriceIndexPredictor priceIndexPredictor;
    State state;

    public CampaignBidder(PriceIndexPredictor predictor, State state) {
        this.priceIndexPredictor = predictor;
        this.state = state;
    }


    public double getBidFor(Campaign incomingCamp, double myQuality){
        System.out.println("==============[CampaignBidder]===========================================");
        System.out.println("--------------[Camp: "+incomingCamp.getNiceName()+"("+incomingCamp.getId()+")]-------------------------------------------");
        // We prioritise having a high quality

        System.out.println("Quality score: " + myQuality);

        double strat1 = performStrategy1(incomingCamp, myQuality);
        double strat2 = performStrategy2(incomingCamp,myQuality);
        double strat3 = performStrategy3(incomingCamp, myQuality);

        double minBid = strat2;
        double maxBid = strat3;
        double supplyDemand = priceIndexPredictor.getEstimatedPriceForImpressions(incomingCamp);

        System.out.println("DEMANDSUPPLY BID= " + maxBid * (1 - supplyDemand));
        System.out.println("LOWEST BID   = " + strat2);
        System.out.println("HIGHEST BID  = " + strat3);


        if (myQuality <= State.LOW_QUALITY){
            System.out.println("Performed strategy 2: Quality too low, bidding lowest price");
            System.out.println("BID = " + strat2);

            return strat2;


        } else if (!this.isCampaignAchievable(incomingCamp)) {
            System.out.println("Performed strategy 2: Campaign unachievable, bidding highest price");
            System.out.println("BID = " + strat3);

            return strat3;


        } else {
            System.out.println("Bidding strategic bid");
            if(supplyDemand > 1){
                System.out.println("BID = " + maxBid);
                return maxBid;
            }else if(supplyDemand == 0.0){
                System.out.println("BID = " + minBid);
                return minBid;
            }else{
                double bid = minBid + (0.9*maxBid-minBid)*supplyDemand;
                System.out.println("BID = " + maxBid * (1 - supplyDemand));
                return this.bound(bid, minBid,  maxBid
                );
            }
        }
    }

    /**
     * bid private value: PI * CI
     * (Normal Path)
     * */
    protected double performStrategy1(Campaign incomingCamp, double myQuality){
        return (myQuality / this.pici(incomingCamp)) * incomingCamp.getReachImps();
    }
    /** Bid lowest valid price */
    protected double performStrategy2(Campaign incomingCamp, double myQuality){
        return this.lowestPrice(incomingCamp, myQuality);
    }
    /** Bid highest valid price */
    protected double performStrategy3(Campaign incomingCamp, double myQuality){
        return this.highestPrice(incomingCamp, myQuality);
    }

    private double pici(Campaign incomingCamp){
        double pi = this.priceIndexPredictor.estimatePriceForSegmentMultipleDays(incomingCamp);
        double ci = this.state.getCi();
        return pi * ci;
    }

    /** if budget (PI*CI) > reach**/
    private boolean isCampaignAchievable(Campaign incomingCamp){
        double pi = this.priceIndexPredictor.estimatePriceForSegmentMultipleDays(incomingCamp);
        return pi <= State.PI_TOO_HIGH;
    }

    private double lowestPrice(Campaign incomingCamp, double myQuality){
        return (int) Math.ceil((incomingCamp.getReachImps() / myQuality) / 10.0);
    }

    private double highestPrice(Campaign incomingCamp, double myQuality){
        return (int) Math.floor(incomingCamp.getReachImps() * myQuality);
    }

    private double bound(double bid, double min, double max) {
        if (bid < min) return min;
        else if (bid > max) return max;
        return bid;
    }

    ///TEST



}

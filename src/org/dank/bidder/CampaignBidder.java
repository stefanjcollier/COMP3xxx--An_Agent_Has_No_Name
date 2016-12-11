package org.dank.bidder;

import org.State;
import org.dank.MarketMonitor;
import org.dank.bidder.index.PriceIndexPredictor;
import org.dank.entities.Campaign;

import java.util.Map;

/**
 *  This class determines the value of the bid for any given bid
 *
 * Created by vlad on 04/12/2016.
 */
public class CampaignBidder {


    PriceIndexPredictor priceIndexPredictor;
    State state;
    CampaignValueDeterminer campaignValueDeterminer;


    public CampaignBidder(Map<Integer, Campaign> myCampaigns, MarketMonitor marketMonitor) {
        this.priceIndexPredictor = PriceIndexPredictor.getInstance();
        this.state = State.getInstance();
        this.campaignValueDeterminer = new CampaignValueDeterminer(myCampaigns,marketMonitor);
    }


    public double getBidFor(Campaign incomingCamp, double myQuality){
        System.out.println("==============[CampaignBidder]===========================================");
        System.out.println("--------------[Camp: "+incomingCamp.getId()+"]-------------------------------------------");
        // We prioritise having a high quality

        System.out.println("Quality score: " + myQuality);

        double strat1Result = performStrategy1(incomingCamp, myQuality);
        double minBid = this.lowestPrice(incomingCamp, myQuality);
        double maxBid = this.highestPrice(incomingCamp, myQuality);

        System.out.println("STRATEGIC BID= " + strat1Result);
        System.out.println("OLD BID      = " + (myQuality / incomingCamp.getReachImps()) / this.pici(incomingCamp));
        System.out.println("LOWEST BID   = " + performStrategy2(incomingCamp, myQuality));
        System.out.println("HIGHEST BID  = " + performStrategy3(incomingCamp, myQuality));

        if (myQuality <= State.LOW_QUALITY){
            System.out.println("Performed strategy 2: Quality too low, bidding lowest price");
            System.out.println("BID = " + performStrategy2(incomingCamp, myQuality));


            return performStrategy2(incomingCamp, myQuality);
        } else if (!this.isCampaignAchievable(incomingCamp)) {
            System.out.println("Performed strategy 2: Campaign unachievable, bidding highest price");
            System.out.println("BID = " + performStrategy3(incomingCamp, myQuality));

            return performStrategy3(incomingCamp, myQuality);
        } else {
            if (strat1Result <= minBid){
                System.out.println("Attempted strategy 1: Campaign achievable, HOWEVER bid set to MIN");
                System.out.println("BID (Strat1) = " + strat1Result);
                System.out.println("BID (Min)    = " + minBid);

            }else if (strat1Result >= maxBid){
                System.out.println("Attempted strategy 1: Campaign achievable, HOWEVER bid set to MAX");
                System.out.println("BID (Strat1) = " + strat1Result);
                System.out.println("BID (Max)      = " + maxBid);
            }else{
                System.out.println("Performed strategy 1: Campaign achievable, bidding strategic bid");
                System.out.println("BID = " + performStrategy1(incomingCamp, myQuality));
            }

            return bound(strat1Result, minBid, maxBid);
        }
    }
    /**
     * bid private value: PI * CI
     * (Normal Path)
     * */
    protected double performStrategy1(Campaign incomingCamp, double myQuality){
        double my_bid = this.campaignValueDeterminer.determineBid(incomingCamp, myQuality);
        return 100;
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
}

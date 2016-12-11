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


    public double getBidFor(Campaign incomingCamp, double myQuality, long today){
        System.out.println("==============[CampaignBidder]===========================================");
        System.out.println("--------------[Camp: "+incomingCamp.getNiceName()+"("+incomingCamp.getId()+")]-------------------------------------------");

        // We prioritise having a high quality
        System.out.println("Quality score: " + myQuality);

        double strat1 = performStrategy1(incomingCamp, myQuality);
        double strat2 = performStrategy2(incomingCamp,myQuality);
        double strat3 = performStrategy3(incomingCamp, myQuality);
        double anlStrat = performANLStrategy(incomingCamp, myQuality);

        double minBid = strat2;
        double maxBid = strat3;

        System.out.println("STRATEGIC BID= " + strat1);
        System.out.println("ANL BID      = " + anlStrat);
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
            if (strat1 <= minBid){
                System.out.println("Attempted strategy 1: Campaign achievable, HOWEVER bid set to MIN");
                System.out.println("BID (Strat1) = " + strat1);
                System.out.println("BID (Min)    = " + minBid);
                return maxBid;

            }else if (strat1 >= maxBid){
                System.out.println("Attempted strategy 1: Campaign achievable, HOWEVER bid set to MAX");
                System.out.println("BID (Strat1) = " + strat1);
                System.out.println("BID (Max)      = " + maxBid);
                return minBid;

            }else{
                System.out.println("Performed strategy 1: Campaign achievable, bidding strategic bid");
                //No need for bounding
                System.out.println("BID = " + strat1);
                return strat1;

            }
        }
    }

    private boolean inFirstFewDays(long today){
        return today <= State.FIRST_FEW_DAYS;
    }

    /**
     * bid private value: PI * CI
     * (Normal Path)
     * */
    protected double performStrategy1(Campaign incomingCamp, double myQuality){
        double my_bid = this.campaignValueDeterminer.determineBid(incomingCamp, myQuality);
        return my_bid*1000;
    }

    protected double performANLStrategy(Campaign incomingCamp, double myQuality){
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
}

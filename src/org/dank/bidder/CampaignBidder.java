package org.dank.bidder;

import org.State;
import org.dank.bidder.index.PriceIndexPredictor;
import org.dank.entities.Campaign;

/**
 *  This class determines the value of the bid for any given bid
 *
 * Created by vlad on 04/12/2016.
 */
public class CampaignBidder {

    private static CampaignBidder INSTANCE;

    PriceIndexPredictor priceIndexPredictor;
    State state;

    public CampaignBidder() {
        this.priceIndexPredictor = PriceIndexPredictor.getInstance();
        this.state = State.getInstance();
    }


    public double getBidFor(Campaign incomingCamp, double myQuality){
        System.out.println("==============[CampaignBidder]===========================================");
        System.out.println("--------------[Camp: "+incomingCamp.getId()+"]-------------------------------------------");
        // We prioritise having a high quality

        System.out.println("Quality score: " + myQuality);

        if (myQuality <= State.LOW_QUALITY){
            System.out.println("Performed strategy 2: Quality too low, bidding lowest price");
            System.out.println("BID = " + performStrategy2(incomingCamp, myQuality));
            System.out.println("LOWEST BID = ");
            System.out.println("HIGHEST BID = ");

            return performStrategy2(incomingCamp, myQuality);
        } else if (!this.isCampaignAchievable(incomingCamp)) {
            System.out.println("Performed strategy 2: Campaign unachievable, bidding highest price");
            System.out.println("BID = " + performStrategy3(incomingCamp, myQuality));
            System.out.println("LOWEST BID = ");
            System.out.println("HIGHEST BID = ");
            return performStrategy3(incomingCamp, myQuality);
        } else {
            System.out.println("Performed strategy 1: Campaign achievable, bidding strategic bid");
            System.out.println("BID = " + performStrategy1(incomingCamp, myQuality));
            System.out.println("LOWEST BID = ");
            System.out.println("HIGHEST BID = ");
            return performStrategy1(incomingCamp, myQuality);
        }
    }

    /**
     * bid private value: PI * CI
     * (Normal Path)
     * */
    protected double performStrategy1(Campaign incomingCamp, double myQuality){
        return myQuality / this.pici(incomingCamp);
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
        return pi >= State.PI_TOO_HIGH;
    }

    private double lowestPrice(Campaign incomingCamp, double myQuality){
        return incomingCamp.getReachImps() / myQuality;
    }

    private double highestPrice(Campaign incomingCamp, double myQuality){
        return (incomingCamp.getReachImps() * myQuality) / 10.0;
    }
}

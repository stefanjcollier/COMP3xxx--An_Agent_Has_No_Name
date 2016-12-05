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
        // We prioritise having a high quality
        if (myQuality <= State.LOW_QUALITY){
            return performStrategy2(incomingCamp, myQuality);

        } else if (!this.isCampaignAchievable(incomingCamp)) {
            return performStrategy3(incomingCamp, myQuality);

        } else {
            return performStrategy1(incomingCamp);
        }
    }

    /**
     * bid private value: PI * CI
     * (Normal Path)
     * */
    protected double performStrategy1(Campaign incomingCamp){
        return this.pici(incomingCamp);
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

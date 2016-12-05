package org.dank.bidder;

/**
 * Created by vlad on 04/12/2016.
 */
public class UCSBidder {

    private static UCSBidder INSTANCE;


    private UCSBidder() {};



    public static UCSBidder getInstance(){
        if (INSTANCE == null) {
            INSTANCE = new UCSBidder();
        }
        return INSTANCE;
    }


    public long calcUCSBid(long prevBid,long ucsLevel,int currentDay){
        long estimatedImpressionReach = 10000; //todo -- the expected impressions to go in the next simulated day
        long gucs = (long) 0.5; //todo --  constant
        long Ep = 10000;//todo -- p:= impression unit-price

        long r0 = (long) 0.75 * (estimatedImpressionReach);

        if(ucsLevel > 0.9){

            return prevBid / (1+gucs);
        }else if(ucsLevel < 0.81 && ((r0 / prevBid) >= (20 / 3) * ((1+gucs)/(Ep)))){

            return (1+gucs) / prevBid;
        }else{

            return prevBid;
        }

    }

}


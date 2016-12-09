package org.dank.bidder;

import com.sun.org.apache.xml.internal.dtm.ref.DTMAxisIterNodeList;
import org.DankAdNetwork;
import org.State;
import org.dank.Logger;
import org.dank.MarketMonitor;
import org.dank.entities.Campaign;

import java.util.Collection;
import java.util.Set;

/**
 * Created by vlad on 04/12/2016.
 */
public class UCSBidder {

    private static UCSBidder INSTANCE;

    DankAdNetwork agent;
    Logger logger = new Logger();

    public UCSBidder(DankAdNetwork agent) {

        this.agent = agent;

    };


    public double calcUCSBid(double prevBid,double ucsLevel,int currentDay){
        logger.logUCS(ucsLevel, prevBid);//logging the ucs performance

        currentDay += 1; // calculation the UCSBid for day n+1

        double estimatedImpressionReach = calcEstimatedImpressionReach(currentDay);
        double gucs = 0.3; //todo --  constant
        double Ep =  0.001;//todo -- p:= impression unit-price

        double r0 =  0.75 * (estimatedImpressionReach);

        if(estimatedImpressionReach == 0){
            System.out.println("=========================================================");
            System.out.println("UCSBidder -- Zero Campaign Precaution");
            System.out.println("UCSBidder -- Calculated Bid : " + 0.0);
            System.out.println("=========================================================");
         return 0.0;
        }
        System.out.println("=========================================================");
        System.out.println("UCSBidder -- Calculating the UCS for Day " + currentDay);
        System.out.println("UCSBidder -- Current Level : " +ucsLevel);
        System.out.println("UCSBidder -- Previous Bid : " +prevBid);
        System.out.println("UCSBidder -- Estimated Impression Reach : " + estimatedImpressionReach);

        if(ucsLevel > 0.9){
            System.out.println("UCSBidder -- Calculated Bid : " + prevBid / (1+gucs));
            System.out.println("=========================================================");
            return prevBid / (1+gucs);

        }else if(ucsLevel < 0.81 && ((r0 / prevBid) >= (20 / 3) * ((1+gucs)/(Ep)))) {
            System.out.println("UCSBidder -- Calculated Bid : " + (1 + gucs) * prevBid);
            System.out.println("=========================================================");
            return (1 + gucs) * prevBid;
        }else{
            System.out.println("UCSBidder -- Calculated Bid : " + prevBid);
            System.out.println("=========================================================");
            return prevBid;
        }

    }

    private double calcEstimatedImpressionReach(int currentDay){

        Set<Campaign> allocatedCampaigns = agent.getAllocatedCampaigns();
        double retVal = 0;
        for(Campaign c : allocatedCampaigns){

            if(c.getDayEnd() > currentDay || c.getDayStart() == currentDay){

                double length = c.getDayEnd() - currentDay;
                double dailyAverage = c.impsTogo() / length;
                retVal += dailyAverage;


            }else if(c.getDayEnd() == currentDay){

                retVal += c.impsTogo();

            }

        }

        return retVal;
    }

}


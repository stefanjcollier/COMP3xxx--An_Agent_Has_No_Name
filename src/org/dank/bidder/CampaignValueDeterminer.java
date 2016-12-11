package org.dank.bidder;

import org.dank.MarketMonitor;
import org.dank.entities.Campaign;
import tau.tac.adx.report.adn.MarketSegment;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * Created by Stefa on 10/12/2016.
 */
public class CampaignValueDeterminer {
    private Map<Integer, Campaign> myCampaigns;
    private MarketMonitor marketMonitor;


    public CampaignValueDeterminer(Map<Integer, Campaign> myCampaigns, MarketMonitor marketMonitor){
        this.myCampaigns = myCampaigns;
        this.marketMonitor = marketMonitor;
    }


    public double determineBid(Campaign IC, double myQuality) {
        return this.determineBid(IC,myQuality,false);
    }

    public double determineBid(Campaign IC, double myQuality, boolean showWorking){
        show(showWorking,"------[ CampaignValueDeterminer(Working): ("+IC.getNiceName()+") "+IC.getId()+"]---------");
        double cost = IC.getReachImps()*getAverageCompetingPrice(IC, showWorking);

        //Assumption on Q_second:
        double Q_second = 1.0;
        double second_winning_bid = cost * Q_second / myQuality;
        show(showWorking,"B_Second = "+second_winning_bid);

        double undercutting_percent = 0.9;
        double my_bid = second_winning_bid * undercutting_percent;

        if (!showWorking && (my_bid == Double.NaN || my_bid <= 0)){
            this.determineBid(IC,myQuality,true);
            show(true,"-----(Working)----------------------");
        }
        return my_bid;
    }

    private double getAverageCompetingPrice(Campaign IC, boolean showWorking){
        double runningTotal = 0.0;
        double runningCount = 0.0;
        for (Campaign PC : this.marketMonitor.getAllCampaigns()){
            if ( !this.wasRandomlyWon(PC) && this.isCompeting(PC, IC) && PC.getBudget() > 0 ){
                show(showWorking, "\t - ("+PC.getNiceName()+") PC.getBudget() / PC.getReachImps() = "+PC.getBudget()+" / "+PC.getReachImps()+" = "+PC.getBudget() / PC.getReachImps());
                runningTotal += PC.getBudget() / PC.getReachImps();
                runningCount++;
            }
        }
        double avg_imp_price = runningTotal / runningCount;

        show(showWorking, "Running Count: "+(int)runningCount);
        show(showWorking, "Avg Imp Price: "+avg_imp_price);

        return avg_imp_price;
    }

    private void show(boolean showWorking, String txt){
        if ( showWorking) System.out.println(txt);
    }
    private boolean wasRandomlyWon(Campaign PC){
        return weShouldHaveWon(PC) || myRandomBid(PC);
    }

    private boolean weShouldHaveWon(Campaign PC){
        // If our bid was smaller, then we should have won
        return PC.getBudget() > PC.getOurBid(); //TODO check this.. as effective bid should be in order
    }

    private boolean myRandomBid(Campaign PC){
         return (myCampaigns.containsKey(PC.getId()) && (PC.getOurBid() == PC.getBudget()));
    }

    /**
     * Determines if the two given {@link Campaign}s are competing by seeing if their target markets intersect
     * @return true -- They do compete
     */
    private boolean isCompeting(Campaign RC, Campaign IC){
        Set<MarketSegment> targetSegment_RC = RC.getTargetSegment();
        Set<MarketSegment> targetSegment_IC = IC.getTargetSegment();

        Set<MarketSegment> overlap = new TreeSet<>(targetSegment_RC);
        overlap.retainAll(targetSegment_IC);

        return overlap.size() > 0;
    }

      /*
        PC := Previous Campaigns
        PC = all campaigns with (overlaping segments) and  not (random wins)

        // Find the avg price per impression
        runningTotal = 0.0
        FOR ALL p in PC:
            runningTotal += p.cost / p.reach
        DONE
        // NOTE: Ensure avg_imp_price is not in millis
        avg_imp_price = runningTotal / LENGTH(PC)

        // We determine the rewarded cost/budget of a winning bid (The thing returned in the costMillis() )
        Cost = C = IC.reach * avg_imp_price

        //Determine the second bid that produces the given Cost
        // Given C = Q_me / e_second
        //     s.t. e_second := effective bid of second place bidder
        //          e_second = Q_second / B_second
        // WORKING OUT:
        ////  C = Q_me / (Q_second / B_second)
        ////  C = B_second * (Q_me / Q_second)    (re-arrange)
        ////  B_second = C / (Q_me / Q_second)
        ////  B_second = C * Q_second / Q_me
        // NOTE: We do not know Q_second
        //     therefore we must estimate Q_second, //TODO assume Q_second is 1.0
        Q_second = 1.0
        B_second = C * Q_second / Q_me

        // Now undercut that second bid
        my_bid = b_second * State.undercuttingValue

        return bid

        // NOTE: Q_second
        //  We change the assumption on Q_second based on desperation.
        //  i.e. a larger Q_second means they have a larger quality
        //      which means that their bid is less, which means something to dwell on later
         */

}

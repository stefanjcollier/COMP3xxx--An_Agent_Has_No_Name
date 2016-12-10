package org.dank.bidder;

import org.dank.entities.Campaign;
import tau.tac.adx.report.adn.MarketSegment;

import java.util.Set;

/**
 *
 * Created by Stefa on 10/12/2016.
 */
public class CampaignValueDeterminer {

    public double determineBid(Campaign IC, double myQuality){
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
        //      which means that their bid is less
         */
        return 0;
    }


    /**
     * Determines if the two given {@link Campaign}s are competing by seeing if their target markets intersect
     * @return true -- They do compete
     */
    private static boolean isCompeting(Campaign RC, Campaign IC){
        Set<MarketSegment> targetSegment_RC = RC.getTargetSegment();
        Set<MarketSegment> targetSegment_IC = IC.getTargetSegment();
        targetSegment_RC.retainAll(targetSegment_IC);


        return targetSegment_RC.size() > 0;
    }

}

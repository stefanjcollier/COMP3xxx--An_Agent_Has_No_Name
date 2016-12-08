package org.dank.bidder;

import org.State;
import org.dank.bidder.index.PriceIndexPredictor;
import org.dank.entities.Campaign;

/**
 *  This determines how much we want to pay for an impression of the target market of the given campaign.
 *
 * Created by vlad on 04/12/2016.
 * Edited by Stefan
 */
public class ImpressionBidder {

    private static ImpressionBidder INSTANCE;
    private PriceIndexPredictor priceIndexPredictor;
    private ImpressionBidder() {}{
        this.priceIndexPredictor = PriceIndexPredictor.getInstance();
    }



    public double getImpressionBid(Campaign runningCamp, int today){
        double remainingBudget = runningCamp.getBudget() - runningCamp.getStats().getCost();
        double spendableBudget = remainingBudget * State.SPENDING_ALLOWANCE;

        double budget = spendableBudget;
        if (isOverspending(spendableBudget,runningCamp, today)){
            budget *= State.MAX_IMP_PRICE;
        }
        if (campaignEndsTomorrow(runningCamp, today) && notAchievedMinimumReach(runningCamp)){
            budget *= 2;
        }
        return budget / runningCamp.impsTogo();

    }

    private boolean notAchievedMinimumReach(Campaign runningCamp) {
        double percentComplete = runningCamp.impsTogo()/runningCamp.getReachImps();
        return percentComplete < State.MIN_ACCEPTABLE_REACH;
    }

    /** You are 'overspending' when you intend to pay more for an impression than
     *  what it is determined to be worth (by the Price Index Predictor) */
    private boolean isOverspending(double spendableBudget, Campaign runningCamp, long day){
        double spendableBudgetPerImp = spendableBudget / runningCamp.impsTogo();

        double pi = determinePiForRemainingDays(runningCamp, day);

        return spendableBudgetPerImp > pi;
    }


    private double determinePiForRemainingDays(Campaign runingCamp, long dayStart){
        return priceIndexPredictor.estimatePriceForSegmentMultipleDays(
                runingCamp.getTargetSegment(),
                dayStart,
                runingCamp.getDayEnd());
    }

    private boolean campaignEndsTomorrow(Campaign runningCamp, long today){
        return (today + 1) == runningCamp.getDayEnd();
    }

    public static ImpressionBidder getInstance(){
        if (INSTANCE == null) {
            INSTANCE = new ImpressionBidder();
        }
        return INSTANCE;
    };
}

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
        System.out.println("--------------[ImpressionBidder:Camp "+runningCamp.getNiceName()+"("+runningCamp.getId()+")]-------------------------------------------");

        double remainingBudget = runningCamp.getBudget() - runningCamp.getStats().getCost();
        double spendableBudget = remainingBudget * State.SPENDING_ALLOWANCE;

        System.out.println("Total Budget: £"+runningCamp.getBudget()+"\t\tremaining: £"+remainingBudget+
                "\t\t="+(100.0*remainingBudget/runningCamp.getBudget())+"% remaining to spend");
        System.out.println("Total Reach: "+runningCamp.getReachImps()+"\t\tremaining: "+runningCamp.impsTogo()+
                "\t\t="+(100.0*runningCamp.impsTogo()/runningCamp.getReachImps())+"%needed");
        System.out.println("Total Length: "+runningCamp.getLength()+"\t\t\tremaining: "+(runningCamp.getDayEnd()-today)+
                "\t\t\t="+(100.0*(runningCamp.getDayEnd()-today)/runningCamp.getLength())+"%of time left");
        System.out.println("(initial) Spendable Budget ("+100*State.SPENDING_ALLOWANCE+"%): £"+spendableBudget);


        double budget = spendableBudget;
        if (false &&isOverspending(spendableBudget,runningCamp, today)){
            budget *= State.MAX_IMP_PRICE;
            System.out.println("isOverspending!");
            System.out.println("    -> budget now:"+budget);
        }
        if (campaignEndsTomorrow(runningCamp, today) && notAchievedMinimumReach(runningCamp)){
            budget *= 2;
            System.out.println("endsTomorrow and underachieving reach!");
            System.out.println("    -> budget doubled:"+budget);
        }
        double impPrice = budget / runningCamp.impsTogo();

        System.out.println("Price Per Impression: "+impPrice);
        System.out.println("---------------------------------------------------------------------------");
        return impPrice;

    }

    /** If the n is less than 0.9 */
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

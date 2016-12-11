package org;

/**
 * Store useful simulation data, to be accessed during strategy computation.
 *
 * e.g. CI := competing index
 *
 * Created by vlad on 04/12/2016.
 */
public class State {

    private static State INSTANCE;

    private double ci;
    private final double gGreed;

    public static final double LOW_QUALITY = 0.8;

    public static final double PI_TOO_HIGH = 9.0; //TODO determine this

    /** The maximum bid some one can offer for an impression */
    public static final double MAX_IMP_PRICE = 0.0001;

    /** The percentage of a budget we intend to spend */
    public static final double SPENDING_ALLOWANCE = 0.9; //TODO decide on this

    public static final double MIN_ACCEPTABLE_REACH = 0.9;

    public enum BidOutcome { BID_WIN, RANDOM_WIN, LOSS }

    public static final long FIRST_FEW_DAYS = 5;


    private State() {
        this.ci = 1.0;
        this.gGreed = 1.2;
    }

    public double getLOW_QUALITY() {
        return LOW_QUALITY;
    }

    public double getCi() {
        return ci;

    }

    public double getgGreed() {
        return gGreed;
    }

    public void informOfCampaignOutcome(double budget, double bid){
        BidOutcome outcome = BidOutcome.LOSS;

        boolean won = budget != 0;
        if(won){
            if(budget == bid){
                outcome = BidOutcome.RANDOM_WIN;
            }else{
                outcome = BidOutcome.BID_WIN;
            }
        }
        updateCi(outcome);
    }

    protected void updateCi(BidOutcome outcome){
        switch(outcome){
            case BID_WIN: {
                // Ci remains the same
                break;
            }case RANDOM_WIN:{
                this.ci /= this.gGreed;
                break;

            }case LOSS:{
                this.ci *= this.gGreed;
                break;

            }default:{
                throw new RuntimeException("Unrecognised Contract BidOutcome enum used: "+outcome.toString());
            }
        }
    }

    public static State getInstance(){
        if (INSTANCE == null) {
            INSTANCE = new State();
        }
        return INSTANCE;
    };
}

package org;

/**
 * Created by vlad on 04/12/2016.
 */
public class ImpressionBidder {

    private static ImpressionBidder INSTANCE;

    private ImpressionBidder() {};



    public static ImpressionBidder getInstance(){
        if (INSTANCE == null) {
            INSTANCE = new ImpressionBidder();
        }
        return INSTANCE;
    };
}

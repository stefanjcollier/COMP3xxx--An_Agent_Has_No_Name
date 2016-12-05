package org;

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
    };
}

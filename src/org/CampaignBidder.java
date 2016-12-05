package org;

/**
 * Created by vlad on 04/12/2016.
 */
public class CampaignBidder {

    private static CampaignBidder INSTANCE;

    private CampaignBidder() {};



    public static CampaignBidder getInstance(){
        if (INSTANCE == null) {
            INSTANCE = new CampaignBidder();
        }
        return INSTANCE;
    };
}

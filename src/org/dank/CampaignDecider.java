package org.dank;

import org.dank.entities.Campaign;

/**
 * Allocated to: Ekrem
 *
 * Created by Stefa on 29/11/2016.
 */
public interface CampaignDecider {

    boolean shouldBidOnCampaign(Campaign incoming,int currentDay);

    static CampaignDecider getInstance(){
        return null;
    }
}

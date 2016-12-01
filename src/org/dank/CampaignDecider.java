package org.dank;

import tau.tac.adx.demand.Campaign;

/**
 * Allocated to: Ekrem
 *
 * Created by Stefa on 29/11/2016.
 */
public interface CampaignDecider {

    boolean shouldBidOnCampaign(Campaign incoming);

    static CampaignDecider getInstance(){
        return null;
    }
}

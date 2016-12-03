package org.dank;

import org.dank.entities.Campaign;

/**
 * Allocated to: Ekrem
 *
 * Created by Stefa on 29/11/2016.
 */
public interface CampaignDecider {

    boolean shouldBidOnCampaign(Campaign incoming);

    static CampaignDecider getInstance(){
        return new Decider(new MarketMonitor());
    }
}

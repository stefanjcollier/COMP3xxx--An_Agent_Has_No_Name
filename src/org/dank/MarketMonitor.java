package org.dank;

import org.dank.entities.Campaign;
import tau.tac.adx.report.adn.MarketSegment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;


/**
 * Created by vlad on 30/11/2016.
 */
public class MarketMonitor implements MarketMonitorInterface {

    private static MarketMonitor singleton;

    private Collection<Campaign> allCampaigns;

    public MarketMonitor() {

        allCampaigns = new ArrayList<Campaign>();
    }

    public Collection<Campaign> getAllCampaignsOnDay(int today) {
        Collection<Campaign> campaigns = new ArrayList<>(3);
        for (Campaign camp : this.allCampaigns){
            if (camp.getDayStart() <= today && today <= camp.getDayEnd()){
                campaigns.add(camp);
            }
        }
        return campaigns;
    }

    public Collection<Campaign> getAllCampaigns() {
        return allCampaigns;
    }

    public void addCampaign(Campaign newCampaign) {
        allCampaigns.add(newCampaign);
    }

    public int getRequiredImpressionsOnDay(int day, Set<MarketSegment> targetSegment) {
        return 0;
    }

    public Set<MarketSegment> getTargetSegmentsOnDay(int day) {
        return null;
    }

    public static MarketMonitor getInstance(){
        if (singleton == null) {
            singleton = new MarketMonitor();
        }
        return singleton;
    }

    public String toString(){
        return "MarketMontitor: NoOfCampaigns = "+this.allCampaigns.size();
    }
}

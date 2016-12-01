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

    long currDay;

    // reference to tables

    public MarketMonitor() {

        allCampaigns = new ArrayList<Campaign>();
    }

    public Collection<Campaign> getAllCampaignsOnDay(int day) {

        return null;
    };

    public Collection<Campaign> getAllCampaigns() {
        return allCampaigns;
    };

    public void addCampaign(Campaign newCampaign) {
        allCampaigns.add(newCampaign);
    };

    public int getRequiredImpressionsOnDay(int day) {
        return 0;
    };

    public Set<MarketSegment> getTargetSegmentsOnDay(int day) {
        return null;
    };

    public static MarketMonitor getInstance(){
        if (singleton == null) {
            singleton = new MarketMonitor();
        }
        return singleton;
    };

    public void setCurrDay(int currDay) {
        this.currDay = currDay;
    }

    private long getAvgImpressionsLeftPerDay(Campaign c) {
        long daysLeft = c.getDayEnd() - currDay;

        return ((long)c.impsTogo() / daysLeft);
    }

}

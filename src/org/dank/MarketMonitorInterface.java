package org.dank;

import org.dank.entities.Campaign;
import tau.tac.adx.report.adn.MarketSegment;

import java.util.Collection;
import java.util.Set;

/**
 *
 * Allocated to: Vlad
 *
 * This is effectively a wrapper on the histogram.
 *
 *  Keeps track of all running campaigns including their:
 *      market_segments
 *      reach
 *
 * Should be updated with each daily message
 *
 * Created by Stefa on 29/11/2016.
 */
public interface MarketMonitorInterface {

    Collection<Campaign> getAllCampaignsOnDay(int day);

    Collection<Campaign> getAllCampaigns();

    void addCampaign(Campaign new_camp);

    public int getRequiredImpressionsOnDay(int day, Set<MarketSegment> targetSegment);

    Set<MarketSegment> getTargetSegmentsOnDay(int day);

}

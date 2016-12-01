package org.dank.entities;

import tau.tac.adx.report.adn.MarketSegment;

import java.util.Set;

/**
 * Created by Stefa on 29/11/2016.
 */
public interface Campaign {

    int getStartDate();
    int getEndDate();
    int getReach();
    double getRequiredImpressionsPerDay();
    Set<MarketSegment> getTargetSegment();




}

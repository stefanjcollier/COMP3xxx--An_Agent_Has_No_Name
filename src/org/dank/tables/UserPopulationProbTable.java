package org.dank.tables;

import tau.tac.adx.report.adn.MarketSegment;

import java.util.Set;

/**
 * Allocated to: Stefan
 *
 * Created by Stefa on 29/11/2016.
 */
public interface UserPopulationProbTable {

    static UserPopulationProbTable getInstance(){
        return new UserPopulations();
    }

    double percentOfPopulation(Set<MarketSegment> segment);
}

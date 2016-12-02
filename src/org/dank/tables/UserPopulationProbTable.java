package org.dank.tables;

import tau.tac.adx.report.adn.MarketSegment;

import java.util.Set;

/**
 *
 * Contains the data from table 2, the User Population Probabilities

 * Allocated to: Stefan
 *
 * Created by Stefa on 29/11/2016.
 */
public interface UserPopulationProbTable {

    static UserPopulationProbTable getInstance(){
        return new UserPopulations();
    }

    double percentOfPopulation(Set<MarketSegment> segment);

    int getPopulation();
}

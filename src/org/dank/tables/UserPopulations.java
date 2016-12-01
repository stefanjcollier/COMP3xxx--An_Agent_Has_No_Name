package org.dank.tables;

import tau.tac.adx.report.adn.MarketSegment;

import java.util.Map;
import java.util.Set;

/**
 * Created by Stefa on 30/11/2016.
 */
public class UserPopulations implements UserPopulationProbTable {

    private Map<Set<MarketSegment>,Integer> segmentToPopMap;

    public UserPopulations(){
         this.segmentToPopMap = MarketSegment.usersInMarketSegments();
    }

    @Override
    public double percentOfPopulation(Set<MarketSegment> segment) {
        return this.segmentToPopMap.get(segment) / 10000;
    }

    public static void main(String[] args){
        UserPopulations u = new UserPopulations();
        Set<MarketSegment> segments = MarketSegment.compundMarketSegment2(MarketSegment.FEMALE, MarketSegment.LOW_INCOME);
        System.out.println(u.percentOfPopulation(segments));
    }
}

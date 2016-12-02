package org.dank.tables.publisher;

import org.dank.tables.PublisherProbTable;
import org.dank.tables.publisher.PublisherGenderStatistics;
import org.dank.tables.publisher.PublisherToken;
import tau.tac.adx.report.adn.MarketSegment;

import java.util.Set;

/**
 *
 *
 * Created by Stefa on 01/12/2016.
 */
public class PublisherStatistics implements PublisherProbTable{

    private PublisherGenderStatistics   genderStat;
    private PublisherIncomeStatistics   incomeStat;
    private PublisherAgeStatistcs       ageStat;
    private PublisherDeviceStatistics   deviceStat;

    public PublisherStatistics(){
        this.genderStat = new PublisherGenderStatistics();
        this.incomeStat = new PublisherIncomeStatistics();
        this.ageStat = new PublisherAgeStatistcs();
        this.deviceStat = new PublisherDeviceStatistics();
    }

    /**
     *  Determine the probabillity of a user being of the given market segments given they are visiting a publisher.
     *  i.e. P(u|s) s.t. u := user & s := site/publisher
     *
     * A user is catagorized by the market segments.
     *
     * @param publisher {@link PublisherToken} -- A publisher from the given list
     * @param segments {@link Set}<{@link MarketSegment}> -- The type of user you are querying
     * @return The chance of a user visitng the given publisher
     */
    @Override
    public double getChanceOf(PublisherToken publisher, Set<MarketSegment> segments){
        double prob = 1.0;
        for (MarketSegment segment : segments){
            prob *= handleGenderProb(publisher, segment);
            prob *= handleAgeProb(publisher, segment);
            prob *= handleIncomeProb(publisher, segment);
        }
        return prob;
    }

    /**
     *  Determine the probabillity of a user being of the given market segments given they are visiting a publisher.
     *  i.e. P(u|s) s.t. u := user & s := site/publisher
     *
     * A user is catagorized by the market segments.
     *
     * @param publisher_name {@link String} -- The name of the publisher from the given list
     * @param segments {@link Set}<{@link MarketSegment}> -- The type of user you are querying
     * @return The chance of a user visitng the given publisher
     */
    @Override
    public double getChanceOf(String publisher_name, Set<MarketSegment> segments){
        PublisherToken publisher = PublisherToken.valueOf(publisher_name);
        return this.getChanceOf(publisher, segments);
    }

    /**
     *  Determine the probabillity of a user being of the given market segments given they are visiting a publisher
     *  and the chance they are viewing it on a given device.
     *  i.e. P(u|s) s.t. u := user & s := site/publisher
     *
     * A user is catagorized by the market segments.
     *
     * @param publisher {@link PublisherToken} -- A publisher from the given list
     * @param segments {@link Set}<{@link MarketSegment}> -- The type of user you are querying
     * @param device {@link org.dank.tables.PublisherProbTable.Device} -- The device you are querying
     * @return The chance of a user visitng the given publisher
     */
    @Override
    public double getChanceOf(PublisherToken publisher, Set<MarketSegment> segments, Device device){
        double device_prob;
        switch (device){
            case MOBILE: {
                device_prob = this.handleDeviveProb(publisher,true); break;
            }case DESKTOP:{
                device_prob = this.handleDeviveProb(publisher,false); break;
            }default: {
                device_prob = 1.0;
            }
        }
        return this.getChanceOf(publisher, segments) * device_prob;
    }

    /**
     *  Determine the probabillity of a user being of the given market segments given they are visiting a publisher
     *  and the chance they are viewing it on a given device.
     *  i.e. P(u|s) s.t. u := user & s := site/publisher
     *
     * A user is catagorized by the market segments.
     *
     * @param publisher_name {@link String} -- The name of a publisher from the given list
     * @param segments {@link Set}<{@link MarketSegment}> -- The type of user you are querying
     * @param device {@link org.dank.tables.PublisherProbTable.Device} -- The device you are querying
     * @return The chance of a user visitng the given publisher
     */
    @Override
    public double getChanceOf(String publisher_name, Set<MarketSegment> segments, Device device){
        PublisherToken publisher = PublisherToken.valueOf(publisher_name);
        return this.getChanceOf(publisher, segments, device);
    }



    protected double handleAgeProb(PublisherToken publisher, MarketSegment ageSegments){
        double total = 1.0;
        for (int age : getAgeTokens(ageSegments)){
            total *= this.ageStat.getAgeProb(publisher,age);
        }
        return total;
    }

    protected double handleIncomeProb(PublisherToken publisher, MarketSegment incomeSegment){
        double total = 1.0;
        for (int income : getIncomeTokens(incomeSegment)){
            total *= this.incomeStat.getIncomeProb(publisher,income);
        }
        return total;
    }

    protected double handleGenderProb(PublisherToken publisher, MarketSegment gender){
        switch (gender) {
            case MALE: {
                return this.genderStat.isMale(publisher);
            }
            case FEMALE: {
                return this.genderStat.isFemale(publisher);
            }
            default:{
                return 1.0;
            }
        }
    }
    protected double handleDeviveProb(PublisherToken publisher, boolean onMobile){
        if (onMobile){
            return this.deviceStat.onMobile(publisher);
        }else{
            return this.deviceStat.onDesktop(publisher);
        }
    }


    private int[] getIncomeTokens(MarketSegment incomeSegment){
        switch (incomeSegment) {
            case LOW_INCOME: {
                return new int[]
                        { PublisherIncomeStatistics.inc0_30,
                                PublisherIncomeStatistics.inc30_60 };
            }
            case HIGH_INCOME: {
                return new int[]
                        { PublisherIncomeStatistics.inc60_100,
                                PublisherIncomeStatistics.inc100plus };
            }
            default: {
                return new int[] {};
            }
        }
    }


    private int[] getAgeTokens(MarketSegment ageSegment){
        switch (ageSegment) {
            case YOUNG: {
                return new int[]
                        { PublisherAgeStatistcs.age18_24,
                          PublisherAgeStatistcs.age25_34,
                          PublisherAgeStatistcs.age35_44};
            }
            case OLD: {
                return new int[]
                        { PublisherAgeStatistcs.age45_54,
                                PublisherAgeStatistcs.age55_64,
                                PublisherAgeStatistcs.age65plus};
            }
            default: {
                return new int[] {};
            }
        }
    }


}

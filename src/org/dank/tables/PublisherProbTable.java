package org.dank.tables;

import org.dank.tables.publisher.PublisherStatistics;
import org.dank.tables.publisher.PublisherToken;
import tau.tac.adx.report.adn.MarketSegment;

import java.util.Set;

/**
 * Contains the data from table 3, the probabilities of a user having given attributes based on site choice.
 *
 * Allocated to: Stefan
 *
 * Created by Stefa on 29/11/2016.
 */
public interface PublisherProbTable {

    public enum Device {
        MOBILE, DESKTOP;
    }
    static PublisherProbTable getInstance(){
        return new PublisherStatistics();
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
    double getChanceOf(String publisher_name, Set<MarketSegment> segments);

    /**
     *  Determine the probabillity of a user being of the given market segments given they are visiting a publisher.
     *  i.e. P(u|s) s.t. u := user & s := site/publisher
     *
     * A user is catagorized by the market segments.
     *
     * @param publisher {@link PublisherToken} -- The publisher from the given list
     * @param segments {@link Set}<{@link MarketSegment}> -- The type of user you are querying
     * @return The chance of a user visitng the given publisher
     */
    double getChanceOf(PublisherToken publisher, Set<MarketSegment> segments);


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
    double getChanceOf(PublisherToken publisher, Set<MarketSegment> segments, Device device);
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
    public double getChanceOf(String publisher_name, Set<MarketSegment> segments, Device device);

    PublisherProbTable INSTANCE = new PublisherStatistics();

}

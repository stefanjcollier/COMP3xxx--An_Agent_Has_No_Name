package org.dank.tables.publisher;

/**
 * The table that contains the chance of a user being of each gender on a site
 *
 * Created by Stefan on 01/12/2016.
 */
public class PublisherGenderStatistics {

    public final double[] chance_of_male = new double[] {
            0.496,
            0.486,
            0.476,
            0.466,
            0.476,
            0.486,

            0.476,
            0.486,
            0.456,
            0.456,
            0.476,
            0.466,

            0.456,
            0.476,
            0.486,
            0.466,
            0.506,
            0.476
    };

    protected PublisherGenderStatistics(){}

    public double isMale(PublisherToken publisher){
        return chance_of_male[publisher.getIndex()];
    }

    public double isFemale(PublisherToken publisher){
        return 1 - chance_of_male[publisher.getIndex()];
    }

}

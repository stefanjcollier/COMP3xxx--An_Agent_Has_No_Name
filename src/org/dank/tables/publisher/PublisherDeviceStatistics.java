package org.dank.tables.publisher;

/**
 * The table that contains the chance of a user being of each gender on a site
 *
 * Created by Stefan on 01/12/2016.
 */
public class PublisherDeviceStatistics {

    public final double[] chance_of_mobile = new double[] {
            0.26,
            0.24,
            0.23,
            0.22,
            0.25,
            0.24,

            0.21,
            0.22,
            0.18,
            0.19,
            0.20,
            0.19,

            0.24,
            0.28,
            0.28,
            0.30,
            0.27,
            0.31

    };

    protected PublisherDeviceStatistics(){}

    public double onMobile(PublisherToken publisher){
        return chance_of_mobile[publisher.getIndex()];
    }

    public double onDesktop(PublisherToken publisher){
        return 1 - chance_of_mobile[publisher.getIndex()];
    }

}

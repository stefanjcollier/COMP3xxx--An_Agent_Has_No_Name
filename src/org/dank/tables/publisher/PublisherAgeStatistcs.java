package org.dank.tables.publisher;

import java.util.stream.DoubleStream;

/**
 * Contains the stats from table 3 on visitor ages
 *
 * Created by Stefa on 01/12/2016.
 */
public class PublisherAgeStatistcs {
    public static final int age18_24 = 0;
    public static final int age25_34 = 1;
    public static final int age35_44 = 2;
    public static final int age45_54 = 3;
    public static final int age55_64 = 4;
    public static final int age65plus = 5;

    protected final double[][] chances_of_ages = new double[][]
            {
                    {0.122, 0.171, 0.167, 0.184, 0.164, 0.192},
                    {0.102, 0.161, 0.167, 0.194, 0.174, 0.202},
                    {0.092, 0.151, 0.167, 0.194, 0.174, 0.222},
                    {0.102, 0.161, 0.167, 0.194, 0.174, 0.202},
                    {0.102, 0.161, 0.167, 0.194, 0.174, 0.202},
                    {0.092, 0.151, 0.167, 0.194, 0.184, 0.212},

                    {0.092, 0.151, 0.167, 0.194, 0.184, 0.212},
                    {0.092, 0.161, 0.157, 0.194, 0.174, 0.222},
                    {0.072, 0.151, 0.167, 0.204, 0.184, 0.222},
                    {0.092, 0.171, 0.177, 0.184, 0.174, 0.202},
                    {0.102, 0.141, 0.167, 0.204, 0.174, 0.212},
                    {0.092, 0.121, 0.167, 0.204, 0.184, 0.232},

                    {0.092, 0.151, 0.157, 0.194, 0.184, 0.222},
                    {0.102, 0.151, 0.157, 0.194, 0.174, 0.222},
                    {0.102, 0.131, 0.157, 0.204, 0.184, 0.222},
                    {0.082, 0.161, 0.177, 0.204, 0.174, 0.202},
                    {0.122, 0.151, 0.157, 0.184, 0.174, 0.212},
                    {0.092, 0.151, 0.167, 0.204, 0.184, 0.202},
            };

    public double getAgeProb(PublisherToken publisher, int age_token){
        return getAgeChances(publisher)[age_token];
    }
    public double[] getAgeChances(PublisherToken publisher){
        return this.chances_of_ages[publisher.getIndex()];
    }

    public double is18_24(PublisherToken publisher){
        return this.getAgeProb(publisher,age18_24);
    }
    public double is25_34(PublisherToken publisher){
        return this.getAgeProb(publisher,age25_34);
    }
    public double is35_44(PublisherToken publisher){
        return this.getAgeProb(publisher,age35_44);
    }
    public double is45_54(PublisherToken publisher){
        return this.getAgeProb(publisher,age45_54);
    }
    public double is55_64(PublisherToken publisher){
        return this.getAgeProb(publisher,age55_64);
    }
    public double is65plus(PublisherToken publisher){
        return this.getAgeProb(publisher,age65plus);
    }

    public static void main(String[] args){
        int i  = 0;
        for (double[] row : new PublisherAgeStatistcs().chances_of_ages) {
            double sum = DoubleStream.of(row).sum();
            System.out.println(i+"   "+sum+"+"+(1-sum));
            i++;
        }
    }

}

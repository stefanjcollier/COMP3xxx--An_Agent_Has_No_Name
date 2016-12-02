package org.dank.tables.publisher;

import java.util.StringJoiner;
import java.util.stream.DoubleStream;

/**
 * Contains the stats from table 3 on visitor incomes
 *
 * Created by Stefa on 01/12/2016.
 */
public class PublisherIncomeStatistics {
    public static final int inc0_30 = 0;
    public static final int inc30_60 = 0;
    public static final int inc60_100 = 0;
    public static final int inc100plus = 0;

    protected final double[][] chances_of_incomes = new double[][]
            {
                    {0.53, 0.27, 0.13, 0.07},
                    {0.48, 0.27, 0.16, 0.09},
                    {0.47, 0.26, 0.17, 0.10},
                    {0.47, 0.27, 0.17, 0.09},
                    {0.49, 0.27, 0.16, 0.08},
                    {0.46, 0.26, 0.18, 0.10},

                    {0.50, 0.27, 0.15, 0.08},
                    {0.50, 0.27, 0.15, 0.08},
                    {0.47, 0.28, 0.19, 0.06},
                    {0.45, 0.27, 0.19, 0.09},
                    {0.465, 0.27, 0.19, 0.075},
                    {0.45, 0.25, 0.20, 0.10},

                    {0.46, 0.265, 0.185, 0.09},
                    {0.5, 0.27, 0.15, 0.08},
                    {0.5, 0.28, 0.15, 0.07},
                    {0.465, 0.26, 0.175, 0.1},
                    {0.48, 0.265, 0.165, 0.09},
                    {0.455, 0.265, 0.185, 0.095},
            };

    public double has0_30Income(PublisherToken publisher){
        return this.getIncomeChances(publisher)[inc0_30];
    }
    public double has30_60Income(PublisherToken publisher){
        return this.getIncomeChances(publisher)[inc30_60];
    }
    public double has60_100Income(PublisherToken publisher){
        return this.getIncomeChances(publisher)[inc60_100];
    }
    public double has100plusIncome(PublisherToken publisher){
        return this.getIncomeChances(publisher)[inc100plus];
    }
    public double getIncomeProb(PublisherToken publisher, int income_token){
        return this.getIncomeChances(publisher)[income_token];
    }
    public double[] getIncomeChances(PublisherToken publisher){
        return this.chances_of_incomes[publisher.getIndex()];
    }


    public static void main(String args[]){

        int index = 0;
        boolean all_good = true;
        for (double[] row : new PublisherIncomeStatistics().chances_of_incomes){
            double sum = DoubleStream.of(row).sum();
            if (sum != 1.0){
                System.out.println(String.format("Row %d has a sum of %f",index,sum));
                all_good = false;
            }else {
                System.out.println(String.format("Row %d good",index));
            }
            index++;
        }
        System.out.println("Done: "+((all_good)?"All Good!":"Failures"));

    }
}

package org.dank.tables.publisher;

/**
 * Created by Stefa on 01/12/2016.
 */
public class PublisherIncomeStatistics {
    public static final int inc0_30 = 0;
    public static final int inc30_60 = 0;
    public static final int inc60_100 = 0;
    public static final int inc100plus = 0;

    public final double[][] chances_of_incomes = new double[][]
            {
                    {0.53, 0.27, 0.13, 0.07},
                    {},
                    {},
                    {},
                    {},
                    {},

                    {},
                    {},
                    {},
                    {},
                    {},
                    {},

                    {},
                    {},
                    {},
                    {},
                    {},
                    {},
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
    public double hasIncome(PublisherToken publisher, int income_token){
        return this.getIncomeChances(publisher)[income_token];
    }
    public double[] getIncomeChances(PublisherToken publisher){
        return this.chances_of_incomes[publisher.getIndex()];
    }

}

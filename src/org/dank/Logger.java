package org.dank;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import org.dank.entities.Campaign;
import tau.tac.adx.report.adn.MarketSegment;
import tau.tac.adx.report.demand.AdNetworkDailyNotification;

/**
 * Created by EKREM on 9.12.2016.
 */
public class Logger {

    String path = "ucsLog.csv";

    String campaignPath = "campaignLog.csv";

    String ucsLog = "";

    String campaignLog = "";


    public Logger(String path){

        this.path = path;
    }

    public Logger(){


    }

    public void logUCS(double ucsLevel, double prevBid){

        ucsLog += ucsLevel + "," + prevBid + "\n";
        BufferedWriter bw = null;
        FileWriter fw = null;

        try {

            fw = new FileWriter(path);
            bw = new BufferedWriter(fw);
            bw.write(ucsLog);



        } catch (IOException e) {

            e.printStackTrace();

        } finally {

            try {

                if (bw != null)
                    bw.close();

                if (fw != null)
                    fw.close();

            } catch (IOException ex) {

                ex.printStackTrace();

            }

        }
    }

    public void logCampaign(Campaign c, double budget){
        String record = "";
        //MALE, FEMALE, YOUNG, OLD, LOW_INCOME, HIGH_INCOME, startDay, endDay, winningBid, Reach

        boolean[] attributes = {false,false,false,false,false,false};

        for(MarketSegment m : c.getTargetSegment()){
            if(m == MarketSegment.MALE){
                attributes[0] = true;
            }else if(m == MarketSegment.FEMALE){
                attributes[1] = true;
            }else if(m == MarketSegment.YOUNG){
                attributes[2] = true;
            }else if(m == MarketSegment.OLD){
                attributes[3] = true;
            }else if(m == MarketSegment.LOW_INCOME){
                attributes[4] = true;
            }else if(m == MarketSegment.HIGH_INCOME){
                attributes[5] = true;
            }
        }

        for(boolean b : attributes){

            record += b ? "1," : "0," ;

        }
        record += budget + ",";
        record += c.getOurBid() + ",";
        record += c.getReachImps() + ",";
        record += (c.getDayEnd() - c.getDayStart() + 1) + "\n";

        campaignLog += record;

        BufferedWriter bw = null;
        FileWriter fw = null;

        try {

            fw = new FileWriter(campaignPath);
            bw = new BufferedWriter(fw);
            bw.write(campaignLog);



        } catch (IOException e) {

            e.printStackTrace();

        } finally {

            try {

                if (bw != null)
                    bw.close();

                if (fw != null)
                    fw.close();

            } catch (IOException ex) {

                ex.printStackTrace();

            }

        }
    }

}

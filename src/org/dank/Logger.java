package org.dank;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by EKREM on 9.12.2016.
 */
public class Logger {

    String path = "ucsLog.csv";

    String ucsLog = "";


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

}

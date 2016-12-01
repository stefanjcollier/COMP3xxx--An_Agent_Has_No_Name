package org.dank.tables.publisher;

/**
 * Created by Stefa on 01/12/2016.
 */
public enum PublisherToken {

    YAHOO(0,"yahoo"),
    CNN(1,"cnn"),
    NY_TIMES(2,"NY Times"),
    HFNGTN(3,"Hfngtn"),
    MSN(4,"MSN"),
    FOX(5,"Fox"),
    AMAZON(6,"Amazon"),
    EBAY(7,"Ebay"),
    WAL_MART(8,"Wal-Mart"),
    TARGET(9,"Target"),
    BESTBUY(10,"BestBuy"),
    SEARS(11,"Sears"),
    WEBMD(12,"WebMD"),
    EHOW(13,"EHow"),
    ASK(14,"Ask"),
    TRIPADVISOR(15,"TripAdvisor"),
    CNET(16,"CNet"),
    WEATHER(17,"Weather");

    private int index;
    private String name;

    PublisherToken(int index, String name){
        this.index = index;
        this.name = name;
    }

    public int getIndex(){ return this.index; }

}

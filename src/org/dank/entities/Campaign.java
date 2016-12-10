package org.dank.entities;

import tau.tac.adx.demand.CampaignStats;
import tau.tac.adx.props.AdxQuery;
import tau.tac.adx.report.adn.MarketSegment;
import tau.tac.adx.report.demand.CampaignOpportunityMessage;
import tau.tac.adx.report.demand.InitialCampaignMessage;

import java.util.Set;

/**
 * Created by vlad on 01/12/2016.
 */
public class Campaign {

    private static char newName = (char)((int)'a'-1);
    private char niceName;


    private int id;
    private Long reachImps;
    private long dayStart;
    private long dayEnd;
    private Set<MarketSegment> targetSegment;
    private double videoCoef;
    private double mobileCoef;
    private AdxQuery[] campaignQueries; // array of queries relevant for the campaign
    /* campaign info as reported */
    private CampaignStats stats;
    private double budget;

    private double ourBid;

    public Campaign(InitialCampaignMessage icm) {
        reachImps = icm.getReachImps();
        dayStart = icm.getDayStart();
        dayEnd = icm.getDayEnd();
        targetSegment = icm.getTargetSegment();
        videoCoef = icm.getVideoCoef();
        mobileCoef = icm.getMobileCoef();
        id = icm.getId();

        stats = new CampaignStats(0, 0, 0);
        budget = 0.0;
        this.niceName = (char)((int)Campaign.newName++);
    }

    public Campaign(CampaignOpportunityMessage com) {
        dayStart = com.getDayStart();
        dayEnd = com.getDayEnd();
        id = com.getId();
        reachImps = com.getReachImps();
        targetSegment = com.getTargetSegment();
        mobileCoef = com.getMobileCoef();
        videoCoef = com.getVideoCoef();
        stats = new CampaignStats(0, 0, 0);
        budget = 0.0;
        this.niceName = (char)((int)Campaign.newName++);
    }

    @Override
    public String toString() {
        return "Campaign ("+this.niceName+") ID " + id + ": " + "day " + dayStart + " to "
                + dayEnd + " " + targetSegment + ", reach: " + reachImps
                + " coefs: (v=" + videoCoef + ", m=" + mobileCoef + ")";
    }

    public char getNiceName(){
        return this.niceName;
    }

    public double getOurBid() {
        return ourBid;
    }

    public void setOurBid(double ourBid) {
        this.ourBid = ourBid;
    }

    public int impsTogo() {
        return (int) Math.max(0, reachImps - stats.getTargetedImps());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Long getReachImps() {
        return reachImps;
    }

    public void setReachImps(Long reachImps) {
        this.reachImps = reachImps;
    }

    public long getDayStart() {
        return dayStart;
    }

    public void setDayStart(long dayStart) {
        this.dayStart = dayStart;
    }

    public long getDayEnd() {
        return dayEnd;
    }

    public void setDayEnd(long dayEnd) {
        this.dayEnd = dayEnd;
    }

    public Set<MarketSegment> getTargetSegment() {
        return targetSegment;
    }

    public void setTargetSegment(Set<MarketSegment> targetSegment) {
        this.targetSegment = targetSegment;
    }

    public double getVideoCoef() {
        return videoCoef;
    }

    public void setVideoCoef(double videoCoef) {
        this.videoCoef = videoCoef;
    }

    public double getMobileCoef() {
        return mobileCoef;
    }

    public AdxQuery[] getCampaignQueries() {
        return campaignQueries;
    }

    public void setCampaignQueries(AdxQuery[] campaignQueries) {
        this.campaignQueries = campaignQueries;
    }

    public void setMobileCoef(double mobileCoef) {
        this.mobileCoef = mobileCoef;
    }

    public CampaignStats getStats() {
        return stats;
    }

    public void setStats(CampaignStats s) {
        stats.setValues(s);
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }

    // Made by Stefan
    public long getLength(){ return this.dayEnd - this.dayStart + 1; }

    // Made by Stefan
    public boolean isRunningOnDay(long day){
        return (this.dayStart <= day && day <= this.dayEnd);
    }
}

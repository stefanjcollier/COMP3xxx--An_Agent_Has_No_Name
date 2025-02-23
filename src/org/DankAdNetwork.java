package org;

import edu.umich.eecs.tac.props.Ad;
import edu.umich.eecs.tac.props.BankStatus;
import org.dank.MarketMonitor;
import org.dank.bidder.ImpressionBidder;
import org.dank.bidder.UCSBidder;
import org.dank.bidder.CampaignBidder;
import org.dank.bidder.index.PriceIndexPredictor;
import org.dank.entities.Campaign;
import se.sics.isl.transport.Transportable;
import se.sics.tasim.aw.Agent;
import se.sics.tasim.aw.Message;
import se.sics.tasim.props.SimulationStatus;
import se.sics.tasim.props.StartInfo;
import tau.tac.adx.ads.properties.AdType;
import tau.tac.adx.demand.CampaignStats;
import tau.tac.adx.devices.Device;
import tau.tac.adx.props.*;
import tau.tac.adx.report.adn.AdNetworkReport;
import tau.tac.adx.report.adn.MarketSegment;
import tau.tac.adx.report.demand.*;
import tau.tac.adx.report.demand.campaign.auction.CampaignAuctionReport;
import tau.tac.adx.report.publisher.AdxPublisherReport;
import tau.tac.adx.report.publisher.AdxPublisherReportEntry;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * @author Test plug-in
 */
public class DankAdNetwork extends Agent {

    private final Logger log = Logger
            .getLogger(DankAdNetwork.class.getName());

    /*
     * Basic simulation information. An agent should receive the {@link
     * StartInfo} at the beginning of the game or during recovery.
     */
    @SuppressWarnings("unused")
    private StartInfo startInfo;

    /**
     * Messages received:
     * <p>
     * We keep all the {@link CampaignReport campaign reports} delivered to the
     * agent. We also keep the initialization messages {@link PublisherCatalog}
     * and {@link InitialCampaignMessage} and the most recent messages and
     * reports {@link CampaignOpportunityMessage}, {@link CampaignReport}, and
     * {@link AdNetworkDailyNotification}.
     */
    private final Queue<CampaignReport> campaignReports;
    private PublisherCatalog publisherCatalog;
    private InitialCampaignMessage initialCampaignMessage;
    private AdNetworkDailyNotification adNetworkDailyNotification;

    /*
     * The addresses of server entities to which the agent should send the daily
     * bids data
     */
    private String demandAgentAddress;
    private String adxAgentAddress;

    /*
     * we maintain a list of queries - each characterized by the web site (the
     * publisher), the device type, the ad type, and the user market segment
     */
    private AdxQuery[] queries;

    /**
     * Information regarding the latest campaign opportunity announced
     */
    private Campaign pendingCampaign;

    /**
     * We maintain a collection (mapped by the campaign id) of the campaigns won
     * by our agent.
     */
    private Map<Integer, Campaign> myCampaigns;

    /*
     * the bidBundle to be sent daily to the AdX
     */
    private AdxBidBundle bidBundle;

    /*
     * The current bid level for the user classification service
     */
    private double ucsBid;

    /*
     * The targeted service level for the user classification service
     */
    private double ucsTargetLevel;

    /*
     * current day of simulation
     */
    private int day;
    private String[] publisherNames;
    private Campaign currCampaign;

    private UCSBidder ucsbidder;
    private ImpressionBidder impressionBidder;
    private CampaignBidder campaignBidder;
    private org.dank.Logger logger;
    private PriceIndexPredictor predictor;
    private MarketMonitor marketMonitor;
    private State state;
    double qualityScore;

    public DankAdNetwork() {
        state = new State();

        marketMonitor = new MarketMonitor();
        logger = new org.dank.Logger();
        predictor = new PriceIndexPredictor(marketMonitor);
        campaignReports = new LinkedList<CampaignReport>();
        ucsbidder = new UCSBidder(this);
        impressionBidder = new ImpressionBidder(predictor);
        campaignBidder = new CampaignBidder(predictor, state);
        qualityScore = 1;
    }

    public Set<Campaign> getAllocatedCampaigns() {

        return new HashSet<Campaign>(myCampaigns.values());
    }

    @Override
    protected void messageReceived(Message message) {
        try {
            Transportable content = message.getContent();

            // log.fine(message.getContent().getClass().toString());

            if (content instanceof InitialCampaignMessage) {
                handleInitialCampaignMessage((InitialCampaignMessage) content);

            } else if (content instanceof CampaignOpportunityMessage) {
                handleICampaignOpportunityMessage((CampaignOpportunityMessage) content);

            } else if (content instanceof CampaignReport) {
                handleCampaignReport((CampaignReport) content);

            } else if (content instanceof AdNetworkDailyNotification) {
                handleAdNetworkDailyNotification((AdNetworkDailyNotification) content);

            } else if (content instanceof AdxPublisherReport) {
                handleAdxPublisherReport((AdxPublisherReport) content);

            } else if (content instanceof SimulationStatus) {
                handleSimulationStatus((SimulationStatus) content);

            } else if (content instanceof PublisherCatalog) {
                handlePublisherCatalog((PublisherCatalog) content);

            } else if (content instanceof AdNetworkReport) {
                handleAdNetworkReport((AdNetworkReport) content);

            } else if (content instanceof StartInfo) {
                handleStartInfo((StartInfo) content);

            } else if (content instanceof BankStatus) {
                handleBankStatus((BankStatus) content);

            } else if (content instanceof CampaignAuctionReport) {
                hadnleCampaignAuctionReport((CampaignAuctionReport) content);

            } else if (content instanceof ReservePriceInfo) {
                // ((ReservePriceInfo)content).getReservePriceType();
            } else {
                System.out.println("UNKNOWN Message Received: " + content);
            }

        } catch (NullPointerException e) {
            this.log.log(Level.SEVERE,
                    "Exception thrown while trying to parse message:\n" + message.toString() + "\nError:");
            e.printStackTrace();
            return;
        }
    }

    private void hadnleCampaignAuctionReport(CampaignAuctionReport content) {
        // ingoring - this message is obsolete
    }

    private void handleBankStatus(BankStatus content) {
        System.out.println("Day " + day + " :" + content.toString());
    }

    /**
     * Processes the start information.
     *
     * @param startInfo the start information.
     */
    protected void handleStartInfo(StartInfo startInfo) {
        this.startInfo = startInfo;
    }

    /**
     * Process the reported set of publishers
     *
     * @param publisherCatalog
     */
    private void handlePublisherCatalog(PublisherCatalog publisherCatalog) {
        this.publisherCatalog = publisherCatalog;
        generateAdxQuerySpace();
        getPublishersNames();

    }

    /**
     * On day 0, a campaign (the "initial campaign") is allocated to each
     * competing agent. The campaign starts on day 1. The address of the
     * server's AdxAgent (to which bid bundles are sent) and DemandAgent (to
     * which bids regarding campaign opportunities may be sent in subsequent
     * days) are also reported in the initial campaign message
     */
    private void handleInitialCampaignMessage(
            InitialCampaignMessage campaignMessage) {
        System.out.println(campaignMessage.toString());

        day = 0;


        initialCampaignMessage = campaignMessage;
        demandAgentAddress = campaignMessage.getDemandAgentAddress();
        adxAgentAddress = campaignMessage.getAdxAgentAddress();

        Campaign campaign = new Campaign(initialCampaignMessage);
        campaign.setBudget(initialCampaignMessage.getBudgetMillis() / 1000.0);
        currCampaign = campaign;
        genCampaignQueries(currCampaign);
        /*
         * The initial campaign is already allocated to our agent so we add it
		 * to our allocated-campaigns list.
		 */
        System.out.println("Day " + day + ": Allocated campaign - " + campaign);
        this.marketMonitor.addCampaign(campaign);
        myCampaigns.put(initialCampaignMessage.getId(), campaign);
    }

    /**
     * On day n ( > 0) a campaign opportunity is announced to the competing
     * agents. The campaign starts on day n + 2 or later and the agents may send
     * (on day n) related bids (attempting to win the campaign). The allocation
     * (the winner) is announced to the competing agents during day n + 1.
     */
    private void handleICampaignOpportunityMessage(
            CampaignOpportunityMessage com) {

        day = com.getDay();

        pendingCampaign = new Campaign(com);


        System.out.println("Day " + day + ": Campaign opportunity - " + pendingCampaign);

		/*
         * The campaign requires com.getReachImps() impressions. The competing
		 * Ad Networks bid for the total campaign Budget (that is, the ad
		 * network that offers the lowest budget gets the campaign allocated).
		 * The advertiser is willing to pay the AdNetwork at most 1$ CPM,
		 * therefore the total number of impressions may be treated as a reserve
		 * (upper bound) price for the auction.
		 */

        Random random = new Random();
        long cmpimps = com.getReachImps();
//		long cmpBidMillis = random.nextInt((int)cmpimps);

        long cmpBidMillis = (long) campaignBidder.getBidFor(pendingCampaign, qualityScore); //TODO should we do any multiplication/division here?

        pendingCampaign.setOurBid(cmpBidMillis);

        marketMonitor.addCampaign(pendingCampaign);

        System.out.println("Day " + day + ": Campaign total budget bid (millis): " + cmpBidMillis);

		/*
         * Adjust ucs bid s.t. target level is achieved. Note: The bid for the
		 * user classification service is piggybacked
		 */

        if (adNetworkDailyNotification != null) {
            double ucsLevel = adNetworkDailyNotification.getServiceLevel();
            //ucsBid = 0.1 + random.nextDouble()/10.0;
            if (ucsBid == 0) {

                ucsBid = 0.1 + random.nextDouble() / 10.0;

            } else {
                ucsBid = ucsbidder.calcUCSBid( ucsBid,  ucsLevel, day);

            }


            System.out.println("Day " + day + ": ucs level reported: " + ucsLevel);
        } else {
            System.out.println("Day " + day + ": Initial ucs bid is " + ucsBid);
        }

		/* Note: Campaign bid is in millis */
        AdNetBidMessage bids = new AdNetBidMessage(ucsBid, pendingCampaign.getId(), cmpBidMillis);
        sendMessage(demandAgentAddress, bids);
    }

    /**
     * On day n ( > 0), the result of the UserClassificationService and Campaign
     * auctions (for which the competing agents sent bids during day n -1) are
     * reported. The reported Campaign starts in day n+1 or later and the user
     * classification service level is applicable starting from day n+1.
     */
    private void handleAdNetworkDailyNotification(
            AdNetworkDailyNotification notificationMessage) {

        adNetworkDailyNotification = notificationMessage;

        System.out.println("Day " + day + ": Daily notification for campaign ("+findNiceNameOfCampaign(adNetworkDailyNotification.getCampaignId())+") "
                + adNetworkDailyNotification.getCampaignId());

        String campaignAllocatedTo = " allocated to "
                + notificationMessage.getWinner();


        // Update the CI
        double budget = notificationMessage.getCostMillis() / 1000.0; // the bid of whoever won it
        double bid = pendingCampaign.getBudget(); //our bid
        state.informOfCampaignOutcome(budget, bid);
        logger.logCampaign(pendingCampaign,budget);

        if ((pendingCampaign.getId() == adNetworkDailyNotification.getCampaignId())
                && (notificationMessage.getCostMillis() != 0)) {

			/* add campaign to list of won campaigns */
            pendingCampaign.setBudget(notificationMessage.getCostMillis() / 1000.0);
            currCampaign = pendingCampaign;
            genCampaignQueries(currCampaign);
            myCampaigns.put(pendingCampaign.getId(), pendingCampaign);

            campaignAllocatedTo = " WON at cost (Millis)"
                    + notificationMessage.getCostMillis();
        }

        qualityScore = notificationMessage.getQualityScore();

        System.out.println("Day " + day + ": " + campaignAllocatedTo
                + ". UCS Level set to " + notificationMessage.getServiceLevel()
                + " at price " + notificationMessage.getPrice()
                + " Quality Score is: " + notificationMessage.getQualityScore());
    }

    /**
     * The SimulationStatus message received on day n indicates that the
     * calculation time is up and the agent is requested to send its bid bundle
     * to the AdX.
     */
    private void handleSimulationStatus(SimulationStatus simulationStatus) {
        System.out.println("Day " + day + " : Simulation Status Received");
        sendBidAndAds();
        System.out.println("Day " + day + " ended. Starting next day");
        ++day;
    }

    /**
     *
     */
    protected void sendBidAndAds() {
        bidBundle = new AdxBidBundle();

		/*
		 *
		 */

        int dayBiddingFor = day + 1;


        int tomorrow = day + 1;
        System.out.println("==================================================================================================================");
        System.out.println("==================================================================================================================");
        System.out.println("===================[Bidding Time, tomorrow: day " + tomorrow + "]========================================================");
        Set<Campaign> runningCamps = new HashSet<>();
        for (Campaign myCamp : this.myCampaigns.values()) {
            if (myCamp.isRunningOnDay(tomorrow)) {
                runningCamps.add(myCamp);
            }
        }
        System.out.println("--------------------");
        System.out.println("My Campaigns(" + runningCamps.size() + "):");
        for (Campaign rc : runningCamps) {
            System.out.println("-("+rc.getNiceName()+") "+
                    rc.toString().split("coef")[0].replace("reach: "," Target Reach:")+
                    "  ImpsToGo:"+rc.impsTogo()+
                    "  imp completion:"+100*rc.getStats().getTargetedImps()/rc.getReachImps()+"%"
            );
        }
        System.out.println("--------------------");


		/*
		 * add bid entries w.r.t. each active campaign with remaining contracted
		 * impressions.
		 *
		 * for now, a single entry per active campaign is added for queries of
		 * matching target segment.
		 */
		double n_max = 1.2;
        for (Campaign currCampaign : runningCamps) {

            if ((dayBiddingFor >= currCampaign.getDayStart())
                    && (dayBiddingFor <= currCampaign.getDayEnd())
                    && (currCampaign.impsTogo() > 0)) {

                int entCount = 0;
                double ourBid = this.impressionBidder.getImpressionBid(currCampaign, day) * 1000;

                for (AdxQuery query : currCampaign.getCampaignQueries()) {

                /*
                 * Note: bidding per 1000 imps (CPM) - no more than average budget
                 * revenue per imp
                 */

                    if (currCampaign.impsTogo() - entCount > 0) {
					/*
					 * among matching entries with the same campaign id, the AdX\
					 * randomly chooses an entry according to the designated
					 * weight. by setting a constant weight 1, we create a
					 * uniform probability over active campaigns(irrelevant because we are bidding only on one campaign)
					 */
                        if (query.getDevice() == Device.pc) {
                            if (query.getAdType() == AdType.text) {
                                entCount++;
                            } else {
                                entCount += currCampaign.getVideoCoef();
                            }
                        } else {
                            if (query.getAdType() == AdType.text) {
                                entCount += currCampaign.getMobileCoef();
                            } else {
                                entCount += currCampaign.getVideoCoef() + currCampaign.getMobileCoef();
                            }

                        }
                        double query_type_multiplier = impressionBidder.impressionMediumMultiplier(query);
                        bidBundle.addQuery(query, ourBid*query_type_multiplier, new Ad(null),
                                currCampaign.getId(), 1);
                    }
                }

                double impressionLimit = currCampaign.impsTogo();
                double budgetLimit = currCampaign.getBudget();
                bidBundle.setCampaignDailyLimit(currCampaign.getId(), (int) (impressionLimit * n_max), budgetLimit * n_max);

                System.out.println("Day " + day + ": Updated " + entCount
                        + " Bid Bundle entries for Campaign ("+currCampaign.getNiceName()+") id " + currCampaign.getId());
            }
        }

        if (bidBundle != null) {
            if (bidBundle.keys().size() > 0){
                System.out.println("Day " + day + ": Sending BidBundle");
            }else{
                System.out.println("Day " + day + ": Sending Empty BidBundle");
            }
            sendMessage(adxAgentAddress, bidBundle);
        }
        System.out.println("==================[ Bidding Over for tomorrow (day "+tomorrow+") ]================================================================================");
        System.out.println("==================================================================================================================");
        System.out.println("==================================================================================================================");
    }

    /**
     * Campaigns performance w.r.t. each allocated campaign
     */
    private void handleCampaignReport(CampaignReport campaignReport) {

        campaignReports.add(campaignReport);

		/*
		 * for each campaign, the accumulated statistics from day 1 up to day
		 * n-1 are reported
		 */
        for (CampaignReportKey campaignKey : campaignReport.keys()) {
            int cmpId = campaignKey.getCampaignId();
            CampaignStats cstats = campaignReport.getCampaignReportEntry(
                    campaignKey).getCampaignStats();
            myCampaigns.get(cmpId).setStats(cstats);

            System.out.println("Day " + day + ": Updating campaign ("+myCampaigns.get(cmpId).getNiceName()+") " + cmpId + " stats: "
                    + cstats.getTargetedImps() + " tgtImps "
                    + cstats.getOtherImps() + " nonTgtImps. Cost of imps is "
                    + cstats.getCost());
        }
    }

    /**
     * Users and Publishers statistics: popularity and ad type orientation
     */
    private void handleAdxPublisherReport(AdxPublisherReport adxPublisherReport) {
        System.out.println("Publishers Report: ");
        for (PublisherCatalogEntry publisherKey : adxPublisherReport.keys()) {
            AdxPublisherReportEntry entry = adxPublisherReport
                    .getEntry(publisherKey);
            System.out.println(entry.toString());
        }
    }

    /**
     *
     *
     */
    private void handleAdNetworkReport(AdNetworkReport adnetReport) {

        System.out.println("Day " + day + " : AdNetworkReport");
		/*
		 * for (AdNetworkKey adnetKey : adnetReport.keys()) {
		 * 
		 * double rnd = Math.random(); if (rnd > 0.95) { AdNetworkReportEntry
		 * entry = adnetReport .getAdNetworkReportEntry(adnetKey);
		 * System.out.println(adnetKey + " " + entry); } }
		 */
    }

    @Override
    protected void simulationSetup() {
        Random random = new Random();



        day = 0;
        bidBundle = new AdxBidBundle();

		/* initial bid between 0.1 and 0.2 */
        ucsBid = 0.1 + random.nextDouble() / 10.0;

        myCampaigns = new HashMap<Integer, Campaign>();
        log.fine("AdNet " + getName() + " simulationSetup");
    }

    @Override
    protected void simulationFinished() {
        marketMonitor = new MarketMonitor();
        campaignReports.clear();
        bidBundle = null;
        System.out.println("#############################################################################################");
        System.out.println("===========[ Post Game Diagnostics ]=============");
        for (Campaign my_camp : this.myCampaigns.values()){
            System.out.println("----[ Campaign ("+my_camp.getNiceName()+"): "+my_camp.getId()+"]----");
            System.out.println("Length: "+my_camp.getLength());
            System.out.println("Total Budget: £" + my_camp.getBudget() +
                    "\t\tspent: £" + my_camp.getStats().getCost() +
                    "\t\t=" + (100.0 * my_camp.getStats().getCost()/ my_camp.getBudget()) + "% spent    ");
            System.out.println("Total Reach: " + my_camp.getReachImps()
                    + "\t\tachieved: " + Math.floor(my_camp.getStats().getTargetedImps()) +
                    "\t\t=" + (100.0 * Math.floor(my_camp.getStats().getTargetedImps()) / my_camp.getReachImps()) + "%achieved");

        }
    }

    /**
     * A user visit to a publisher's web-site results in an impression
     * opportunity (a query) that is characterized by the the publisher, the
     * market segment the user may belongs to, the device used (mobile or
     * desktop) and the ad type (text or video).
     * <p>
     * An array of all possible queries is generated here, based on the
     * publisher names reported at game initialization in the publishers catalog
     * message
     */
    private void generateAdxQuerySpace() {
        if (publisherCatalog != null && queries == null) {
            Set<AdxQuery> querySet = new HashSet<AdxQuery>();

			/*
			 * for each web site (publisher) we generate all possible variations
			 * of device type, ad type, and user market segment
			 */
            for (PublisherCatalogEntry publisherCatalogEntry : publisherCatalog) {
                String publishersName = publisherCatalogEntry
                        .getPublisherName();
                for (MarketSegment userSegment : MarketSegment.values()) {
                    Set<MarketSegment> singleMarketSegment = new HashSet<MarketSegment>();
                    singleMarketSegment.add(userSegment);

                    querySet.add(new AdxQuery(publishersName,
                            singleMarketSegment, Device.mobile, AdType.text));

                    querySet.add(new AdxQuery(publishersName,
                            singleMarketSegment, Device.pc, AdType.text));

                    querySet.add(new AdxQuery(publishersName,
                            singleMarketSegment, Device.mobile, AdType.video));

                    querySet.add(new AdxQuery(publishersName,
                            singleMarketSegment, Device.pc, AdType.video));

                }

                /**
                 * An empty segments set is used to indicate the "UNKNOWN"
                 * segment such queries are matched when the UCS fails to
                 * recover the user's segments.
                 */
                querySet.add(new AdxQuery(publishersName,
                        new HashSet<MarketSegment>(), Device.mobile,
                        AdType.video));
                querySet.add(new AdxQuery(publishersName,
                        new HashSet<MarketSegment>(), Device.mobile,
                        AdType.text));
                querySet.add(new AdxQuery(publishersName,
                        new HashSet<MarketSegment>(), Device.pc, AdType.video));
                querySet.add(new AdxQuery(publishersName,
                        new HashSet<MarketSegment>(), Device.pc, AdType.text));
            }
            queries = new AdxQuery[querySet.size()];
            querySet.toArray(queries);
        }
    }

    /*genarates an array of the publishers names
     * */
    private void getPublishersNames() {
        if (null == publisherNames && publisherCatalog != null) {
            ArrayList<String> names = new ArrayList<String>();
            for (PublisherCatalogEntry pce : publisherCatalog) {
                names.add(pce.getPublisherName());
            }

            publisherNames = new String[names.size()];
            names.toArray(publisherNames);
        }
    }

    /*
     * genarates the campaign queries relevant for the specific campaign, and assign them as the campaigns campaignQueries field
     */
    private void genCampaignQueries(Campaign campaign) {
        Set<AdxQuery> campaignQueriesSet = new HashSet<AdxQuery>();
        for (String PublisherName : publisherNames) {
            campaignQueriesSet.add(new AdxQuery(PublisherName,
                    campaign.getTargetSegment(), Device.mobile, AdType.text));
            campaignQueriesSet.add(new AdxQuery(PublisherName,
                    campaign.getTargetSegment(), Device.mobile, AdType.video));
            campaignQueriesSet.add(new AdxQuery(PublisherName,
                    campaign.getTargetSegment(), Device.pc, AdType.text));
            campaignQueriesSet.add(new AdxQuery(PublisherName,
                    campaign.getTargetSegment(), Device.pc, AdType.video));
        }

        campaign.setCampaignQueries(new AdxQuery[campaignQueriesSet.size()]);
        campaignQueriesSet.toArray(campaign.getCampaignQueries());


    }

    private char findNiceNameOfCampaign(long id){
        for (Campaign camp : marketMonitor.getAllCampaigns()){
            if (camp.getId() == id){
                return camp.getNiceName();
            }
        }
        return ' ';
    }

}

 /*************************************************************************
 * Ledbetter CONFIDENTIAL
 * __________________
 * 
 * [2018] - [2020] Aaron Ledbetter
 * All Rights Reserved.
 * 
 * NOTICE: All information contained herein is, and remains
 * the property of Aaron Ledbetter. The intellectual and technical 
 * concepts contained herein are proprietary to Aaron Ledbetter and 
 * may be covered by U.S. and Foreign Patents, patents in process, 
 * and are protected by trade secret or copyright law. 
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Aaron Ledbetter.
 */

package main.java.com.sedroApps;


import java.util.List;
import java.util.stream.Collectors;

import twitter4j.DirectMessage;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;


public class ChatTwitter { 

	// this is per user?
	private static TwitterFactory twitterfactory = null;
		
	
	
	ChatTwitter() {
	
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////////
	// EXTERNAL calls: init & processing
	protected void init() {

    	// TWITTER
    	ConfigurationBuilder cb = new ConfigurationBuilder();
    	cb.setDebugEnabled(true);
    	cb.setOAuthConsumerKey("your consumer key");
    	cb.setOAuthConsumerSecret("your consumer secret");
    	cb.setOAuthAccessToken("your access token");
    	cb.setOAuthAccessTokenSecret("your access token secret");
    	twitterfactory = new TwitterFactory(cb.build());
    	
	}
	private Twitter getTwitterinstance(String user) {
		Twitter twitter = twitterfactory.getInstance();
		// FIXME
		return twitter;
	}
	
	public String twitterPostMessage(String user, String msg) throws TwitterException {
	    Twitter twitter = getTwitterinstance(user);
	    Status status = twitter.updateStatus(msg);
	    return status.getText();
	}
	public String twitterSendDirectMessage(String user, String touser, String msg) throws TwitterException {
	    Twitter twitter = getTwitterinstance(user);
	    DirectMessage message = twitter.sendDirectMessage(touser, msg);
	    return message.getText();
	}
	
	public List<String> twitterGetTimeLine(String user) throws TwitterException {
	    Twitter twitter = getTwitterinstance(user);	     
	    return twitter.getHomeTimeline().stream().map(item -> item.getText()).collect(Collectors.toList());
	}

	/*
	public List<String> twitterSearchMessages(String user, String text) throws TwitterException {		  
	    Twitter twitter = getTwitterinstance(user);
	    Query query = new Query("source:twitter4j " + text);
	    QueryResult result = twitter.search(query);
	     
	    return result.getTweets().stream().map(item -> item.getText()).collect(Collectors.toList());
	}*/
	/*
	public static void twitterStream() {
		 
	    StatusListener listener = new StatusListener() {
	 
	        @Override
	        public void onException(Exception e) {
	            e.printStackTrace();
	        }
	        @Override
	        public void onDeletionNotice(StatusDeletionNotice arg) {
	        }
	        @Override
	        public void onScrubGeo(long userId, long upToStatusId) {
	        }
	        @Override
	        public void onStallWarning(StallWarning warning) {
	        }
	        @Override
	        public void onStatus(Status status) {
	        }
	        @Override
	        public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
	        }

	    };
	 
	    TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
	 
	    twitterStream.addListener(listener);
	 
	    twitterStream.sample();
	}*/


}

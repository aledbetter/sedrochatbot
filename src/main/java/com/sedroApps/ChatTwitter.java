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


public class ChatTwitter extends ChatService { 

	// this is per user?
	private static TwitterFactory twitterfactory = null;
		
	
	ChatTwitter() {
	
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////////
	// EXTERNAL calls: init & processing
	@Override
	public int init(UserAccount ua) {
    	ConfigurationBuilder cb = new ConfigurationBuilder();
    	cb.setDebugEnabled(true);
    	cb.setOAuthConsumerKey(ua.consumer_key);
    	cb.setOAuthConsumerSecret(ua.consumer_secret);
    	cb.setOAuthAccessToken(ua.access_token);
    	cb.setOAuthAccessTokenSecret(ua.access_token_secret);
    	twitterfactory = new TwitterFactory(cb.build());
    	return 0;
	}
	
	@Override
	public String postMessage(String msg) {
		try {
			Twitter twitter = getTwitterinstance();
			Status status = twitter.updateStatus(msg);
			return status.getText();
		} catch (Throwable t) { }
		return "ERROR";	
	}

	@Override
	public String sendDirectMessage(String touser, String msg) {
		try {
		    Twitter twitter = getTwitterinstance();
		    DirectMessage message = twitter.sendDirectMessage(touser, msg);
		    return message.getText();
		} catch (Throwable t) { }
		return "ERROR";			
	}
	
	@Override
	public List<String> getTimeLine() {
		try {
		    Twitter twitter = getTwitterinstance();	     
		    return twitter.getHomeTimeline().stream().map(item -> item.getText()).collect(Collectors.toList());
		} catch (Throwable t) { }
		return null;			
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////////
	// internal
	private Twitter getTwitterinstance() {
		Twitter twitter = twitterfactory.getInstance();
		return twitter;
	}
	
	
	/*
	private List<String> twitterSearchMessages(String user, String text) throws TwitterException {		  
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

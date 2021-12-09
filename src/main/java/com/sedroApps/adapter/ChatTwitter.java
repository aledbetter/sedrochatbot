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

package com.sedroApps.adapter;


import java.util.List;
import java.util.stream.Collectors;

import com.sedroApps.SCCall;
import com.sedroApps.SCSedroCall;
import com.sedroApps.SCUser;
import com.sedroApps.util.Sutil;
import twitter4j.DirectMessage;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;


public class ChatTwitter extends ChatAdapter { 

	// this is per user?
	private TwitterFactory factory = null;
	private String pconsumer_key = null;  	// api_key
	private String pconsumer_secret = null; // api_secret
	private String paccess_token = null;
	private String paccess_token_secret = null;
	private String pdopublic = null;
	private String pdoprivate = null;


	public ChatTwitter(SCUser user, String id) {
		super(user, id);
	}
	
	@Override
	public String getName() {
		return "twitter";	
	}
	@Override
	public String getChannel_type() {
		if (isPublicMsg()) return "post";	
		return "post_direct";
	}
	
	@Override
	public boolean isPublicMsg() {
		if (pdoprivate != null) {
			if (pdoprivate.equals("true")) return true;
			return false;
		}
		return true;
	}
	@Override
	public boolean isPrivateMsg() {
		if (pdopublic != null) {
			if (pdopublic.equals("true")) return true;
			return false;
		}
		return true;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////
	// EXTERNAL calls: init & processing
	@Override
	public int init(SCUser ua) {
		super.init(ua);
		String consumer_key = getServiceInfo("consumer_key");
		String consumer_secret = getServiceInfo("consumer_secret");
		String access_token = getServiceInfo("access_token");
		String access_token_secret = getServiceInfo("access_token_secret");
		String idopublic = getServiceInfo("dopublic");
		String idoprivate = getServiceInfo("doprivate");

		if (consumer_key == null || consumer_secret == null || access_token == null || access_token_secret == null) {
			return -1; // not configured... remove
		}
		if (factory != null) {
			// check for updates..
			boolean change = false;
			if (!pconsumer_key.equals(consumer_key)) change = true;
			if (!pconsumer_secret.equals(consumer_secret)) change = true;
			if (!paccess_token.equals(access_token)) change = true;
			if (!paccess_token_secret.equals(access_token_secret)) change = true;
			if (Sutil.compare(idopublic, pdopublic)) change = true;
			if (Sutil.compare(idoprivate, pdoprivate)) change = true;
			if (!change) return 1;
		}
    	ConfigurationBuilder cb = new ConfigurationBuilder();
    	cb.setDebugEnabled(true);
    	cb.setOAuthConsumerKey(consumer_key);
    	cb.setOAuthConsumerSecret(consumer_secret);
    	cb.setOAuthAccessToken(access_token);
    	cb.setOAuthAccessTokenSecret(access_token_secret);
    	factory = new TwitterFactory(cb.build());
    	
    	// retain config
    	pconsumer_key = consumer_key;
    	pconsumer_secret = consumer_secret;
    	paccess_token = access_token;
    	paccess_token_secret = access_token_secret;
    	pdoprivate = idoprivate;
    	pdopublic = idopublic;
    	return 0;
	}
	
	@Override
	public String postMessage(SCCall proc, String msg) {
		try {
			Twitter twitter = getTwitterinstance();
			Status status = twitter.updateStatus(msg);
			return status.getText();
		} catch (Throwable t) { }
		return "ERROR";	
	}

	@Override
	public String sendDirectMessage(SCCall proc, String touser, String msg) {
		try {
		    Twitter twitter = getTwitterinstance();
		    DirectMessage message = twitter.sendDirectMessage(touser, msg);
		    return message.getText();
		} catch (Throwable t) { }
		return "ERROR";			
	}
	
	@Override
	public List<String> getPublicMessages() {
		try {
		    Twitter twitter = getTwitterinstance();	     
		    return twitter.getHomeTimeline().stream().map(item -> item.getText()).collect(Collectors.toList());
		} catch (Throwable t) { }
		return null;			
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////////
	// internal
	private Twitter getTwitterinstance() {
		Twitter twitter = factory.getInstance();
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

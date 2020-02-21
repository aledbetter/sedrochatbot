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


import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;



public class ChatFacebook extends ChatAdapter { 

	// this is per user?
	//private TwitterFactory factory = null;
	private String pconsumer_key = null;  	// api_key
	private String pconsumer_secret = null; // api_secret
	private String paccess_token = null;
	private String paccess_token_secret = null;
	
	ChatFacebook() {
	
	}
	
	@Override
	public String getName() {
		return "facebook";	
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////
	// EXTERNAL calls: init & processing
	@Override
	public int init(UserAccount ua) {
		super.init(ua);
		String consumer_key = ua.getServiceInfo(getName(), "consumer_key");
		String consumer_secret = ua.getServiceInfo(getName(), "consumer_secret");
		String access_token = ua.getServiceInfo(getName(), "access_token");
		String access_token_secret = ua.getServiceInfo(getName(), "access_token_secret");
		if (consumer_key == null || consumer_secret == null || access_token == null || access_token_secret == null) {
			return -1; // not configured... remove
		}
		/*
		if (factory != null) {
			// check for updates..
			boolean change = false;
			if (!pconsumer_key.equals(consumer_key)) change = true;
			if (!pconsumer_secret.equals(consumer_secret)) change = true;
			if (!paccess_token.equals(access_token)) change = true;
			if (!paccess_token_secret.equals(access_token_secret)) change = true;
			if (!change) return 0;
		}
    	ConfigurationBuilder cb = new ConfigurationBuilder();
    	cb.setDebugEnabled(true);
    	cb.setOAuthConsumerKey(consumer_key);
    	cb.setOAuthConsumerSecret(consumer_secret);
    	cb.setOAuthAccessToken(access_token);
    	cb.setOAuthAccessTokenSecret(access_token_secret);
    	factory = new TwitterFactory(cb.build());
    	*/
    	// retain config
    	pconsumer_key = consumer_key;
    	pconsumer_secret = consumer_secret;
    	paccess_token = access_token;
    	paccess_token_secret = access_token_secret;
		session_per_direct = true;

    	return 0;
	}
	
	@Override
	public String postMessage(Sedro proc, String msg) {
		try {
		
		} catch (Throwable t) { }
		return "ERROR";	
	}

	@Override
	public String sendDirectMessage(Sedro proc, String touser, String msg) {
		try {

		} catch (Throwable t) { }
		return "ERROR";			
	}
	
	@Override
	public List<String> getPublicMessages() {
		try {

		} catch (Throwable t) { }
		return null;			
	}
	
	// list of messages: from:from user / msg:message text
	@Override	
	public List<HashMap<String, String>> getDirectMessages(Orator orat, Sedro processor) {
		return null;
	}


}

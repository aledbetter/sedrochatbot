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


import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import com.sedroApps.SCCall;
import com.sedroApps.SCOrator;
import com.sedroApps.SCSedroCall;
import com.sedroApps.SCUser;
import com.sedroApps.util.Sutil;



public class ChatWhatsapp extends ChatAdapter { 
/*
 * https://www.twilio.com/docs/sms/whatsapp/quickstart/java
 * https://whatsmate.github.io/2016-02-17-send-whatsapp-message-java/
 */
	// this is per user?
	//private TwitterFactory factory = null;
	private String pconsumer_key = null;  	// api_key
	private String pconsumer_secret = null; // api_secret
	private String paccess_token = null;
	private String paccess_token_secret = null;
	
	public ChatWhatsapp(SCUser user, String id) {
		super(user, id);
	}
	
	@Override
	public String getName() {
		return "whatsapp";	
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
    	return 0;
	}
	
	@Override
	public String postMessage(SCCall proc, String msg) {
		try {
		
		} catch (Throwable t) { }
		return "ERROR";	
	}

	@Override
	public String sendDirectMessage(SCCall proc, String touser, String msg) {
		try {

		} catch (Throwable t) { }
		return "ERROR";			
	}

	
	// list of messages: from:from user / msg:message text
	@Override	
	public List<HashMap<String, String>> getDirectMessages(SCOrator orat, SCCall processor) {
		return null;
	}


}

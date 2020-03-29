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

package main.java.com.sedroApps.adapter;


import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONObject;

import main.java.com.sedroApps.SCOrator;
import main.java.com.sedroApps.SCSedroCall;
import main.java.com.sedroApps.SCUser;
import main.java.com.sedroApps.util.Sutil;



public class ChatWebRest extends ChatAdapter { 
	// this is per user?
	private String papi_key = null;  	// api_key
	
	public ChatWebRest(SCUser user, String id) {
		super(user, id);
	}
	
	@Override
	public String getName() {
		return "webchat";	
	}
	@Override
	public String getChannel_type() {
		return "chat";	
	}	
	@Override
	public boolean isPublicMsg() {
		return false;
	}
	@Override
	public boolean isPrivateMsg() {
		return true;
	}
	@Override
	public boolean isPolled() {
		return false;
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////////
	// EXTERNAL calls: init & processing
	@Override
	public int init(SCUser ua) {
		super.init(ua);
		String api_key = getServiceInfo("api_key");
		if (api_key == null) {
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
		papi_key = api_key;
    	return 0;
	}
	
	@Override
	public String postMessage(SCSedroCall proc, String msg) {
		try {
		
		} catch (Throwable t) { }
		return "ERROR";	
	}

	@Override
	public String sendDirectMessage(SCSedroCall proc, String touser, String msg) {
		try {

		} catch (Throwable t) { }
		return "ERROR";			
	}

	
	// list of messages: from:from user / msg:message text
	@Override	
	public List<HashMap<String, String>> getDirectMessages(SCOrator orat, SCSedroCall processor) {
		return null;
	}

	
	//////////////////////////////////////////////////
	// Callback for direct recieve and handle from REST APIs
	
	// callback for receive (when deployed with public IP only)
	@Override	
	public List<String> getReceiveMessages(String data) {
	/*
		try {
			JSONObject obj = new JSONObject(data);
			HashMap<String, String> msg = parseTwillioMessage(obj);
			this.getOrator().processMessage(msg);
		} catch (Throwable t) {
			t.printStackTrace();
		}	*/
		return null;
	}
	
	// call back for new incomming calls 
	@Override	
	public HashMap<String, String> getReceiveCall() {
		return null;
	}
}

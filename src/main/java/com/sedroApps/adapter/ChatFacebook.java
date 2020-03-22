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

import com.github.messenger4j.Messenger;

import main.java.com.sedroApps.SCOrator;
import main.java.com.sedroApps.SCSedroCall;
import main.java.com.sedroApps.SCUser;
import main.java.com.sedroApps.util.Sutil;


/*
Facebook requires a public Domain SSL and a publicly signed cert in order to
interact. 
All interaction is via a registered webhook (url).
 
 
This library may help with some work:
https://github.com/messenger4j/messenger4j

 */
public class ChatFacebook extends ChatAdapter { 

	// this is per user?
	private Messenger factory = null;
	private String papp_secret = null;  	// secret
	private String pverify_token = null; // api_secret
	private String paccess_token = null;
	private String pweb_hook_url = null;
	private String pdopublic = null;
	private String pdoprivate = null;
	
	public ChatFacebook(SCUser user, String id) {
		super(user, id);
	}
	
	@Override
	public String getName() {
		return "facebook";	
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
		String app_secret = getServiceInfo("app_secret");
		String verify_token = getServiceInfo("verify_token");
		String access_token = getServiceInfo("access_token");
		String web_hook_url = getServiceInfo("web_hook_url");
		
		String idopublic = getServiceInfo("dopublic");
		String idoprivate = getServiceInfo("doprivate");

		if (app_secret == null || verify_token == null || access_token == null) {
			return -1; // not configured... remove
		}
		
		if (factory != null) {
			// check for updates..
			boolean change = false;
			if (!papp_secret.equals(app_secret)) change = true;
			if (!pverify_token.equals(verify_token)) change = true;
			if (!paccess_token.equals(access_token)) change = true;
			if (!pweb_hook_url.equals(web_hook_url)) change = true;
			if (Sutil.compare(idopublic, pdopublic)) change = true;
			if (Sutil.compare(idoprivate, pdoprivate)) change = true;
			if (!change) return 0;
		}
		// set it... 
		factory = Messenger.create(access_token, app_secret, verify_token);

    	// retain config
    	papp_secret = app_secret;
    	pverify_token = verify_token;
    	paccess_token = access_token;
    	pweb_hook_url = web_hook_url;
    	pdoprivate = idoprivate;
    	pdopublic = idopublic;
    	
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
	
	@Override
	public List<String> getPublicMessages() {
		try {

		} catch (Throwable t) { }
		return null;			
	}
	
	// list of messages: from:from user / msg:message text
	@Override	
	public List<HashMap<String, String>> getDirectMessages(SCOrator orat, SCSedroCall processor) {
		return null;
	}


}

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

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import main.java.com.sedroApps.util.Sutil;



public class ChatSMS extends ChatAdapter { 
/*
 * bandwidth.com
 * https://dev.bandwidth.com/numbers/about.html
 * https://dev.bandwidth.com/messaging/about.html
 * https://dev.bandwidth.com/sdks/java.html
<dependency>
    <groupId>com.bandwidth.sdk</groupId>
    <artifactId>bandwidth-sdk</artifactId>
    <version>1.0.0</version>
</dependency>

 */
	// this is per user?
	//private TwitterFactory factory = null;
	private String pprovider = null;  	// twillio / bandwidth.com / etc
	private String paccount_sid = null;  	// api_key
	private String pauth_token = null; // api_secret
	private boolean init = false;
	
	ChatSMS() {
		
	}
	
	public String getName() {
		return "SMS";	
	}
	public boolean isProvider(String provider) {
		return Sutil.compare(provider, pprovider);
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////
	// EXTERNAL calls: init & processing
	@Override
	public int init(UserAccount ua) {
		super.init(ua);
	 
		String provider = ua.getServiceInfo(getName(), "provider");
		String account_sid = ua.getServiceInfo(getName(), "account_sid");
		String auth_token = ua.getServiceInfo(getName(), "auth_token");
		if (auth_token == null || account_sid == null || provider == null) {
			return -1; // not configured... remove
		}
		if (init) {
			boolean change = false;
			if (!pprovider.equals(provider)) change = true;
			if (!paccount_sid.equals(account_sid)) change = true;
			if (!pauth_token.equals(auth_token)) change = true;
			if (!change) return 0;

		}

    	// retain config
    	pprovider = provider;
    	paccount_sid = account_sid;
    	pauth_token = auth_token;
    	
		if (isProvider("twillio")) {
			// twillio init
			Twilio.init(account_sid, auth_token);
			init = true;
		}
		
		return 0;
	}
	
	@Override
	public String postMessage(String msg) {
		try {
		
		} catch (Throwable t) { }
		return "ERROR";	
	}

	@Override
	public String sendDirectMessage(String touser, String msg) {
		try {
			if (isProvider("twillio")) {

			Message message = Message.creator(new PhoneNumber("+15558675309"),
			        new PhoneNumber("+15017250604"), 
			        "This is the ship that made the Kessel Run in fourteen parsecs?").create();

			    System.out.println(message.getSid());
			}
		} catch (Throwable t) { }
		return "ERROR";			
	}
	

	
	// list of messages: from:from user / msg:message text
	@Override	
	public List<HashMap<String, String>> getDirectMessages() {
		
		// FIXME some may be new calls
		
		return null;
	}
	
	// call back for new incomming calls 
	@Override	
	public HashMap<String, String> getReceiveCall() {
		
		return null;
	}
	
	/*
	    public static void main(String[] args) {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        Message message = Message.fetcher("MMXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX")
            .fetch();

        System.out.println(message.getTo());
    }
	 */

	//https://api.twilio.com/2010-04-01/Accounts/{AccountSid}/Messages.json
	
	// SMS Webhook
	/*
	public class Example {
	    // Find your Account Sid and Token at twilio.com/console
	    // DANGER! This is insecure. See http://twil.io/secure
	    public static final String ACCOUNT_SID = "ACXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX";
	    public static final String AUTH_TOKEN = "your_auth_token";

	    public static void main(String[] args) {
	        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
	        Message message = Message.creator(
	                new com.twilio.type.PhoneNumber("+15558675310"),
	                new com.twilio.type.PhoneNumber("+15017122661"),
	                "McAvoy or Stewart? These timelines can get so confusing.")
	            .setStatusCallback(URI.create("http://postb.in/1234abcd"))
	            .create();

	        System.out.println(message.getSid());
	    }
	}
	*/
	
/*
{
  "account_sid": "ACXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX",
  "api_version": "2010-04-01",
  "body": "testing",
  "date_created": "Fri, 24 May 2019 17:18:27 +0000",
  "date_sent": "Fri, 24 May 2019 17:18:28 +0000",
  "date_updated": "Fri, 24 May 2019 17:18:28 +0000",
  "direction": "outbound-api",
  "error_code": 30007,
  "error_message": "Carrier violation",
  "from": "+12019235161",
  "messaging_service_sid": "MGXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX",
  "num_media": "0",
  "num_segments": "1",
  "price": "-0.00750",
  "price_unit": "USD",
  "sid": "MMXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX",
  "status": "sent",
  "subresource_uris": {
    "media": "/2010-04-01/Accounts/ACXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX/Messages/SMb7c0a2ce80504485a6f653a7110836f5/Media.json",
    "feedback": "/2010-04-01/Accounts/ACXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX/Messages/SMb7c0a2ce80504485a6f653a7110836f5/Feedback.json"
  },
  "to": "+18182008801",
  "uri": "/2010-04-01/Accounts/ACXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX/Messages/SMb7c0a2ce80504485a6f653a7110836f5.json"
}
 */

}

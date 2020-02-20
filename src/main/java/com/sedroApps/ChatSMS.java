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


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.collect.Range;
import com.twilio.Twilio;
import com.twilio.base.ResourceSet;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;


import main.java.com.sedroApps.util.Sutil;



public class ChatSMS extends ChatAdapter { 
	private static final String datefmt = "EEE, dd MMM yyyy HH:mm:ss Z";

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
	private DateTime last_msg_check_time = null;
	private DateTime last_call_check_time = null;
	
	// FIXME limit phone numbers ?
	
	ChatSMS() {
		
	}
	
	@Override
	public String getName() {
		return "SMS";	
	}
	@Override
	public String getChannel_type() {
		return "sms";	
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
	public String postMessage(Sedro proc, String msg) {
		try {
		
		} catch (Throwable t) { }
		return "ERROR";	
	}
	
	private String makePhoneNumber(String phone_number) {
		// FIXME better correction needed
		return "+"+phone_number;
	}
	
	@Override
	public String sendDirectMessage(Sedro proc, String touser, String msg) {
		try {
			if (isProvider("twillio")) {
				String caller_phone = proc.getCaller_handle();
				if (touser != null) caller_phone = touser;
				String persona_phone = proc.getPersona_handle();
				
				Message message = Message.creator(new PhoneNumber(makePhoneNumber(caller_phone)),
			        new PhoneNumber(makePhoneNumber(persona_phone)), msg).create();

			    System.out.println("SENT: " + message.getSid());
			}
		} catch (Throwable t) { }
		return "ERROR";			
	}
	
	
	/// fetch a single message for account... why?
	//Message message = Message.fetcher(message_id).fetch();
	//System.out.println(message.getTo());
	// delete
    //Message.deleter("MMXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX").delete();
   
	
	//////////////////////////////////////////////////
	// Polling handlers	
	// list of messages: from:from user / msg:message text
	//https://api.twilio.com/2010-04-01/Accounts/{AccountSid}/Messages.json
	@Override	
	public List<HashMap<String, String>> getDirectMessages(Orator orat) {		
		ResourceSet<Message> messages = null;
		//new DateTime(2019, 3, 1, 0, 0, 0)
		if (last_msg_check_time != null) {
			messages = Message.reader().setDateSent(Range.greaterThan(last_msg_check_time)).read();
		} else {
			messages = Message.reader().read();
		}
		List<HashMap<String, String>> dl = parseTwillioMessages(orat, messages, false);
		last_msg_check_time = new DateTime();
		return dl;
	}

	

	// get new calls
	@Override	
	public List<HashMap<String, String>> getDirectCall(Orator orat) {		
		ResourceSet<Message> messages = null;
		//new DateTime(2019, 3, 1, 0, 0, 0)
		if (last_call_check_time != null) {
			messages = Message.reader().setDateSent(Range.greaterThan(last_call_check_time)).read();
		} else {
			messages = Message.reader().read();
		}		
		List<HashMap<String, String>> dl = parseTwillioMessages(orat, messages, true);
		last_call_check_time = new DateTime();
		return dl;
	}

	private static List<HashMap<String, String>> parseTwillioMessages(Orator orat, ResourceSet<Message> messages, boolean newcalls) {
		List<HashMap<String, String>> msgList = null;
		if (messages == null) return null;
		
		for(Message record : messages) {
			System.out.println(record.getSid());
			if (!"inboud".equals(""+record.getDirection())) continue;
			if (!"received".equals(""+record.getStatus())) continue;
			String from = record.getFrom().toString();
			Sedro proc = orat.findProcessor(from);
			if (newcalls) {						
				if (proc != null) continue; // check for new calls only							
			} else {
				if (proc == null) continue;			
				// is this a new message ?			
			}		
			HashMap<String, String> mm = new HashMap<>();
			
			mm.put("id", record.getSid());
			mm.put("from", from);

			mm.put("to", record.getTo());
			mm.put("status", ""+record.getStatus());
			mm.put("error", ""+record.getErrorCode());
			mm.put("msg", record.getBody());
			mm.put("date_created", ""+record.getDateCreated().toString(datefmt));
						
			mm.put("caller_handle", from);
			mm.put("caller", from);
		//	mm.put("caller_token", status);
			System.out.println(" MSG txt: " + mm.get("msg"));

			if (msgList == null) msgList = new ArrayList<>();
			msgList.add(mm);
		}
		
		return msgList;
	}
	
	private static List<HashMap<String, String>> parseTwillioMessages(Orator orat, String msgs, boolean newcalls) {
		List<HashMap<String, String>> msgList = null;
		
		try {
			JSONObject obj = new JSONObject(msgs);
			try {
				JSONArray list = obj.getJSONArray("messages");
				if (list != null && list.length() > 0) {

					for (int i=0;i<list.length();i++) {
						// the messages .... 
						JSONObject msg = list.getJSONObject(i);
						// only inbound
						String direction = msg.getString("direction");
						if (!direction.equals("inbound")) continue;
						
						// only recieved
						String status = msg.getString("status");
						if (!status.equals("received")) continue;
						// FIXME limit phone numbers ?

						
						// find session...					
						String from = msg.getString("from");
						Sedro proc = orat.findProcessor(from);
						if (newcalls) {						
							if (proc != null) continue; // check for new calls only							
						} else {
							if (proc == null) continue;			
							// is this a new message ?
	//FIXME					
						}
												
						HashMap<String, String> mm = new HashMap<>();
						String asid = msg.getString("account_sid");
						String msid = msg.getString("sid");
						String to = msg.getString("to");
						String body = msg.getString("body");

						String date_created = msg.getString("date_created");

						String error_code = msg.getString("error_code");
						String num_segments = msg.getString("num_segments");
						String num_media = msg.getString("num_media");
						
						mm.put("id", msid);
						mm.put("from", from);
						mm.put("date_created", date_created);

						mm.put("to", to);
						mm.put("status", status);
						mm.put("error", error_code);
						mm.put("msg", body);
												
						mm.put("caller_handle", from);
						mm.put("caller", from);
					//	mm.put("caller_token", status);

						
						/*
					     "account_sid": "ACXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX",
					      "api_version": "2010-04-01",
					      "body": "look mom I have media!",
					      "date_created": "Fri, 24 May 2019 17:44:46 +0000",
					      "date_sent": "Fri, 24 May 2019 17:44:49 +0000",
					      "date_updated": "Fri, 24 May 2019 17:44:49 +0000",
					      "direction": "inbound",
					      "error_code": 30004,
					      "error_message": "Message blocked",
					      "from": "+12019235161",
					      "messaging_service_sid": null,
					      "num_media": "3",
					      "num_segments": "1",
					      "price": "-0.00750",
					      "price_unit": "USD",
					      "sid": "MMc26223853f8c46b4ab7dfaa6abba0a26",
					      "status": "received",
					      "subresource_uris": {
					        "media": "/2010-04-01/Accounts/ACXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX/Messages/MMc26223853f8c46b4ab7dfaa6abba0a26/Media.json",
					        "feedback": "/2010-04-01/Accounts/ACXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX/Messages/MMc26223853f8c46b4ab7dfaa6abba0a26/Feedback.json"
					      },
					      "to": "+18182008801",
					      "uri": "/2010-04-01/Accounts/ACXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX/Messages/MMc26223853f8c46b4ab7dfaa6abba0a26.json"
						 */
						
						
						
						System.out.println(" MSG["+i+"] txt: " + mm.get("msg"));

						if (msgList == null) msgList = new ArrayList<>();
						msgList.add(mm);
					}
				}
			} catch (Throwable t) {}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return msgList;
	}
	

	
	
	
	
	//////////////////////////////////////////////////
	// Callback for direct recieve and handle
	//https://www.twilio.com/docs/usage/webhooks/sms-webhooks
	
	// callback for receive (when deployed with public IP only)
	@Override	
	public List<String> getReceiveMessages(String msg) {
		return null;
	}
	
	// call back for new incomming calls 
	@Override	
	public HashMap<String, String> getReceiveCall() {
		return null;
	}

	
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

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
	private static final int DEF_PAST = 4; // 4 days
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
	private String pphone_number = null; // number
	private boolean init = false;
	
	// message cache for polling mode
	private DateTime last_msg_check_time = null;
	private List<HashMap<String, String>> msg_set = null;

	// FIXME limit phone numbers ?
	
	ChatSMS() {
		
	}
	
	@Override
	public String getName() {
		return "sms";	
	}
	@Override
	public String getChannel_type() {
		return "chat";	
		//return "sms";	
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
		String phone_number = ua.getServiceInfo(getName(), "phone_number");
		if (auth_token == null || account_sid == null || provider == null) {
			return -1; // not configured... remove
		}
		phone_number = makePhoneNumber(phone_number);
		if (init) {
			boolean change = false;
			if (!pprovider.equals(provider)) change = true;
			if (!paccount_sid.equals(account_sid)) change = true;
			if (!pauth_token.equals(auth_token)) change = true;
			if (!pphone_number.equals(phone_number)) change = true;
			if (!change) return 1;

		}

    	// retain config
    	pprovider = provider;
    	paccount_sid = account_sid;
    	pauth_token = auth_token;
    	pphone_number = phone_number;
    	
		if (isProvider("twilio")) {
			//System.out.println("  TWI["+account_sid+"] " + auth_token);
			// twilio init
			Twilio.init(account_sid, auth_token);
			init = true;
		}
		session_per_direct = true;
		
		return 0;
	}
	
	private String makePhoneNumber(String phone_number) {
		// FIXME better correction needed
		if (phone_number.startsWith("+")) return phone_number;
		phone_number = phone_number.replaceAll("\\D+","");
		if (phone_number.length() == 10) return "+1"+phone_number; 
		return "+"+phone_number;
	}
	
	@Override
	public String sendDirectMessage(Sedro proc, String touser, String msg) {
		try {
			if (isProvider("twilio")) {
				String caller_phone = makePhoneNumber(proc.getCaller_handle());
				if (touser != null) caller_phone = touser;
							    
				System.out.println("sendDirectMessage["+this.pphone_number+" -> " + caller_phone + "]  => " + msg);

	//			Message message = Message.creator(new PhoneNumber(caller_phone), new PhoneNumber(this.pphone_number), msg).create();
	//		    System.out.println("SMS_SENT: " + message.getSid());
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
	public List<HashMap<String, String>> getDirectCall(Orator orat) {	
		List<HashMap<String, String>> ml = getMessages(orat);
		if (ml == null || ml.size() < 1) return null;
		List<HashMap<String, String>> cl = null;
		for (HashMap<String, String> msg:ml) {
			// find session...					
			String from = msg.get("from");
			Sedro proc = orat.findProcessor(from);					
			if (proc != null) continue; // check for new calls only	
			if (cl != null) {
				// check if accounted for
				boolean dup = false;
				for (HashMap<String, String> m:cl) {
					String tf = m.get("from");
					if (tf.equals(from)) {
						dup = true;
						break;
					}
				}
				if (dup) continue;
			}
			if (cl == null) cl = new ArrayList<>();
			cl.add(msg);
			//System.out.println("   NEW["+cl.size()+"]: " +from);			
		}
		return cl;
	}

	

	// get new calls
	@Override	
	public List<HashMap<String, String>> getDirectMessages(Orator orat, Sedro processor) {	
		List<HashMap<String, String>> ml = getMessages(orat);
		if (ml == null || ml.size() < 1) return null;	
		
		List<HashMap<String, String>> cl = null;
		for (HashMap<String, String> msg:ml) {
			String caller_handle = msg.get("caller_handle");		
			if (!caller_handle.equals(processor.getCaller_handle())) continue;
			
			if (cl == null) cl = new ArrayList<>();
			cl.add(msg);
		}
		
		return cl;
	}
	
	@Override	
	public void clearCache() {
		msg_set = null;
	}

	// get and cache the messages
	private List<HashMap<String, String>> getMessages(Orator orat) {		
		// check if use cache
		if (msg_set != null) return msg_set;
		
		ResourceSet<Message> messages = null;
		if (last_msg_check_time == null) {
			last_msg_check_time = new DateTime().minusDays(DEF_PAST);
		}	

		if (pphone_number != null) {
			messages = Message.reader().setTo(pphone_number).setDateSent(Range.greaterThan(last_msg_check_time)).read();
		} else {
			//System.out.println("   DT_S_S["+last_msg_check_time.toString(datefmt)+"]");
			messages = Message.reader().setDateSent(Range.greaterThan(last_msg_check_time)).read();
		}
		List<HashMap<String, String>> dl = parseTwillioMessages(orat, messages);
		last_msg_check_time = new DateTime();
		// FIXME not matching correct offset
		int xx = last_msg_check_time.getZone().getOffsetFromLocal(last_msg_check_time.getMillis());
		//last_msg_check_time.withZone(newZone)
		last_msg_check_time = last_msg_check_time.minusMillis(xx);

		//System.out.println("   DT_E_E["+last_msg_check_time.toString(datefmt)+"] ["+xx+"]");
		msg_set = dl;
		if (msg_set == null) msg_set = new ArrayList<>();
		return dl;
	}
	
	private static List<HashMap<String, String>> parseTwillioMessages(Orator orat, ResourceSet<Message> messages) {
		List<HashMap<String, String>> msgList = null;
		if (messages == null) return null;
		
		//System.out.println("  SMS_GDC["+messages.getPageSize()+"]: count " + messages.getPageSize());		

		int cnt = 0;
		for(Message record : messages) {
			cnt++;
			if (!"inbound".equals(""+record.getDirection())) continue;
			if (!"received".equals(""+record.getStatus())) continue;

			String from = record.getFrom().toString();
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
	//		System.out.println(" MSG txt: " + mm.get("msg"));

			if (msgList == null) msgList = new ArrayList<>();
			msgList.add(0, mm);
		}
		//System.out.println("   TOTAL["+cnt+"]");
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
	
						
				//		System.out.println(" MSG["+i+"] txt: " + mm.get("msg"));

						if (msgList == null) msgList = new ArrayList<>();
						msgList.add(0, mm);
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

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


import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.joda.time.DateTime;

import com.google.common.collect.Range;
import com.twilio.Twilio;
import com.twilio.base.ResourceSet;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.api.v2010.account.IncomingPhoneNumber;
import com.twilio.rest.api.v2010.account.IncomingPhoneNumberUpdater;
import com.twilio.type.PhoneNumber;



import main.java.com.sedroApps.util.Sutil;



public class ChatSMS extends ChatAdapter { 
	private static final boolean no_send = false;
	
	private static final String datefmt = "EEE, dd MMM yyyy HH:mm:ss Z";
	private static final int DEF_PAST = 2; // 2 days

	// this is per user?
	private String pprovider = null;  	// twillio / bandwidth.com / etc
	private String paccount_sid = null;  	// api_key
	private String pauth_token = null; // api_secret
	private String pphone_number = null; // number
	private boolean init = false;
	
	private String psms_callback_url = null;

	// message cache for polling mode
	private DateTime last_msg_check_time = null;
	private List<HashMap<String, String>> msg_set = null;

	
	public ChatSMS(UserAccount user, String id) {
		super(user, id);
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

		String provider = getServiceInfo("provider");
		String account_sid = getServiceInfo("account_sid");
		String auth_token = getServiceInfo("auth_token");
		String phone_number = getServiceInfo("phone_number");
		String sms_callback_url = getServiceInfo("sms_callback_url");
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
			if (!Sutil.compare(psms_callback_url, phone_number)) change = true;			
			if (!change) return 1;

		}

    	// retain config
    	pprovider = provider;
    	paccount_sid = account_sid;
    	pauth_token = auth_token;
    	pphone_number = phone_number;
    	psms_callback_url = sms_callback_url;
    	
		if (isProvider("twilio")) {
			//System.out.println("  TWI["+account_sid+"] " + auth_token);
			// twilio init
			Twilio.init(account_sid, auth_token);
			init = true;
			if (psms_callback_url != null) {
				// set the callback
				setPhoneNumberSMSCb(pphone_number, psms_callback_url);
			}
		}
		session_per_direct = true;
		
		return 0;
	}
	
	// update the SMS callback for this here phone number
	private void setPhoneNumberSMSCb(String phone_number, String cb_sms_url) {
        IncomingPhoneNumber incomingPhoneNumber = IncomingPhoneNumber.creator(
                new com.twilio.type.PhoneNumber(phone_number)).create();
        System.out.println(" SMS URL: " + incomingPhoneNumber.getSmsUrl());
		try {
			URI smsCb = new URI(cb_sms_url);
	        if (smsCb.equals(incomingPhoneNumber.getSmsUrl())) return;
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return;
		}
		// get updater
		IncomingPhoneNumberUpdater pu = IncomingPhoneNumber.updater(incomingPhoneNumber.getSid());
		pu.setSmsUrl(cb_sms_url).update();
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
				if (!no_send) {
					Message message = Message.creator(new PhoneNumber(caller_phone), new PhoneNumber(this.pphone_number), msg).create();
					System.out.println("SMS_SENT: " + message.getSid());
				}
			}
		} catch (Throwable t) { }
		return "ERROR";			
	}
	
	// slow... so maybe not worth it.
	private void deleteSMS(ArrayList<String> msgids) {
		
		/// fetch a single message for account... why?
		//Message message = Message.fetcher(message_id).fetch();
		//System.out.println(message.getTo());
		// delete
	    //Message.deleter("MMXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX").delete();
	   
	}
	


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
			String ms = getServiceState("msg_check_time");
			if (ms != null) {
				long milli = Sutil.toLong(ms);
				if (milli > 100) last_msg_check_time = new DateTime(milli);
			}
			if (last_msg_check_time == null) {
				last_msg_check_time = new DateTime().minusDays(DEF_PAST);
			}
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
		setServiceState("msg_check_time", ""+last_msg_check_time.getMillis());
		
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
	        Message message = Message.creator(
	                new com.twilio.type.PhoneNumber("+15558675310"),
	                new com.twilio.type.PhoneNumber("+15017122661"),
	                "McAvoy or Stewart? These timelines can get so confusing.")
	            .setStatusCallback(URI.create("http://postb.in/1234abcd"))
	            .create();
	
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
	
	*/
	
}

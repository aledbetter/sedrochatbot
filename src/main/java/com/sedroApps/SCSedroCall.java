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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import main.java.com.sedroApps.adapter.ChatAdapter;
import main.java.com.sedroApps.util.HttpUtil;
import main.java.com.sedroApps.util.Sutil;


public class SCSedroCall extends SCCall {

	public SCSedroCall(ChatAdapter adapter, boolean readPublic, boolean respPublic, boolean directMsg) {
		super(adapter, readPublic, respPublic, directMsg);
	}
	
	private static String getAPIHost() {
		return SCServer.getChatServer().getSedro_hostname();
	}
	private static String getUrl(String ending) {
		return SCServer.getChatServer().getSedro_host()+ending;
	}

	
	// get current personas
	public static List<String> getPersonas(String key) {
		String url = getUrl("/tenant/personas");
				
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("x-rapidapi-key", key);
		headers.put("x-rapidapi-host", getAPIHost());
		headers.put("Accept", "application/json");
		String line = HttpUtil.getURLContent(url, headers);
		if (line == null) return null;
		
		//System.out.println("LINE_WORD: " + line);
		try {
			JSONObject obj = new JSONObject(line);
			JSONArray list = obj.getJSONArray("list");
			if (list != null && list.length() > 0) {
				List<String> pl = new ArrayList<>();
				for (int i=0;i<list.length();i++) {
					pl.add(list.getString(i));
				}
				return pl;
			}
		} catch (Throwable tt) {}		
		return null;
	}

	@Override
	public List<HashMap<String, Object>> chatWakeMessages(String key, String text) {
		HashMap<String, Object> res =  chatWake(key, text);
		if (res == null) return null;
		List<HashMap<String, Object>> msgl = (List<HashMap<String, Object>>)res.get("messages");
		return msgl;
	}
	@Override
	public HashMap<String, Object> chatWake(String key, String text) {
		if (!isStatus("wake") && !isStatus("bye")) return null;
		String url = getUrl("/persona/chat/wake");
		this.key = key;
		
		String reqData = "{\"event\": \"wake\""; 		
		reqData += ", \"persona\": \"" + persona  + "\""; 
		if (text != null) reqData += ", \"text\": \"" + escape(text) + "\""; 
		if (this.getCaller() != null) reqData += ", \"user\": \"" + escape(this.getCaller()) + "\""; 
		if (this.getCall_count() != 0) reqData += ", \"call_count\": \"" + this.getCall_count() + "\""; 
	    if (this.getCaller_token() != null) reqData += ", \"caller_token\": \"" + this.getCaller_token() + "\""; 
	    if (context != null) reqData += ", \"context\": \"" + context  + "\""; 
	    if (this.getLanguage() != null) reqData += ", \"language\": \"" + this.getLanguage()  + "\""; 
	    if (this.getChannel_type() != null) reqData += ", \"channel_type\": \"" + this.getChannel_type()  + "\""; 
	 //   if (save) ind += ", \"save\": \"" + save + "\"";  
	    if (this.getMax_qn() >= 0) reqData += ", \"max_qn\": \"" + this.getMax_qn() + "\"";
	    if (latitude > 0) reqData += ", \"latitude\": \"" + latitude + "\"";
	    if (longitude > 0) reqData += ", \"longitude\": \"" + longitude + "\"";
	    if (location != null) reqData += ", \"location\": \"" + location + "\"";
	    if (calltime != null) reqData += ", \"calltime\": \"" + calltime + "\"";
	    if (tzoffset >= 0) reqData += ", \"tz\": \"" + tzoffset + "\"";
	    if (tzn != null) reqData += ", \"tzn\": \"" + tzn + "\"";	    
		reqData += "}";
		
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("x-rapidapi-key", key);
		headers.put("x-rapidapi-host", getAPIHost());
		headers.put("Accept", "application/json");

		String line = HttpUtil.postDataHttpsJson(url, reqData, null, null, null, headers);
		//System.out.println(line);
		HashMap<String, Object> res = chatRespParse(line, true);
		return res;
	}
	
	@Override
	public List<HashMap<String, Object>> chatPollMessages() {
		HashMap<String, Object> res =  chatPoll();
		if (res == null) return null;
		List<HashMap<String, Object>> msgl = (List<HashMap<String, Object>>)res.get("messages");
		return msgl;
	}
	@Override
	public HashMap<String, Object> chatPoll() {
		if (isStatus("wake") || isStatus("bye")) return null;
		//System.out.println(" **CHAT_POLL: " + this.chid);

		String url = getUrl("/persona/chat/poll");
		String reqData = "{ \"chid\": \"" + this.chid  + "\", \"event\": \"poll\"}";
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("x-rapidapi-key", this.key);
		headers.put("x-rapidapi-host", getAPIHost());
		headers.put("Accept", "application/json");

		String line = HttpUtil.postDataHttpsJson(url, reqData, null, null, null, headers);
		HashMap<String, Object> res = chatRespParse(line, true);
		return res;
	}
	
	@Override
	public List<HashMap<String, Object>> chatMsgMessages(String text) {
		HashMap<String, Object> res =  chatMsg(text);
		if (res == null) return null;
		List<HashMap<String, Object>> msgl = (List<HashMap<String, Object>>)res.get("messages");
		return msgl;
	}
	@Override
	public HashMap<String, Object> chatMsg(String text) {
		if (isStatus("wake") || isStatus("bye")) return null;
		//System.out.println(" **CHAT_MSG: " + this.chid);

		String url = getUrl("/persona/chat/msg");
		String reqData = "{ \"text\": \"";
	    if (text != null) reqData += escape(text);
	    else reqData += " ";
	    reqData += "\", \"chid\": \"" + this.chid  + "\"}";
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("x-rapidapi-key", this.key);
		headers.put("x-rapidapi-host", getAPIHost());
		headers.put("Accept", "application/json");

		String line = HttpUtil.postDataHttpsJson(url, reqData, null, null, null, headers);
		HashMap<String, Object> res = chatRespParse(line, true);
		return res;
	}
	
	@Override
	public List<HashMap<String, Object>> chatByeMessages() {
		HashMap<String, Object> res =  chatBye();
		if (res == null) return null;
		List<HashMap<String, Object>> msgl = (List<HashMap<String, Object>>)res.get("messages");
		return msgl;
	}
	@Override
	public HashMap<String, Object> chatBye() {
		if (isStatus("wake") || isStatus("bye")) return null;
		//System.out.println(" **CHAT_BYE");
		String url = getUrl("/persona/chat/bye");
		String reqData = "{ \"chid\": \"" + this.chid  + "\", \"event\": \"bye\"}";
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("x-rapidapi-key", this.key);
		headers.put("x-rapidapi-host", getAPIHost());
		headers.put("Accept", "application/json");

		String line = HttpUtil.postDataHttpsJson(url, reqData, null, null, null, headers);
		this.setStatus("bye");
		HashMap<String, Object> res = chatRespParse(line, true);
		return res;
	}	
	private HashMap<String, Object> chatRespParse(String resp, boolean noremote) {
		if (resp == null) return null;

		msg_num_last = msg_num;
		//System.out.println(resp);
		
		List<HashMap<String, Object>> rl = null;
		try {
			JSONObject obj = new JSONObject(resp);
			JSONObject info = obj.getJSONObject("info");
			HashMap<String, Object> rm = new HashMap<>();
			try {
				String chid = info.getString("chid");
				this.chid = chid;
				rm.put("chid", chid);
			} catch (Throwable t) {}
			try {
				String persona_full_name = info.getString("persona_full_name");
				this.persona_full_name = persona_full_name;
				rm.put("from_full_name", persona_full_name);
			} catch (Throwable t) {}
			try {
				String persona = info.getString("persona");
				this.persona = persona;
				rm.put("from", persona);
			} catch (Throwable t) {}
			try {
				String persona_email = info.getString("persona_email");
				this.persona_handle = persona_email;
				rm.put("from_email", persona_email);
			} catch (Throwable t) {}
			
			String num_sent = info.getString("num_sent");
			String num_total = info.getString("num_total");
			//System.out.println("chatRespNUM["+num_sent+"]["+num_total+"] => " + this.chid );
			rm.put("msg_total", num_total);
			rm.put("msg_sent", num_sent);
			
			try {
				JSONArray list = obj.getJSONArray("list");
				if (list != null && list.length() > 0) {
					for (int i=0;i<list.length();i++) {
						// the messages .... 
				//		if (resp.list[i].r == "false" || !resp.list[i].msg) continue; // only if remote add..
						this.setStatus("msg");
						HashMap<String, Object> mm = new HashMap<>();
						JSONObject msg = list.getJSONObject(i);
						String nms [] = JSONObject.getNames(msg);
						for (String n:nms) {
							String val = null;
							try {
								val = msg.getString(n);
							} catch (Throwable t) {
								val = ""+msg.getInt(n);
							}
							//System.out.println(" NAME["+n+"] val: " + val);
							mm.put(n, val);
						}
						String remote = (String)mm.get("r");
					//	System.out.println("   Smsg["+remote+"]["+mm.get("num")+"/"+this.msg_num_last+"] " + mm.get("msg"));
						if (Sutil.compare(remote, "false")) {
							// is remote message
							// FIXME save or not..
							if (noremote) continue;
						}
						int mnum = Sutil.toInt((String)mm.get("num"));
		/*				
						if (mnum <= this.msg_num_last) {
							System.out.println(" ERROR_RESENT["+mnum+"]["+msg_num+"] [sns:"+num_sent+" / "+num_total+"] txt: " + mm.get("msg"));
							continue; // alredy sent
						}
///ERROR_RESENT[2][2] [sns:2 / 1] txt: Bye, have a good night
			*/			
						if (mnum > msg_num) msg_num = mnum;

						String ev = (String)mm.get("event");
						if (Sutil.compare(ev, "bye")) {
							this.setStatus("msg");
						}
						
						//System.out.println(" MSG["+mnum+"]["+msg_num+"] txt: " + mm.get("msg"));

						if (rl == null) rl = new ArrayList<>();
						rl.add(mm);
					}
				}
			} catch (Throwable t) {}
			if (rl != null && rl.size() > 0) rm.put("messages", rl);
			return rm;
		} catch (JSONException e) {
			System.out.println("ERROR SCSedro RESP: " + resp);
		}
		
		return null;
	}
	
	private static String escape(String raw) {
	    String escaped = raw;
	    escaped = escaped.replace("\\", "\\\\");
	    escaped = escaped.replace("\"", "\\\"");
	    escaped = escaped.replace("\b", "\\b");
	    escaped = escaped.replace("\f", "\\f");
	    escaped = escaped.replace("\n", "\\n");
	    escaped = escaped.replace("\r", "\\r");
	    escaped = escaped.replace("\t", "\\t");
	    // TODO: escape other non-printing characters using uXXXX notation
	    return escaped;
	}
	
	
	
}

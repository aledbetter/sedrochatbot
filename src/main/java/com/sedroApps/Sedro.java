package main.java.com.sedroApps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import main.java.com.sedroApps.util.HttpUtil;
import main.java.com.sedroApps.util.Sutil;


public class Sedro {
	private static String rapidapi_host = "inteligent-chatbots.p.rapidapi.com";

	private String persona;
	private String persona_full_name;
	private String persona_handle;	// email/phone/id
	
	private String caller = null;
	private String caller_full_name = null;
	private String caller_handle = null;	// email/phone/id

	private HashMap<String, String> call_info = null;
	
	private String language = null;
	private String context;
	private String chid;
	private String key;
	private String channel_type = null;
	private String caller_token = null;
	private int max_qn = -1;
	
	private int msg_num_last = 0;	
	private int msg_num = 0;	
	private boolean readPublic = false;
	private boolean respPublic = false;
	private boolean directMsg = false;
	
	private List<HashMap<String, Object>> msg = null;
	
	private String status = "wake";
	
	
	
	
	Sedro(boolean readPublic, boolean respPublic, boolean directMsg) {
		this.status = "wake";
		this.readPublic = readPublic;
		this.respPublic = respPublic;
		this.directMsg = directMsg;
	}
	
	//////////////////////////////////////////////////
	// Configuration
	public boolean isReadPublic() {
		return readPublic;
	}
	public boolean isRespPublic() {
		return respPublic;
	}
	public boolean isDirectMsg() {
		return directMsg;
	}
	
	public String getPersona() {
		return persona;
	}	
	public void setPersona(String persona) {
		this.persona = persona;
	}
	public String getPersona_full_name() {
		return persona_full_name;
	}	
	public String getPersona_handle() {
		return persona_handle;
	}	
	public void setPersona_handle(String persona_handle) {
		this.persona_handle = persona_handle;
	}
	
	public String getCaller() {
		return caller;
	}	
	public void setCaller(String caller) {
		this.caller = caller;
	}
	public String getCaller_full_name() {
		return caller_full_name;
	}	
	public void setCaller_full_name(String caller_full_name) {
		this.caller_full_name = caller_full_name;
	}
	public String getCaller_handle() {
		return caller_handle;
	}	
	public void setCaller_handle(String caller_handle) {
		this.caller_handle = caller_handle;
	}
	
	public void setCall_info(HashMap<String, String> call_info) {
		this.call_info = call_info;
	}
	
	
	public String getLanguage() {
		return language;
	}	
	public void setLanguage(String language) {
		this.language = language;
	}
	public String getContext() {
		return context;
	}	
	public void setContext(String context) {
		this.context = context;
	}
	public String getChannel_type() {
		return channel_type;
	}
	public void setChannel_type(String channel_type) {
		this.channel_type = channel_type;
	}
	public void setCaller_token(String caller_token) {
		this.caller_token = caller_token;
	}
	public String getCaller_token() {
		return caller_token;
	}	
	public void setMax_qn(int max_qn) {
		this.max_qn = max_qn;
	}
	public int getMax_qn() {
		return max_qn;
	}
	
	private static String getUrl(String ending) {
		return "https://"+ rapidapi_host+ending;
	}

	//////////////////////////////////////////////////
	// State and status
	public String getStatus() {
		return status;
	}
	public int getMsgNumber() {
		return msg_num;
	}
	
	
	// get current personas
	public static List<String> getPersonas(String key) {
		String url = getUrl("/tenant/personas");
				
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("x-rapidapi-key", key);
		headers.put("x-rapidapi-host", rapidapi_host);
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
		
	public List<HashMap<String, Object>> chatWake(String key) {
		if (!getStatus().equals("wake") && !getStatus().equals("bye")) return null;
		//System.out.println(" **CHAT_WAKE");

		String url = getUrl("/persona/chat/wake");
		this.key = key;
		
		String reqData = "{\"event\": \"wake\""; 		
		reqData += ", \"persona\": \"" + persona  + "\""; 
		if (caller != null) reqData += ", \"user\": \"" + escape(caller) + "\""; 
	    if (caller_token != null) reqData += ", \"caller_token\": \"" + caller_token + "\""; 
	    if (context != null) reqData += ", \"context\": \"" + context  + "\""; 
	    if (language != null) reqData += ", \"language\": \"" + language  + "\""; 
	 //   if (channel_type != null) reqData += ", \"channel_type\": \"" + channel_type  + "\""; 
	 //   if (save) ind += ", \"save\": \"" + save + "\"";  
	    if (max_qn >= 0) reqData += ", \"max_qn\": \"" + max_qn + "\"";

		reqData += "}";
		
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("x-rapidapi-key", key);
		headers.put("x-rapidapi-host", rapidapi_host);
		headers.put("Accept", "application/json");

		String line = HttpUtil.postDataHttpsJson(url, reqData, null, null, null, headers);
		List<HashMap<String, Object>> rl = chatRespParse(line, true);
//FIXME save info for instance
		
		return rl;
	}
	
	public List<HashMap<String, Object>> chatPoll() {
		if (getStatus().equals("wake") || getStatus().equals("bye")) return null;
		//System.out.println(" **CHAT_POLL: " + this.chid);

		String url = getUrl("/persona/chat/poll");
		String reqData = "{ \"chid\": \"" + this.chid  + "\", \"event\": \"poll\"}";
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("x-rapidapi-key", this.key);
		headers.put("x-rapidapi-host", rapidapi_host);
		headers.put("Accept", "application/json");

		String line = HttpUtil.postDataHttpsJson(url, reqData, null, null, null, headers);
		List<HashMap<String, Object>> rl = chatRespParse(line, true);
		return rl;
	}
	
	public List<HashMap<String, Object>> chatMsg(String text) {
		if (getStatus().equals("wake") || getStatus().equals("bye")) return null;
		//System.out.println(" **CHAT_MSG: " + this.chid);

		String url = getUrl("/persona/chat/msg");
		String reqData = "{ \"text\": \"";
	    if (text != null) reqData += escape(text);
	    else reqData += " ";
	    reqData += "\", \"chid\": \"" + this.chid  + "\"}";
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("x-rapidapi-key", this.key);
		headers.put("x-rapidapi-host", rapidapi_host);
		headers.put("Accept", "application/json");

		String line = HttpUtil.postDataHttpsJson(url, reqData, null, null, null, headers);
		List<HashMap<String, Object>> rl = chatRespParse(line, true);
		return rl;
	}
	
	public List<HashMap<String, Object>> chatBye() {
		if (getStatus().equals("wake") || getStatus().equals("bye")) return null;
		//System.out.println(" **CHAT_BYE");
		String url = getUrl("/persona/chat/bye");
		String reqData = "{ \"chid\": \"" + this.chid  + "\", \"event\": \"bye\"}";
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("x-rapidapi-key", this.key);
		headers.put("x-rapidapi-host", rapidapi_host);
		headers.put("Accept", "application/json");

		String line = HttpUtil.postDataHttpsJson(url, reqData, null, null, null, headers);
		List<HashMap<String, Object>> rl = chatRespParse(line, true);
		status = "bye";
		return rl;
	}
	
	private List<HashMap<String, Object>> chatRespParse(String resp, boolean noremote) {
		if (resp == null) return null;

		msg_num_last = msg_num;
		
		List<HashMap<String, Object>> rl = null;
		try {
			JSONObject obj = new JSONObject(resp);
			JSONObject info = obj.getJSONObject("info");
			try {
				String chid = info.getString("chid");
				this.chid = chid;
			} catch (Throwable t) {}
			try {
				String persona_full_name = info.getString("persona_full_name");
				this.persona_full_name = persona_full_name;
			} catch (Throwable t) {}
			try {
				String persona = info.getString("persona");
				this.persona = persona;
			} catch (Throwable t) {}
			try {
				String persona_email = info.getString("persona_email");
				this.persona_handle = persona_email;
			} catch (Throwable t) {}
			
			String num_sent = info.getString("num_sent");
			String num_total = info.getString("num_total");
			//System.out.println("chatRespNUM["+num_sent+"]["+num_total+"] => " + this.chid );
			
			try {
				JSONArray list = obj.getJSONArray("list");
				if (list != null && list.length() > 0) {
					for (int i=0;i<list.length();i++) {
						// the messages .... 
				//		if (resp.list[i].r == "false" || !resp.list[i].msg) continue; // only if remote add..
						status = "msg";
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
						System.out.println("   Smsg["+remote+"]["+mm.get("num")+"/"+this.msg_num_last+"] " + mm.get("msg"));
						if (Sutil.compare(remote, "false")) {
							// is remote message
							// FIXME save or not..
							if (noremote) continue;
						}

						String ev = (String)mm.get("event");
						if (Sutil.compare(ev, "bye")) {
							status = "msg";
						}
						int mnum = Sutil.toInt((String)mm.get("num"));
						if (mnum > msg_num) msg_num = mnum;
						
						//System.out.println(" MSG["+mnum+"] txt: " + mm.get("msg"));

						if (rl == null) rl = new ArrayList<>();
						rl.add(mm);
					}
				}
			} catch (Throwable t) {}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return rl;
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

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

import main.java.com.sedroApps.adapter.ChatAdapter;
import main.java.com.sedroApps.util.Sutil;


public class SCCall {
	protected String id;
	private ChatAdapter adapter = null;
	
	protected String persona;
	protected String persona_full_name;
	protected String persona_handle;	// email/phone/id
	
	private String caller = null;
	private String caller_full_name = null;
	private String caller_handle = null;	// email/phone/id
	protected double latitude = 0;
	protected double longitude = 0;
	protected String location = null;
	protected String calltime = null;
	protected int tzoffset = -1;
	protected String tzn = null;
	
	protected HashMap<String, String> call_info = null;
	
	protected String context;
	protected String chid;
	protected String key;
	private String language = null;
	private String channel_type = null;
	private String caller_token = null;
	private int max_qn = -1;


	private int call_count = 0;
		
	protected int msg_num_last = 0;	
	protected int msg_num = 0;	
	private boolean readPublic = false;
	private boolean respPublic = false;
	private boolean directMsg = false;
		
	private String status = "wake";
	

	public SCCall(ChatAdapter adapter, boolean readPublic, boolean respPublic, boolean directMsg) {
		this.status = "wake";
		this.id = Sutil.getGUIDNoString();
		this.adapter = adapter; 
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
	public String getId() {
		return id;
	}
	public ChatAdapter getChatService() {
		return adapter;
	}

	//////////////////////////////////////////////////
	// provider info
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

	
	//////////////////////////////////////////////////
	// caller info
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
	
	public void setLocation(double latitude, double longitude, String location) {
		this.location = location;
		this.latitude = latitude;
		this.longitude = longitude;
		if (this.latitude <= 0 || this.longitude <= 0) {
			this.latitude = 0;
			this.longitude = 0;
		}
	}
	public void setCalltime(String calltime, String tzn, int tzoffset) {
		this.calltime = calltime;
		this.tzoffset = tzoffset;
		this.tzn = tzn;
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
	public void setCall_count(int call_count) {
		this.call_count = call_count;
	}
	public int getCall_count() {
		return call_count;
	}	
	
	// State and status
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public boolean isStatus(String status) {
		return Sutil.compare(this.status, status);
	}
	public int getMsgNumber() {
		return msg_num;
	}
	

	//////////////////////////////////////////////////
	// Implementation to override
	public HashMap<String, Object> chatWake(String key, String text) {
		if (!getStatus().equals("wake") && !getStatus().equals("bye")) return null;
		return null;
	}
	public List<HashMap<String, Object>> chatWakeMessages(String key, String text) {
		if (!getStatus().equals("wake") && !getStatus().equals("bye")) return null;
		return null;
	}
	
	public HashMap<String, Object> chatPoll() {
		if (getStatus().equals("wake") || getStatus().equals("bye")) return null;
		return null;
	}
	public List<HashMap<String, Object>> chatPollMessages() {
		if (getStatus().equals("wake") || getStatus().equals("bye")) return null;
		return null;
	}	
	
	public HashMap<String, Object> chatMsg(String text) {
		if (getStatus().equals("wake") || getStatus().equals("bye")) return null;
		return null;
	}
	public List<HashMap<String, Object>> chatMsgMessages(String text) {
		if (getStatus().equals("wake") || getStatus().equals("bye")) return null;
		return null;
	}
	
	public HashMap<String, Object> chatBye() {
		if (getStatus().equals("wake") || getStatus().equals("bye")) return null;
		status = "bye";
		return null;
	}
	public List<HashMap<String, Object>> chatByeMessages() {
		if (getStatus().equals("wake") || getStatus().equals("bye")) return null;
		status = "bye";
		return null;
	}
	
}

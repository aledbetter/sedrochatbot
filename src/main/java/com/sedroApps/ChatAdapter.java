package main.java.com.sedroApps;

import java.util.HashMap;
import java.util.List;

import main.java.com.sedroApps.util.Sutil;


public class ChatAdapter {
	protected boolean session_per_direct = false;

	public boolean isSession_per_direct() {
		return session_per_direct;
	}
	
	public String getName() {
		return "none";	
	}
	public String getChannel_type() {
		return "chat";	
	}
	public String getContext() {
		return null;	
	}
	public String getLanguage() {
		return null;	
	}
		
	
	public int init(UserAccount ua) {
		// over-ride for each service to set info needed
		String ssession_per_direct = ua.getServiceInfo(getName(), "session_per_direct");
		if (Sutil.compare(ssession_per_direct, "true")) session_per_direct = true;
		
		return 0;
	}
	
	public int disconnnect(UserAccount ua) {
		// over-ride for each service to set info needed
		return 0;
	}
	
	public String postMessage(Sedro proc, String msg) {
		return "NOP";	
	}

	public String sendDirectMessage(Sedro proc, String touser, String msg) {
		return "NOP";			
	}
	
	
	// clear any cache from processing cycle
	public void clearCache() {
	
	}
	
	
	//////////////////////////////////////////////////
	// Polling handlers
	public List<String> getPublicMessages() {
		return null;
	}
	

	// list of messages: from:from user / msg:message text
	// Call identifier: "CID" in each
	public List<HashMap<String, String>> getDirectMessages(Orator orat, Sedro processor) {
		return null;
	}
	
	// get new calls
	public List<HashMap<String, String>> getDirectCall(Orator orat) {
		return null;
	}
	
	
	//////////////////////////////////////////////////
	// Callback for direct recieve and handle
	
	// callback for receive (when deployed with public IP only)
	public List<String> getReceiveMessages(String msg) {
		return null;
	}
	
	// call back for new incomming calls 
	public HashMap<String, String> getReceiveCall() {
		return null;
	}
	
}

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

import main.java.com.sedroApps.SCOrator;
import main.java.com.sedroApps.SCSedro;
import main.java.com.sedroApps.SCUser;
import main.java.com.sedroApps.util.Sutil;


public class ChatAdapter {
	private SCUser user = null;
	private String id = null;
	private SCOrator orat = null;
	
	public ChatAdapter(SCUser user, String id) {
		this.user = user;
		if (id != null) this.id = id;
		else this.id = Sutil.getGUIDNoString();
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
	public SCUser getUser() {
		return user;
	}	
	public String getId() {
		return id;	
	}
	
	public void setOrator(SCOrator o) {
		orat = o;
	}
	public SCOrator getOrator() {
		return orat;
	}
	
	
	public boolean isPublicMsg() {
		return false;
	}
	public boolean isPrivateMsg() {
		return false;
	}
	public boolean isPolled() {
		return true;	
	}
	
	
	////////////////////////////////////////////
	// Service state information
	public void setServiceState(String element, String value) {
		user.setServiceState(getId(), element, value);
	}
	public String getServiceState(String element) {
		return user.getServiceState(getId(), element);
	}
	public String getServiceInfo(String element) {
		return user.getServiceInfo(getId(), element);
	}
	
	
	public int init(SCUser ua) {
		// over-ride for each service to set info needed
		return 0;
	}
	
	public int disconnnect(SCUser ua) {
		// over-ride for each service to set info needed
		return 0;
	}
	
	public String postMessage(SCSedro proc, String msg) {
		return "NOP";	
	}

	public String sendDirectMessage(SCSedro proc, String touser, String msg) {
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
	public List<HashMap<String, String>> getDirectMessages(SCOrator orat, SCSedro processor) {
		return null;
	}
	
	// get new calls
	public List<HashMap<String, String>> getDirectCall(SCOrator orat) {
		return null;
	}
	
	
	//////////////////////////////////////////////////
	// Callback for direct recieve and handle
	
	// callback for receive (when deployed with public IP only)
	public List<String> getReceiveMessages(String data) {
		return null;
	}
	
	// call back for new incomming calls 
	public HashMap<String, String> getReceiveCall() {
		return null;
	}
	
}

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

package com.sedroApps.adapter;


import java.util.HashMap;

import com.sedroApps.SCCall;
import com.sedroApps.SCUser;



public class ChatWebRest extends ChatAdapter { 
	// this is per user?	
	public ChatWebRest(SCUser user, String id) {
		super(user, id);
	}
	
	@Override
	public String getName() {
		return "webchat";	
	}
	@Override
	public String getChannel_type() {
		return "chat";	
	}	
	@Override
	public boolean isPublicMsg() {
		return false;
	}
	@Override
	public boolean isPrivateMsg() {
		return true;
	}
	@Override
	public boolean isPolled() {
		return false;
	}
		
	///////////////////////////////////////////////////////////////////////////////////////////
	// EXTERNAL calls: init & processing
	@Override
	public int init(SCUser ua) {
		super.init(ua);
    	return 0;
	}
	
	@Override
	public String sendDirectMessage(SCCall proc, String touser, String msg) {
		try {

		} catch (Throwable t) { }
		return "ERROR";			
	}


	@Override
	public HashMap<String, String> getReceiveCall(SCCall call, HashMap<String, String> call_info) {
		if (call == null) {
			// new call
//FIXME
		}
		call_info.put("chat_id", this.getId());
//		call_info.put("date_created", ""+record.getDateCreated().toString(datefmt));
//		call_info.put("time", ""+record.getDateCreated().toString(datefmt));
		//event default "msg"
		if (call_info.get("event") == null) call_info.put("event", "msg");
		
		call_info.put("to", this.getSedroPersona());

	//	String ip = call_info.get("ip_address");
		//call_info.put("id", this.getSedroPersona());
//FIXME this should come from existing call		
		String from = call_info.get("from");
		if (from == null) from = "FIXME";
		call_info.put("from", from);
		call_info.put("caller_handle", from);
		call_info.put("caller", from);
		call_info.put("timezone", from);
		call_info.put("time", from);
		return call_info;
	}
}

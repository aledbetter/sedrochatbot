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


package main.java.com.sedroApps.api;



import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.CookieParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.json.JSONObject;

import main.java.com.sedroApps.SCCall;
import main.java.com.sedroApps.SCServer;
import main.java.com.sedroApps.adapter.ChatAdapter;
import main.java.com.sedroApps.util.RestResp;
import main.java.com.sedroApps.util.RestUtil;
import main.java.com.sedroApps.util.Sutil;



@Path("/1.0/chat/")
@Produces(MediaType.APPLICATION_JSON)
public class RestChat {

	@POST
	@Path("/wake")
	public Response interactWakePOST(@Context UriInfo info, 
			@Context HttpServletRequest hsr,
			@CookieParam("atok") String cookie_access_key, 
			String body) {
		RestResp rr = new RestResp(info, hsr, null, cookie_access_key, cookie_access_key);
		String language = null, chat_id = null;
		double latitude = 0, longitude = 0;
		String timezone = null, time = null, location = null;
		
		try {
			JSONObject obj = new JSONObject(body);
			chat_id = RestUtil.getJStr(obj, "chat_id");
			String slanguage = RestUtil.getJStr(obj, "language");
			if (RestUtil.paramHave(slanguage)) language = slanguage;
			// location
			String slatitude = RestUtil.getJStr(obj, "latitude");
			if (RestUtil.paramHave(slatitude)) latitude = Sutil.toDouble(slatitude);
			String slongitude = RestUtil.getJStr(obj, "longitude");
			if (RestUtil.paramHave(slongitude)) longitude = Sutil.toDouble(slongitude);
			String slocation = RestUtil.getJStr(obj, "location");
			if (RestUtil.paramHave(slocation)) location = slocation;
			// time
			String stimezone = RestUtil.getJStr(obj, "timezone");
			if (RestUtil.paramHave(stimezone)) timezone = stimezone;
			String stime = RestUtil.getJStr(obj, "time");
			if (RestUtil.paramHave(stime)) time = stime;
		} catch (Throwable t) {
			t.printStackTrace();
		}	
		if (!RestUtil.paramHave(chat_id)) return rr.ret(402); // must have chid

		// Find chat service
		ChatAdapter cs = SCServer.getChatServer().findChatService(chat_id);
		if (cs == null) {
			System.out.println("ERROR WAKE["+chat_id+"] Service NOT FOUND");
			return rr.ret(404);
		}
		//System.out.println("WAKE["+chat_id+"] " + cs.getName());

		HashMap<String, String> call_info = new HashMap<>();
		call_info.put("event", "wake");
		call_info.put("remote_addr", hsr.getRemoteAddr());
		if (language != null) call_info.put("language", language);
		if (latitude != 0) call_info.put("latitude", ""+latitude);
		if (longitude != 0) call_info.put("longitude", ""+longitude);
		if (location != null) call_info.put("location", location);
		if (timezone != null) call_info.put("timezone", timezone);
		if (time != null) call_info.put("time", time);
		
		// complete the call info
		HashMap<String, String> ci = cs.getReceiveCall(null, call_info);
		// process message
		HashMap<String, Object> resp = cs.getOrator().processMessageFull(ci);
		if (resp == null || resp.keySet().size() < 1) {
			// too many sessions or something bad
			return rr.ret(429);
		}
		setResp(rr, resp);
		return rr.ret();
	}
		
	@POST
	@Path("/msg")
	public Response interactMsgPOST(@Context UriInfo info, 
			@Context HttpServletRequest hsr,
			@CookieParam("atok") String cookie_access_key, 
			String body) {
		RestResp rr = new RestResp(info, hsr, null, cookie_access_key, cookie_access_key);
		String text = null, chid = null;
		try {
			JSONObject obj = new JSONObject(body);
			text = RestUtil.getJStr(obj, "text");
			chid = RestUtil.getJStr(obj, "call_id");							
		} catch (Throwable t) {
			t.printStackTrace();
		}	
		if (!RestUtil.paramHave(chid)) return rr.ret(402); // must have chid
		if (!RestUtil.paramHave(text)) return rr.ret();
		if (text != null) text = text.trim();
		//System.out.println("MSG["+chid+"] " + text);
		
		// find the call
		SCCall call = SCServer.getChatServer().findCallByID(chid);
		if (call == null) return rr.ret(404); 
				
		HashMap<String, String> call_info = new HashMap<>();
		call_info.put("event", "msg");
		call_info.put("msg", text);
		call_info.put("call_id", chid);
		call_info.put("remote_addr", hsr.getRemoteAddr());
		
		// complete the call info
		ChatAdapter cs = call.getChatService();
		HashMap<String, String> ci = cs.getReceiveCall(call, call_info);
		// process message
		HashMap<String, Object> resp = cs.getOrator().processMessageFull(ci);
		setResp(rr, resp);
		return rr.ret();
	}
	
	@POST
	@Path("/poll")
	public Response interactGetMsgPOST(@Context UriInfo info, 
			@Context HttpServletRequest hsr,
			@CookieParam("atok") String cookie_access_key, 
			String body) {
		RestResp rr = new RestResp(info, hsr, null, cookie_access_key, cookie_access_key);
		String chid = null;
		try {
			JSONObject obj = new JSONObject(body);
			chid = RestUtil.getJStr(obj, "call_id");
					
		} catch (Throwable t) {
			t.printStackTrace();
		}	
		if (!RestUtil.paramHave(chid)) return rr.ret(402); // must have chid
		
		// find the call
		SCCall call = SCServer.getChatServer().findCallByID(chid);
		if (call == null) return rr.ret(404); 
		
		HashMap<String, String> call_info = new HashMap<>();
		call_info.put("event", "poll");
		call_info.put("call_id", chid);
		call_info.put("remote_addr", hsr.getRemoteAddr());
	
		// complete the call info
		ChatAdapter cs = call.getChatService();
		HashMap<String, String> ci = cs.getReceiveCall(call, call_info);
		// process message
		HashMap<String, Object> resp = cs.getOrator().processMessageFull(ci);
		setResp(rr, resp);
		return rr.ret();
	}
	
	@POST
	@Path("/bye")
	public Response interactByePOST(@Context UriInfo info, 
			@Context HttpServletRequest hsr,
			@CookieParam("atok") String cookie_access_key, 
			String body) {
		RestResp rr = new RestResp(info, hsr, null, cookie_access_key, cookie_access_key);
		String chid = null;
		try {
			JSONObject obj = new JSONObject(body);
			chid = RestUtil.getJStr(obj, "call_id");				
		} catch (Throwable t) {
			t.printStackTrace();
		}	
		if (!RestUtil.paramHave(chid)) return rr.ret(402); // must have chid
		//System.out.println("BYE["+chid+"] ");
		
		// find the call
		SCCall call = SCServer.getChatServer().findCallByID(chid);
		if (call == null) return rr.ret(404); 

		HashMap<String, String> call_info = new HashMap<>();
		call_info.put("event", "bye");
		call_info.put("call_id", chid);
		call_info.put("remote_addr", hsr.getRemoteAddr());

		// complete the call info
		ChatAdapter cs = call.getChatService();
		HashMap<String, String> ci = cs.getReceiveCall(call, call_info);
		// process message
		HashMap<String, Object> resp = cs.getOrator().processMessageFull(ci);
		setResp(rr, resp);
		return rr.ret();
	}
	private void setResp(RestResp rr, HashMap<String, Object> resp) {
		if (resp != null) {
			List<Object> ml = (List<Object>)resp.get("messages");
// TODO cleanup messages ?			
			rr.setList(ml);
			resp.remove("messages");
			resp.remove("chid");
			rr.setInfo(resp);
		} else {
			rr.ret(402);
		}
	}
	
}

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


package com.sedroApps.api;


import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sedroApps.SCServer;
import com.sedroApps.SCTenant;
import com.sedroApps.SCUser;
import com.sedroApps.adapter.ChatAdapter;
import com.sedroApps.util.DButil;
import com.sedroApps.util.RestResp;
import com.sedroApps.util.RestUtil;
import com.sedroApps.util.Sutil;



@Path("/1.0/")
@Produces(MediaType.APPLICATION_JSON)
public class RestAPI {
	public static boolean debug_time = false;
	public static int SESSION_TIME = (60*60*2); // 2 hours
	public static int SESSION_KEEP_TIME = (60*60*48); // 48 hours
	
	@POST
	@Path("/login")
	public Response loginPOST(@Context UriInfo info, 
			@Context HttpServletRequest hsr,
			@CookieParam("atok") String cookie_access_key, 
			String body) {
		RestResp rr = new RestResp(info, hsr, null, cookie_access_key, cookie_access_key);
		String username = null, password = null, keep = null;

		try {
			JSONObject obj = new JSONObject(body);
			username = RestUtil.getJStr(obj, "username");
			password = RestUtil.getJStr(obj, "password"); 
			
			String skeep = RestUtil.getJStr(obj, "keep");
			if (RestUtil.paramHave(skeep)) keep = skeep;
			
		} catch (Throwable t) {
			t.printStackTrace();
		}	
		if (!RestUtil.paramHave(username) || !RestUtil.paramHave(password)) return rr.ret(402);
		
		SCTenant cs = SCServer.login(username, password);
		if (cs == null) return rr.ret(403);
		
		int exp = SESSION_TIME;
		if (Sutil.compare(keep, "true")) exp = SESSION_KEEP_TIME;
				
		// set cookie with atoken
		String atok = Sutil.getGUIDString();	
		NewCookie cook = new  NewCookie("atok", atok, "/", null, null, exp, false);
		Calendar ex = Sutil.getUTCTimePlusSeconds(exp);
		// add atok to session table
		Timestamp expire = new Timestamp(ex.getTimeInMillis());
		DButil.saveSessionKey(atok, cs.getUsername(), cs.getId(), expire);
		return rr.ret(cook);
	}
	@GET
	@Path("/logout")
    public Response logoutGET(@Context UriInfo info, 
			@Context HttpServletRequest hsr, 
    		@CookieParam("atok") String cookie_access_key) { 
		RestResp rr = new RestResp(info, hsr, null, cookie_access_key, cookie_access_key);
		// drop session
		DButil.deleteSessionKey(rr.getAtok());
		// drop cookie
		NewCookie delCookie = new NewCookie("atok", null, "/", null, null, 0, false, true);
		return rr.ret(delCookie);
	}
	@POST
	@Path("/join")
	public Response joinPOST(@Context UriInfo info, 
			@Context HttpServletRequest hsr,
			@CookieParam("atok") String cookie_access_key, 
			String body) {
		RestResp rr = new RestResp(info, hsr, null, cookie_access_key, cookie_access_key);

		String sedro_access_key = null;
		try {
			JSONObject obj = new JSONObject(body);
			sedro_access_key = RestUtil.getJStr(obj, "sedro_access_key");		
		} catch (Throwable t) {
			t.printStackTrace();
		}	
		if (!RestUtil.paramHave(sedro_access_key)) return rr.ret(402);
		//System.out.println("JOIN: " + sedro_access_key);
		SCTenant cs = SCServer.newTenant(sedro_access_key);
		if (cs == null) return rr.ret(403);

		int exp = SESSION_TIME;
		//if (Sutil.compare(keep, "true")) exp = SESSION_KEEP_TIME;
				
		// set cookie with atoken
		String atok = Sutil.getGUIDString();	
		NewCookie cook = new  NewCookie("atok", atok, "/", null, null, exp, false);
		Calendar ex = Sutil.getUTCTimePlusSeconds(exp);
		// add atok to session table
		Timestamp expire = new Timestamp(ex.getTimeInMillis());
		DButil.saveSessionKey(atok, cs.getUsername(), cs.getId(), expire);
		return rr.ret(cook);
	}

	@GET
	@Path("/settings")
    public Response settingsGET(@Context UriInfo info, 
			@Context HttpServletRequest hsr, 
    		@CookieParam("atok") String cookie_access_key) { 
		RestResp rr = new RestResp(info, hsr, null, cookie_access_key, cookie_access_key);
		SCTenant cs = rr.isAuth();
		if (cs == null) return rr.ret(401);
		
		rr.setInfo(cs.getMap());
		return rr.ret();
	}
	@POST
	@Path("/settings")
	public Response settingsPOST(@Context UriInfo info, 
			@Context HttpServletRequest hsr,
			@CookieParam("atok") String cookie_access_key, 
			String body) {
		RestResp rr = new RestResp(info, hsr, null, cookie_access_key, cookie_access_key);
		SCTenant cs = rr.isAuth();
		if (cs == null) return rr.ret(401);

		String sedro_access_key = null, sedro_host = null, username = null, password = null, poll_interval = null;

		try {
			JSONObject obj = new JSONObject(body);
			//sedro_access_key = RestUtil.getJStr(obj, "sedro_access_key");
			sedro_host = RestUtil.getJStr(obj, "sedro_host");
			poll_interval = RestUtil.getJStr(obj, "poll_interval");
			//username = RestUtil.getJStr(obj, "username");
			password = RestUtil.getJStr(obj, "password");			
		} catch (Throwable t) {
			t.printStackTrace();
		}	
		if (!RestUtil.paramHave(password)) password = null;
		//if (!RestUtil.paramHave(username)) username = null;
		//if (!RestUtil.paramHave(sedro_access_key)) sedro_access_key = null;
		if (!RestUtil.paramHave(sedro_host)) sedro_host = null;
		if (!RestUtil.paramHave(poll_interval)) poll_interval = null;
		if (password != null) cs.setPassword(password);
		//if (username != null) cs.setUsername(username);
		if (sedro_host != null) cs.setSedro_host(sedro_host);
		//if (sedro_access_key != null) cs.setSedro_access_key(sedro_access_key);
		//if (sedro_access_key != null) cs.setup(sedro_access_key);
		if (poll_interval != null) {
			cs.setPoll_interval(Sutil.toInt(poll_interval));
			// FIXME must update timer
		}
		cs.save();
		// add all the doc content
		return rr.ret();
	}
	@GET
	@Path("/callbacks")
    public Response callbacksGET(@Context UriInfo info, 
			@Context HttpServletRequest hsr, 
    		@CookieParam("atok") String cookie_access_key) { 
		RestResp rr = new RestResp(info, hsr, null, cookie_access_key, cookie_access_key);
		SCTenant cs = rr.isAuth();
		if (cs == null) return rr.ret(401);
		
		Set<String> ms = cs.getCbMsgNames();
		if (ms != null && ms.size() > 0) {
			List<Object> cbl = new ArrayList<>();
			for (String s: ms) {
				cbl.add(s);
				//System.out.println("CB: " + s);
			}
			rr.setList(cbl);
		}

		return rr.ret();
	}
	
	/*
	 * List services
	 * service/List users
	 * user/add
	 * user/get
	 * user/del
	 * user/update
	 * 
	 * chat/xxx -> copy API from sedro
	 * 
	 * NEXT set
	 *  - controls for users in services
	 *  - interject / add directly to service user msg
	 */
	/////////////////////////////////////////////////////////////////
	//  Get service list
	/////////////////////////////////////////////////////////////////
	@GET
	@Path("/users")
    public Response servicesGET(@Context UriInfo info, 
			@Context HttpServletRequest hsr, 
    		@CookieParam("atok") String cookie_access_key) { 
		RestResp rr = new RestResp(info, hsr, null, cookie_access_key, cookie_access_key);
		SCTenant cs = rr.isAuth();
		if (cs == null) return rr.ret(401);
		
		List<SCUser> ual = cs.getUsers();
		if (ual == null || ual.size() < 1) return rr.ret();
		
		List<HashMap<String, Object>> sl = new ArrayList<>();
		for (SCUser ua:ual) sl.add(ua.getMap());
		rr.addInfo("users", sl);
		return rr.ret();
	}
	
	@GET
	@Path("/user/{user}")
    public Response serviceUserGET(@Context UriInfo info, 
			@Context HttpServletRequest hsr, 
    		@PathParam("user") String user,
    		@CookieParam("atok") String cookie_access_key) { 
		RestResp rr = new RestResp(info, hsr, null, cookie_access_key, cookie_access_key);
		SCTenant cs = rr.isAuth();
		if (cs == null) return rr.ret(401);
		SCUser ua = cs.getUser(user);
		if (ua == null) return rr.ret(404);
		rr.setInfo(ua.getMap());
		return rr.ret();
	}

	@POST
	@Path("/user/add")
	public Response GetServiceUserAddPOST(@Context UriInfo info, 
			@Context HttpServletRequest hsr,
			@CookieParam("atok") String cookie_access_key, 
			String body) {
		RestResp rr = new RestResp(info, hsr, null, cookie_access_key, cookie_access_key);
		String username = null, sedro_persona = null, callback = null;

		try {
			JSONObject obj = new JSONObject(body);
			username = RestUtil.getJStr(obj, "username");
			sedro_persona = RestUtil.getJStr(obj, "sedro_persona");
			callback = RestUtil.getJStr(obj, "callback");
		} catch (Throwable t) {
			t.printStackTrace();
		}	
		if (!RestUtil.paramHave(username)) return rr.ret(402);
		if (!RestUtil.paramHave(sedro_persona)) return rr.ret(402);
		if (!RestUtil.paramHave(callback)) callback = null;
		
		SCTenant cs = rr.isAuth();
		if (cs == null) return rr.ret(401);
		SCUser ua = cs.getUser(username);
		if (ua != null) return rr.ret(409);
			
		ua = cs.addUser(username, true);
		if (ua == null) return rr.ret(500);
		ua.setSedroPersona(sedro_persona);
		if (callback != null) ua.setMessageCb(callback);

		rr.setInfo(ua.getMap());
		
		return rr.ret();
	}
	@POST
	@Path("/user/{user}/del")
	public Response GetServiceUserDelPOST(@Context UriInfo info, 
			@Context HttpServletRequest hsr,
    		@PathParam("user") String user,
			@CookieParam("atok") String cookie_access_key, 
			String body) {
		RestResp rr = new RestResp(info, hsr, null, cookie_access_key, cookie_access_key);
		SCTenant cs = rr.isAuth();
		if (cs == null) return rr.ret(401);
		SCUser ua = cs.getUser(user);
		if (ua == null) return rr.ret(404);
		cs.delUser(user);
		
		// add all the doc content
		return rr.ret();
	}
	@POST
	@Path("/service/{id}/del")
	public Response GetServiceUserDelServicePOST(@Context UriInfo info, 
			@Context HttpServletRequest hsr,
    		@PathParam("id") String id,
			@CookieParam("atok") String cookie_access_key, 
			String body) {
		RestResp rr = new RestResp(info, hsr, null, cookie_access_key, cookie_access_key);
		SCTenant cs = rr.isAuth();
		if (cs == null) return rr.ret(401);
		ChatAdapter ca = cs.findChatService(id);
		if (ca == null) return rr.ret(404);
		ca.getUser().removeChatService(id);
		cs.save();
		// add all the doc content
		return rr.ret();
	}
	
	@POST
	@Path("/user/{user}")
	public Response GetServiceUserUpdatePOST(@Context UriInfo info, 
			@Context HttpServletRequest hsr,
    		@PathParam("user") String user,
			@CookieParam("atok") String cookie_access_key, 
			String body) {
		RestResp rr = new RestResp(info, hsr, null, cookie_access_key, cookie_access_key);
		SCTenant cs = rr.isAuth();
		if (cs == null) return rr.ret(401);
		SCUser ua = cs.getUser(user);
		if (ua == null) return rr.ret(404);
		
		//System.out.println("RESP: " + body);
		try {
			JSONObject obj = new JSONObject(body);			
			try {
			JSONArray jsl = obj.getJSONArray("services");
			if (jsl != null) {
				//System.out.println("SERVICES " + jsl.length());
				for (int i=0;i<jsl.length();i++) {
					JSONObject srv = jsl.getJSONObject(i);
					//System.out.println("SERVICES x " + jsl.length());

					// get the service param info...

					String [] services = JSONObject.getNames(srv);
					String service = services[0];
					
					JSONObject svrcfg = srv.getJSONObject(service);
					String [] params = JSONObject.getNames(svrcfg);
					String id = null;
					for (String p:params) {
						if (p.equals("id")) {
							String val = svrcfg.getString(p);
							id = val;
							break;
						}
					}
					
					// new service
					if (id == null || id.equalsIgnoreCase("new")) {
						id = ua.addChatService(service);
					}
					//System.out.println("SERVICES params["+service+"] " + params.length);

					for (String p:params) {
						if (p.equals("id")) continue;
						String val = svrcfg.getString(p);
						String cur_val = ua.getServiceInfo(id, p);
					//	System.out.println("     params["+p+"] " + val);

						if (!Sutil.compare(val, cur_val)) {
							ua.setServiceInfo(id, p, val);
							//System.out.println("     params["+service+"]["+p+"] " + val);
						}
						// FIXME: what about remove?
					}
				}
			}
			} catch (Throwable t) {}
			
		} catch (Throwable t) {
			t.printStackTrace();
		}	
		ua.save();
		ua.initializeServices();
		return rr.ret();
	}

	
	//////////////////////////////////////////////////////////////////////////////////////
	// Service Specific WebHooks
	//http://chatbot.sedro.xyz/api/1.0/cb/sms_hook/<id>
	//http://chatbot.sedro.xyz/api/1.0/cb/voice_hook/<id>
	@POST
	@Path("/cb/voice_hook/{id}")
	public Response voiceWebHookPOST(@Context UriInfo info, 
			@Context HttpServletRequest hsr,
    		@PathParam("id") String id,
			String body) {
		RestResp rr = new RestResp(info, hsr, null, null, null);
		ChatAdapter ca = SCServer.getServer().findChatService(id);
		if (ca == null) return rr.ret(402);
		ca.getReceiveMessages(body);
		return rr.ret();
	}
	@POST
	@Path("/cb/sms_hook/{id}")
	public Response smsWebHookPOST(@Context UriInfo info, 
			@Context HttpServletRequest hsr,
    		@PathParam("id") String id,
			String body) {
		RestResp rr = new RestResp(info, hsr, null, null, null);
		ChatAdapter ca = SCServer.getServer().findChatService(id);
		if (ca == null) return rr.ret(402);
		ca.getReceiveMessages(body);
		return rr.ret();
	}

}

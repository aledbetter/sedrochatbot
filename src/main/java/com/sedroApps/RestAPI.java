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

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.json.JSONArray;
import org.json.JSONObject;


import main.java.com.sedroApps.util.RestResp;
import main.java.com.sedroApps.util.RestUtil;
import main.java.com.sedroApps.util.Sutil;



@Path("/1.0/")
@Produces(MediaType.APPLICATION_JSON)
public class RestAPI {
	public static boolean debug_time = false;
	
	@POST
	@Path("/login")
	public Response loginPOST(@Context UriInfo info, 
			@Context HttpServletRequest hsr,
			@CookieParam("atok") String cookie_access_key, 
			String body) {
		RestResp rr = new RestResp(info, hsr, null, cookie_access_key, cookie_access_key);
		String username = null, password = null, keep = null;

		SCServer cs = SCServer.getChatServer();		
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
		
		boolean resp = cs.login(username, password);
		if (!resp) return rr.ret(403);
		
		// set cookie
		String atok = Sutil.getGUIDString();
		// add all the doc content
		return rr.ret("atok="+atok+";Path=/");
	}
	@GET
	@Path("/logout")
    public Response logoutGET(@Context UriInfo info, 
			@Context HttpServletRequest hsr, 
    		@CookieParam("atok") String cookie_access_key) { 
		RestResp rr = new RestResp(info, hsr, null, cookie_access_key, cookie_access_key);
		SCServer cs = SCServer.getChatServer();
		cs.logout();
		// drop cookie
		return rr.ret("atok=;Path=/");
	}

	@GET
	@Path("/settings")
    public Response settingsGET(@Context UriInfo info, 
			@Context HttpServletRequest hsr, 
    		@CookieParam("atok") String cookie_access_key) { 
		RestResp rr = new RestResp(info, hsr, null, cookie_access_key, cookie_access_key);
		SCServer cs = SCServer.getChatServer();
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
		SCServer cs = SCServer.getChatServer();

		String sedro_access_key = null, username = null, password = null;

		try {
			JSONObject obj = new JSONObject(body);
			sedro_access_key = RestUtil.getJStr(obj, "sedro_access_key");
			username = RestUtil.getJStr(obj, "username");
			password = RestUtil.getJStr(obj, "password");			
		} catch (Throwable t) {
			t.printStackTrace();
		}	
		if (!RestUtil.paramHave(password)) password = null;
		if (!RestUtil.paramHave(username)) username = null;
		if (!RestUtil.paramHave(sedro_access_key)) sedro_access_key = null;
		if (password != null) cs.setPassword(password);
		if (username != null) cs.setUsername(username);
		if (sedro_access_key != null) cs.setSedro_access_key(sedro_access_key);
		cs.save();
		// add all the doc content
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
		SCServer cs = SCServer.getChatServer();
		List<UserAccount> ual = cs.getUsers();
		if (ual == null || ual.size() < 1) return rr.ret();
		
		List<HashMap<String, Object>> sl = new ArrayList<>();
		for (UserAccount ua:ual) sl.add(ua.getMap());
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
		SCServer cs = SCServer.getChatServer();
		UserAccount ua = cs.getUser(user);
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

		String username = null;

		try {
			JSONObject obj = new JSONObject(body);
			username = RestUtil.getJStr(obj, "username");
		} catch (Throwable t) {
			t.printStackTrace();
		}	
		if (!RestUtil.paramHave(username)) return rr.ret(401);

		SCServer cs = SCServer.getChatServer();
		UserAccount ua = cs.getUser(username);
		if (ua != null) return rr.ret(409);
			
		ua = cs.addUser(username);
		if (ua == null) return rr.ret(500);

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

		SCServer cs = SCServer.getChatServer();
		UserAccount ua = cs.getUser(user);
		if (ua == null) return rr.ret(404);
		
		cs.delUser(user);
		
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
		SCServer cs = SCServer.getChatServer();
		UserAccount ua = cs.getUser(user);
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

					//System.out.println("SERVICES params["+service+"] " + params.length);

					for (String p:params) {
						String val = svrcfg.getString(p);
						String cur_val = ua.getServiceInfo(service, p);
					//	System.out.println("     params["+p+"] " + val);

						if (!Sutil.compare(val, cur_val)) {
							ua.setServiceInfo(service, p, val);
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


}

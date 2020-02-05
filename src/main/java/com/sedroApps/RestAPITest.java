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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
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


import main.java.com.sedroApps.RestBase;
import main.java.com.sedroApps.RestResp;

@Path("/1.0/test/")
@Produces(MediaType.APPLICATION_JSON)
public class RestAPITest extends RestBase {
	public static boolean debug_time = false;
	
	
	/////////////////////////////////////////////////////////////////
	// INTERACT and get back lots of stuff
	/////////////////////////////////////////////////////////////////	
	@POST
	@Path("/interact")
	public Response interactMsgPOST(@Context UriInfo info, 
			@Context HttpServletRequest hsr,
			@CookieParam("atok") String cookie_access_key, 
			String body) {
		RestResp rr = new RestResp(info, hsr, null, cookie_access_key, cookie_access_key);
		if (!checkAuth(rr, "user")) return rr.retNoAuth();

		String text = null, context = null, language = null, channel_type = "chat", channel_id = null, caller_token = null, event = null, style = null, chid = null, user = null, persona = null;
		boolean save_usage = false, ctx_save = false, incoming = true;
		List<String> knowledge = null;
	

		try {
			JSONObject obj = new JSONObject(body);
			text = getJStr(obj, "text");
			String scontext = getJStr(obj, "context"); // the name of the context... needed to save
			if (paramHave(scontext)) context = scontext;
			String slanguage = getJStr(obj, "language");
			if (paramHave(slanguage)) language = slanguage;
			
			String spersona = getJStr(obj, "persona");
			if (paramHave(spersona)) persona = spersona;
			
		} catch (Throwable t) {
			t.printStackTrace();
		}	
		if (text != null) text = text.trim();
		if (text.isEmpty()) text = null;
		if (chid == null) {
			event = "wake";
			if (persona == null || persona.trim().isEmpty()) persona = "sedro"; // default persona
		}
		
		// add all the doc content
		return rr.ret();
	}



}

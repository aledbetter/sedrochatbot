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
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

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
		
		ChatServer cs = ChatServer.getChatServer();
		
		String text = null, context = null, language = null, channel_type = "chat", channel_id = null, caller_token = null, event = null, style = null, chid = null, user = null, persona = null;
		boolean save_usage = false, ctx_save = false, incoming = true;
		List<String> knowledge = null;
	/*

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
		*/
		// add all the doc content
		return rr.ret();
	}
	@GET
	@Path("/logout")
    public Response logoutGET(@Context UriInfo info, 
			@Context HttpServletRequest hsr, 
    		@CookieParam("atok") String cookie_access_key) { 
		RestResp rr = new RestResp(info, hsr, null, cookie_access_key, cookie_access_key);
		ChatServer cs = ChatServer.getChatServer();

		//if (!checkAuth(rr, "user")) return rr.retNoAuth();
		//System.out.println("pool/get["+ctx+"] ");
//FIXME
		return rr.ret();
	}
	
	@GET
	@Path("/settings")
    public Response settingsGET(@Context UriInfo info, 
			@Context HttpServletRequest hsr, 
    		@CookieParam("atok") String cookie_access_key) { 
		RestResp rr = new RestResp(info, hsr, null, cookie_access_key, cookie_access_key);
		ChatServer cs = ChatServer.getChatServer();

		//if (!checkAuth(rr, "user")) return rr.retNoAuth();
		//System.out.println("pool/get["+ctx+"] ");
//FIXME
		return rr.ret();
	}
	@POST
	@Path("/settings")
	public Response settingsPOST(@Context UriInfo info, 
			@Context HttpServletRequest hsr,
			@CookieParam("atok") String cookie_access_key, 
			String body) {
		RestResp rr = new RestResp(info, hsr, null, cookie_access_key, cookie_access_key);
		ChatServer cs = ChatServer.getChatServer();

		String text = null, context = null, language = null, channel_type = "chat", channel_id = null, caller_token = null, event = null, style = null, chid = null, user = null, persona = null;
		boolean save_usage = false, ctx_save = false, incoming = true;
		List<String> knowledge = null;
	/*

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
		*/
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
		//if (!checkAuth(rr, "user")) return rr.retNoAuth();
		//System.out.println("pool/get["+ctx+"] ");
//FIXME
		return rr.ret();
	}
	
	@GET
	@Path("/user/{user}")
    public Response serviceUserGET(@Context UriInfo info, 
			@Context HttpServletRequest hsr, 
    		@PathParam("user") String user,
    		@CookieParam("atok") String cookie_access_key) { 
		RestResp rr = new RestResp(info, hsr, null, cookie_access_key, cookie_access_key);
		//System.out.println("pool/get["+ctx+"] ");
//FIXME
		return rr.ret();
	}

	@POST
	@Path("/user/add")
	public Response GetServiceUserAddPOST(@Context UriInfo info, 
			@Context HttpServletRequest hsr,
			@CookieParam("atok") String cookie_access_key, 
			String body) {
		RestResp rr = new RestResp(info, hsr, null, cookie_access_key, cookie_access_key);

		String text = null, context = null, language = null, channel_type = "chat", channel_id = null, caller_token = null, event = null, style = null, chid = null, user = null, persona = null;
		boolean save_usage = false, ctx_save = false, incoming = true;
		List<String> knowledge = null;
	/*

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
		*/
		// add all the doc content
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

		//String text = null, context = null, language = null, channel_type = "chat", channel_id = null, caller_token = null, event = null, style = null, chid = null, persona = null;
		//boolean save_usage = false, ctx_save = false, incoming = true;
		/*

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
		*/
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

		//String text = null, context = null, language = null, channel_type = "chat", channel_id = null, caller_token = null, event = null, style = null, chid = null, user = null, persona = null;
		//boolean save_usage = false, ctx_save = false, incoming = true;
		/*

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
		*/
		// add all the doc content
		return rr.ret();
	}

	@POST
	@Path("/chat/wake")
	public Response interactWakePOST(@Context UriInfo info, 
			@Context HttpServletRequest hsr,
			@CookieParam("atok") String cookie_access_key, 
			String body) {
		RestResp rr = new RestResp(info, hsr, null, cookie_access_key, cookie_access_key);

		String text = null, context = "itx", language = null, channel_type = "chat", channel_id = null, caller_token = null, style = null, user = null, persona = null;
		boolean save_usage = false, ctx_save = false;
		int max_qn = -1;

		try {
			JSONObject obj = new JSONObject(body);
			String scontext = RestUtil.getJStr(obj, "context"); // the name of the context... needed to save
			if (RestUtil.paramHave(scontext)) context = scontext;
			String slanguage = RestUtil.getJStr(obj, "language");
			if (RestUtil.paramHave(slanguage)) language = slanguage;
			
			persona = RestUtil.getJStr(obj, "persona");
			text = RestUtil.getJStr(obj, "text");

			String suser = RestUtil.getJStr(obj, "user");
			if (RestUtil.paramHave(suser)) user = suser;
			String scaller_token = RestUtil.getJStr(obj, "caller_token");
			if (RestUtil.paramHave(scaller_token)) caller_token = scaller_token;
			
			String schannel_type = RestUtil.getJStr(obj, "channel_type");
			if (RestUtil.paramHave(schannel_type)) channel_type = schannel_type;
			String sstyle = RestUtil.getJStr(obj, "style");
			if (RestUtil.paramHave(sstyle)) style = sstyle;			
			if (RestUtil.paramTrue(RestUtil.getJStr(obj, "save"))) ctx_save = true;
			
			String smax_qn = RestUtil.getJStr(obj, "max_qn");
			if (RestUtil.paramHave(smax_qn)) max_qn = Sutil.toInt(smax_qn);
		
		} catch (Throwable t) {
			t.printStackTrace();
		}	
		if (text != null) text = text.trim();
		if (!RestUtil.paramHave(persona)) {
			// must have persona
			return rr.ret(402);
		}
		/*
		/////////////////////////
		// get tenant
		PTenant tenant = RestUtils.getTenant(hsr);
		if (tenant == null) return rr.ret(402);
		
		// session
		PSession sess = new PSession(tenant.getTid(), null, context, language, text, user, caller_token, persona, channel_type, "wake", channel_id, style, null, max_qn, true, ctx_save, save_usage);	
		SDoc res = Lex.processInteraction(sess);
		if (res == null) return rr.ret(502);
		
		// add all the doc content
		makeMap(rr, res.getCtx(), res);
		*/
		return rr.ret();
	}
	
	
	@POST
	@Path("/chat/msg")
	public Response interactMsgPOST(@Context UriInfo info, 
			@Context HttpServletRequest hsr,
			@CookieParam("atok") String cookie_access_key, 
			String body) {
		RestResp rr = new RestResp(info, hsr, null, cookie_access_key, cookie_access_key);

		String text = null, context = "itx", channel_type = "chat", channel_id = null, event = null, style = null, chid = null;	
		int max_qn = -1;
		try {
			JSONObject obj = new JSONObject(body);
			text = RestUtil.getJStr(obj, "text");
			String scontext = RestUtil.getJStr(obj, "context"); // the name of the context... needed to save
			if (RestUtil.paramHave(scontext)) context = scontext;
			
			String schannel_type = RestUtil.getJStr(obj, "channel_type");
			if (RestUtil.paramHave(schannel_type)) channel_type = schannel_type;
			String sevent = RestUtil.getJStr(obj, "event");
			if (RestUtil.paramHave(sevent)) event = sevent;
			String sstyle = RestUtil.getJStr(obj, "style");
			if (RestUtil.paramHave(sstyle)) style = sstyle;
			String smax_qn = RestUtil.getJStr(obj, "max_qn");
			if (RestUtil.paramHave(smax_qn)) max_qn = Sutil.toInt(smax_qn);
			
			chid = RestUtil.getJStr(obj, "chid");
			
					
		} catch (Throwable t) {
			t.printStackTrace();
		}	
		if (!RestUtil.paramHave(chid)) return rr.ret(402); // must have chid
		if (text != null) text = text.trim();
		/*
		/////////////////////////
		// get tenant
		PTenant tenant = RestUtils.getTenant(hsr);
		if (tenant == null) return rr.ret(402);
		
		// session
		PSession sess = new PSession(tenant.getTid(), chid, context, null, text, null, null, null, channel_type, event, channel_id, style, null, max_qn, true, false, false);	
		SDoc res = Lex.processInteraction(sess);		
		if (res == null) return rr.ret(502);
		
		// add all the doc content
		makeMap(rr, res.getCtx(), res);*/
		return rr.ret();
	}
	
	@POST
	@Path("/chat/poll")
	public Response interactGetMsgPOST(@Context UriInfo info, 
			@Context HttpServletRequest hsr,
			@CookieParam("atok") String cookie_access_key, 
			String body) {
		RestResp rr = new RestResp(info, hsr, null, cookie_access_key, cookie_access_key);

		String context = "itx", channel_type = "chat", channel_id = null, event = null, style = null, chid = null;	
		int max_qn = -1;
		try {
			JSONObject obj = new JSONObject(body);
			String scontext = RestUtil.getJStr(obj, "context"); // the name of the context... needed to save
			if (RestUtil.paramHave(scontext)) context = scontext;
			
			String schannel_type = RestUtil.getJStr(obj, "channel_type");
			if (RestUtil.paramHave(schannel_type)) channel_type = schannel_type;
			String sevent = RestUtil.getJStr(obj, "event");
			if (RestUtil.paramHave(sevent)) event = sevent;
			String sstyle = RestUtil.getJStr(obj, "style");
			if (RestUtil.paramHave(sstyle)) style = sstyle;
			String smax_qn = RestUtil.getJStr(obj, "max_qn");
			if (RestUtil.paramHave(smax_qn)) max_qn = Sutil.toInt(smax_qn);
			
			chid = RestUtil.getJStr(obj, "chid");
					
		} catch (Throwable t) {
			t.printStackTrace();
		}	
		if (!RestUtil.paramHave(chid)) return rr.ret(402); // must have chid
		/*
		/////////////////////////
		// get tenant
		PTenant tenant = RestUtils.getTenant(hsr);
		if (tenant == null) return rr.ret(402);
		
		// session
		PSession sess = new PSession(tenant.getTid(), chid, context, null, null, null, null, null, channel_type, event, channel_id, style, null, max_qn, true, false, false);	
		SDoc res = Lex.processInteraction(sess);		
		if (res == null) return rr.ret(502);
		
		// add all the doc content
		makeMap(rr, res.getCtx(), res);
		
		*/
		return rr.ret();
	}
	@POST
	@Path("/chat/bye")
	public Response interactByePOST(@Context UriInfo info, 
			@Context HttpServletRequest hsr,
			@CookieParam("atok") String cookie_access_key, 
			String body) {
		RestResp rr = new RestResp(info, hsr, null, cookie_access_key, cookie_access_key);

		String context = "itx", channel_type = "chat", channel_id = null, style = null, chid = null;
		int max_qn = -1;
		try {
			JSONObject obj = new JSONObject(body);
			String scontext = RestUtil.getJStr(obj, "context"); // the name of the context... needed to save
			if (RestUtil.paramHave(scontext)) context = scontext;		
			String schannel_type = RestUtil.getJStr(obj, "channel_type");
			if (RestUtil.paramHave(schannel_type)) channel_type = schannel_type;
			String sstyle = RestUtil.getJStr(obj, "style");
			if (RestUtil.paramHave(sstyle)) style = sstyle;	
			String smax_qn = RestUtil.getJStr(obj, "max_qn");
			if (RestUtil.paramHave(smax_qn)) max_qn = Sutil.toInt(smax_qn);
			
			chid = RestUtil.getJStr(obj, "chid");
					
		} catch (Throwable t) {
			t.printStackTrace();
		}	
		if (!RestUtil.paramHave(chid)) return rr.ret(402); // must have chid
		/*
		/////////////////////////
		// get tenant
		PTenant tenant = RestUtils.getTenant(hsr);
		if (tenant == null) return rr.ret(402);
		
		// session
		PSession sess = new PSession(tenant.getTid(), chid, context, null, null, null, null, null, channel_type, "bye", channel_id, style, null, max_qn, true, false, false);	
		SDoc res = Lex.processInteraction(sess);		
		if (res == null) return rr.ret(502);
		
		// add all the doc content
		makeMap(rr, res.getCtx(), res);
		*/
		return rr.ret();
	}

	@POST
	@Path("/ask")
	public Response interactAskPOST(@Context UriInfo info, 
			@Context HttpServletRequest hsr,
			@CookieParam("atok") String cookie_access_key, 
			String body) {
		RestResp rr = new RestResp(info, hsr, null, cookie_access_key, cookie_access_key);

		String text = null, context = "itx", language = null, channel_type = "chat", channel_id = null, caller_token = null, user = null, persona = null;			
		int max_qn = -1;
		try {
			JSONObject obj = new JSONObject(body);
			String scontext = RestUtil.getJStr(obj, "context"); // the name of the context... needed to save
			if (RestUtil.paramHave(scontext)) context = scontext;
			String slanguage = RestUtil.getJStr(obj, "language");
			if (RestUtil.paramHave(slanguage)) language = slanguage;
			
			persona = RestUtil.getJStr(obj, "persona");
			text = RestUtil.getJStr(obj, "text");

			String suser = RestUtil.getJStr(obj, "user");
			if (RestUtil.paramHave(suser)) user = suser;
			String scaller_token = RestUtil.getJStr(obj, "caller_token");
			if (RestUtil.paramHave(scaller_token)) caller_token = scaller_token;
			
			String schannel_type = RestUtil.getJStr(obj, "channel_type");
			if (RestUtil.paramHave(schannel_type)) channel_type = schannel_type;	
			String smax_qn = RestUtil.getJStr(obj, "max_qn");
			if (RestUtil.paramHave(smax_qn)) max_qn = Sutil.toInt(smax_qn);
			
		} catch (Throwable t) {
			t.printStackTrace();
		}	
		if (text != null) text = text.trim();
		if (!RestUtil.paramHave(persona)) return rr.ret(402);	// must have persona
			/*
		/////////////////////////
		// get tenant
		PTenant tenant = RestUtils.getTenant(hsr);
		if (tenant == null) return rr.ret(402);
		
		// session
		PSession sess = new PSession(tenant.getTid(), null, context, language, text, user, caller_token, persona, channel_type, "ask", channel_id, null, null, max_qn, true, false, false);	
		SDoc res = Lex.processInteraction(sess);
		if (res == null) return rr.ret(502);
		*/
		// add all the doc content
		//makeMap(rr, res.getCtx(), res);
		return rr.ret();
	}

	@POST
	@Path("/tell")
	public Response interactTellPOST(@Context UriInfo info, 
			@Context HttpServletRequest hsr,
			@CookieParam("atok") String cookie_access_key, 
			String body) {
		RestResp rr = new RestResp(info, hsr, null, cookie_access_key, cookie_access_key);

		String text = null, context = "itx", language = null, channel_type = "chat", channel_id = null, caller_token = null, user = null, persona = null;	
		int max_qn = -1;
		try {
			JSONObject obj = new JSONObject(body);
			String scontext = RestUtil.getJStr(obj, "context"); // the name of the context... needed to save
			if (RestUtil.paramHave(scontext)) context = scontext;
			String slanguage = RestUtil.getJStr(obj, "language");
			if (RestUtil.paramHave(slanguage)) language = slanguage;
			
			persona = RestUtil.getJStr(obj, "persona");
			text = RestUtil.getJStr(obj, "text");

			String suser = RestUtil.getJStr(obj, "user");
			if (RestUtil.paramHave(suser)) user = suser;
			String scaller_token = RestUtil.getJStr(obj, "caller_token");
			if (RestUtil.paramHave(scaller_token)) caller_token = scaller_token;
			
			String schannel_type = RestUtil.getJStr(obj, "channel_type");
			if (RestUtil.paramHave(schannel_type)) channel_type = schannel_type;		
			String smax_qn = RestUtil.getJStr(obj, "max_qn");
			if (RestUtil.paramHave(smax_qn)) max_qn = Sutil.toInt(smax_qn);
			
		} catch (Throwable t) {
			t.printStackTrace();
		}	
		if (text != null) text = text.trim();
		if (!RestUtil.paramHave(persona)) return rr.ret(402); // must have persona
		/*
		/////////////////////////
		// get tenant
		PTenant tenant = RestUtils.getTenant(hsr);
		if (tenant == null) return rr.ret(402);
	
		// session
		PSession sess = new PSession(tenant.getTid(), null, context, language, text, user, caller_token, persona, channel_type, "tell", channel_id, null, null, max_qn, true, true, false);	
		SDoc res = Lex.processInteraction(sess);
		if (res == null) return rr.ret(502);
		
		// refresh the content
		tenant.refresh(persona);
		
		// add all the doc content
		makeMap(rr, res.getCtx(), res);
		*/
		return rr.ret();
	}

}

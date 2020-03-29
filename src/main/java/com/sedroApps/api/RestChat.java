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


import java.util.ArrayList;
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

import org.json.JSONArray;
import org.json.JSONObject;

import main.java.com.sedroApps.SCServer;
import main.java.com.sedroApps.util.HttpUtil;
import main.java.com.sedroApps.util.RestResp;
import main.java.com.sedroApps.util.RestUtil;
import main.java.com.sedroApps.util.Sutil;



@Path("/1.0/persona/chat/")
@Produces(MediaType.APPLICATION_JSON)
public class RestChat {

	
	/*
	 * get instance OR create new one (SedroCall)
	 * then forward info / get response
	 * 
	 */
	
	
	@POST
	@Path("/wake")
	public Response interactWakePOST(@Context UriInfo info, 
			@Context HttpServletRequest hsr,
			@CookieParam("atok") String cookie_access_key, 
			String body) {
		RestResp rr = new RestResp(info, hsr, null, cookie_access_key, cookie_access_key);
	//	if (!checkAuth(rr, "user")) return rr.retNoAuth();

		String text = null, context = "itx", language = null, channel_type = "chat", channel_id = null,
				caller_token = null, style = null, caller = null, persona = null;
		boolean save_usage = false, ctx_save = false;
		int max_qn = -1, call_count = 0;
		double latitude = 0, longitude = 0;
		String timezone = null, time = null, location = null, tzn = null;
		
		try {
			JSONObject obj = new JSONObject(body);
			String scontext = RestUtil.getJStr(obj, "context"); // the name of the context... needed to save
			if (RestUtil.paramHave(scontext)) context = scontext;
			String slanguage = RestUtil.getJStr(obj, "language");
			if (RestUtil.paramHave(slanguage)) language = slanguage;
			
			persona = RestUtil.getJStr(obj, "persona");
			text = RestUtil.getJStr(obj, "text");

			String scaller = RestUtil.getJStr(obj, "caller");
			if (RestUtil.paramHave(scaller)) caller = scaller;
			String scaller_token = RestUtil.getJStr(obj, "caller_token");
			if (RestUtil.paramHave(scaller_token)) caller_token = scaller_token;
			
			String schannel_type = RestUtil.getJStr(obj, "channel_type");
			if (RestUtil.paramHave(schannel_type)) channel_type = schannel_type;
			String sstyle = RestUtil.getJStr(obj, "style");
			if (RestUtil.paramHave(sstyle)) style = sstyle;			
			if (RestUtil.paramTrue(RestUtil.getJStr(obj, "save"))) ctx_save = true;
			
			String smax_qn = RestUtil.getJStr(obj, "max_qn");
			if (RestUtil.paramHave(smax_qn)) max_qn = Sutil.toInt(smax_qn);
			String scall_count = RestUtil.getJStr(obj, "call_count");
			if (RestUtil.paramHave(scall_count)) call_count = Sutil.toInt(scall_count);
			
			// location
			String slatitude = RestUtil.getJStr(obj, "latitude");
			if (RestUtil.paramHave(slatitude)) latitude = Sutil.toDouble(slatitude);
			String slongitude = RestUtil.getJStr(obj, "longitude");
			if (RestUtil.paramHave(slongitude)) longitude = Sutil.toDouble(slongitude);
			String slocation = RestUtil.getJStr(obj, "location");
			if (RestUtil.paramHave(slocation)) location = slocation;
			// time
			String stimezone = RestUtil.getJStr(obj, "tz");
			if (RestUtil.paramHave(stimezone)) timezone = stimezone;
			String stzn = RestUtil.getJStr(obj, "tzn");
			if (RestUtil.paramHave(stzn)) tzn = stzn;
			String stime = RestUtil.getJStr(obj, "time");
			if (RestUtil.paramHave(stime)) time = stime;

		
		} catch (Throwable t) {
			t.printStackTrace();
		}	
		if (text != null) text = text.trim();
		if (!RestUtil.paramHave(persona)) {
			// must have persona
			return rr.ret(402);
		}
		
		// FIND 
		
	//	ChatAdapter wc = null;
	/*
		/////////////////////////
		// get tenant
		PTenant tenant = RestUtils.getTenant(hsr);
		if (tenant == null) return rr.ret(402);
		
		// session
		PSession sess = new PSession(tenant, tenant.getTid(), null, context, language, text, caller, caller_token, persona, channel_type, "wake", channel_id, style, null, max_qn, true, ctx_save, save_usage);	
		sess.setLocation(latitude, longitude, location);
		sess.setTime(timezone, tzn, time);
		sess.setCall_count(call_count);
		
		SDoc res = Lex.processInteraction(sess);
		if (res == null) return rr.ret(502);
		
		// add all the doc content
		makeMap(rr, res.getCtx(), res);
		*/
		return rr.ret();
	}
	
	
	@POST
	@Path("/msg")
	public Response interactMsgPOST(@Context UriInfo info, 
			@Context HttpServletRequest hsr,
			@CookieParam("atok") String cookie_access_key, 
			String body) {
		RestResp rr = new RestResp(info, hsr, null, cookie_access_key, cookie_access_key);
//		if (!checkAuth(rr, "user")) return rr.retNoAuth();

		String text = null, context = "itx", channel_type = null, channel_id = null, event = null, style = null, chid = null;	
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
  		// get tenant
		PTenant tenant = RestUtils.getTenant(hsr);
		if (tenant == null) return rr.ret(402);
		
		// session
		PSession sess = new PSession(tenant, tenant.getTid(), chid, context, null, text, null, null, null, channel_type, event, channel_id, style, null, max_qn, true, false, false);	
		SDoc res = Lex.processInteraction(sess);		
		if (res == null) return makeDefaultByeMap(rr);
		
		// add all the doc content
		makeMap(rr, res.getCtx(), res);
	*/
		return rr.ret();
	}
	
	@POST
	@Path("/poll")
	public Response interactGetMsgPOST(@Context UriInfo info, 
			@Context HttpServletRequest hsr,
			@CookieParam("atok") String cookie_access_key, 
			String body) {
		RestResp rr = new RestResp(info, hsr, null, cookie_access_key, cookie_access_key);
	//	if (!checkAuth(rr, "user")) return rr.retNoAuth();

		String context = "itx", channel_type = null, channel_id = null, event = null, style = null, chid = null;	
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
		// get tenant
		PTenant tenant = RestUtils.getTenant(hsr);
		if (tenant == null) return rr.ret(402);
		
		// session
		PSession sess = new PSession(tenant, tenant.getTid(), chid, context, null, null, null, null, null, channel_type, event, channel_id, style, null, max_qn, true, false, false);	
		SDoc res = Lex.processInteraction(sess);		
		if (res == null) return makeDefaultByeMap(rr);
		
		// add all the doc content
		makeMap(rr, res.getCtx(), res);
		*/
		return rr.ret();
	}
	@POST
	@Path("/bye")
	public Response interactByePOST(@Context UriInfo info, 
			@Context HttpServletRequest hsr,
			@CookieParam("atok") String cookie_access_key, 
			String body) {
		RestResp rr = new RestResp(info, hsr, null, cookie_access_key, cookie_access_key);
	//	if (!checkAuth(rr, "user")) return rr.retNoAuth();

		String context = "itx", channel_type = null, channel_id = null, style = null, chid = null;
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
		// get tenant
		PTenant tenant = RestUtils.getTenant(hsr);
		if (tenant == null) return rr.ret(402);
		
		// session
		PSession sess = new PSession(tenant, tenant.getTid(), chid, context, null, null, null, null, null, channel_type, "bye", channel_id, style, null, max_qn, true, false, false);	
		SDoc res = Lex.processInteraction(sess);		
		if (res == null) return makeDefaultByeMap(rr);
		
		// add all the doc content
		makeMap(rr, res.getCtx(), res);
	*/
		return rr.ret();
	}
	
}

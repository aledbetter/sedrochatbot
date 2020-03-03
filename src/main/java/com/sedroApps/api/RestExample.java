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

import main.java.com.sedroApps.SCServer;
import main.java.com.sedroApps.SCUser;
import main.java.com.sedroApps.adapter.ChatAdapter;
import main.java.com.sedroApps.util.DButil;
import main.java.com.sedroApps.util.RestResp;
import main.java.com.sedroApps.util.RestUtil;
import main.java.com.sedroApps.util.Sutil;



@Path("/1.0/")
@Produces(MediaType.APPLICATION_JSON)
public class RestExample {
	
	@POST
	@Path("/usertestpost")
	public Response loginPOST(@Context UriInfo info, 
			@Context HttpServletRequest hsr,
			@CookieParam("atok") String cookie_access_key, 
			String body) {
		RestResp rr = new RestResp(info, hsr, null, cookie_access_key, cookie_access_key);
		String username = null;

		SCServer cs = SCServer.getChatServer();		
		try {
			JSONObject obj = new JSONObject(body);
			username = RestUtil.getJStr(obj, "username");
			
			
		} catch (Throwable t) {
			t.printStackTrace();
		}	
		if (!RestUtil.paramHave(username)) return rr.ret(402);
		

		return rr.ret();
	}
	@GET
	@Path("/usertestget")
    public Response logoutGET(@Context UriInfo info, 
			@Context HttpServletRequest hsr, 
    		@CookieParam("atok") String cookie_access_key) { 
		RestResp rr = new RestResp(info, hsr, null, cookie_access_key, cookie_access_key);
		// drop session
		
		
		
		// drop cookie
		return rr.ret();
	}

	
	/////////////////////////////////////////////////////////////////////////////////////////////
	// TEST only for form post
	@POST
	@Path("/testformgetpost")
	public String testPOST(@Context UriInfo info, 
			@Context HttpServletRequest hsr,
    		@CookieParam("atok") String cookie_access_key, 
    		String body) {

		//System.out.println("TEST_FORM_GET_POST: " + body);		
		JSONObject obj = null;
	/*	
		try {
			obj = new JSONObject(body);
			JSONObject fobj = obj.getJSONObject("form");
			JSONObject reqobj = null;
			try {
			reqobj = obj.getJSONObject("request");
			} catch (Throwable t) {};
			String acctnum = "4343434";

			try {
			String persona = obj.getString("persona");
			String chid = obj.getString("chid");
			//String dp_name = obj.getString("dp_name");
			String caller_token = obj.getString("ctok");
			String ctype = obj.getString("type");
			String lang = obj.getString("lang");
			
			String fname = fobj.getString("name");
			} catch (Throwable t) {}

			if (reqobj != null) {
				try {
					// FIXME request info
					//String db_id = obj.getString("db_id");
					JSONArray relem = reqobj.getJSONArray("elements");
					for (int i=0;i<relem.length();i++) {
						JSONObject eo = relem.getJSONObject(i);
						String name = getJStr(eo, "name");
						String type = getJStr(eo, "type");
						String val = getJStr(eo, "val");
						if (name.equals("account number")) acctnum = val;					
					}
				} catch (Throwable t) {}
			}
			
			obj.remove("request");
			obj.remove("ctok");
			obj.remove("lang");
			obj.remove("db_id");
			obj.remove("type");
			obj.put("type", "data");
			
			
			JSONArray elem = fobj.getJSONArray("elements");
			//System.out.println("   FORM[" + fname + "]["+ctype+"]["+caller_token+"]  elements: " + elem.length());		
			for (int i=0;i<elem.length();i++) {
				JSONObject eo = elem.getJSONObject(i);
				try {
				String name = getJStr(eo, "name");
				String type = getJStr(eo, "type");
				//System.out.println("   ELEM: "+fname+"/"+name+" t:"+type);	
				eo.remove("type");
				switch (name) {
				case "name":
				case "full name":
					eo.put("val", "Bart Simpson");
					break;
				case "phone number":
				case "home phone":
				case "work phone":
				case "phone":
				case "mobile phone":
					eo.put("val", "(555) 345-6789");
					break;
				case "account number":
				case "acctnum":
					eo.put("val", acctnum);
					break;
				case "billing address":
				case "mailing address":
				case "home address":
				case "address":
					eo.put("val", "123 N. Alphabet St., Los Angeles, Ca 97045");
					break;
				default:
					eo.put("val", "VAL_"+name);
					eo.put("val2", "VAL2_" + name);
					break;
				}
				} catch (Throwable t) {}
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}	
		if (obj != null) {
			//System.out.println(" RESP: "+obj.toString());	
			return obj.toString();
		}
		*/
		return null;
	}
	
	
	
	/////////////////////////////////////////////////////////////////////////////////////////////
	// TEST only for form post
	@POST
	@Path("/testformdatapost")
	public Response testDataPOST(@Context UriInfo info, 
			@Context HttpServletRequest hsr,
    		@CookieParam("atok") String cookie_access_key, 
    		String body) {
		RestResp rr = new RestResp(info, hsr, null, cookie_access_key, cookie_access_key);
/*
		//System.out.println("TEST_FORM_DATA_POST: " + body);		
		try {
			JSONObject obj = new JSONObject(body);
			String persona = obj.getString("persona");
			String chid = obj.getString("chid");
			//String dp_name = obj.getString("dp_name");
			String caller_token = obj.getString("ctok");
			String lang = obj.getString("lang");
			
			JSONObject fobj = obj.getJSONObject("form");
			String fname = fobj.getString("name");
			String curstate = fobj.getString("curstate");
			String tend = fobj.getString("tend");
			String tstart = fobj.getString("tstart");
			String ctype = obj.getString("type");
			String fid = fobj.getString("id");
			String ftype = fobj.getString("type");

			JSONArray elem = fobj.getJSONArray("elements");
			//System.out.println("   FORM[" + fname + "]["+ctype+"]state["+curstate+"]   elements: " + elem.length());		
			for (int i=0;i<elem.length();i++) {
				JSONObject eo = elem.getJSONObject(i);
				String name = getJStr(eo, "name");
				String type = getJStr(eo, "type");
				String val = getJStr(eo, "val");
				String val2 = getJStr(eo, "val2");
				//if (val2 == null) System.out.println("   ELEM: "+name+"/"+type + " => " + val);		
				//else System.out.println("   ELEM: "+name+"/"+type + " => " + val + " / " + val2);		

			}
		} catch (Throwable t) {
			t.printStackTrace();
		}	
*/
		return rr.ret();
	}
}

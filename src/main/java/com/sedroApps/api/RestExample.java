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
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import main.java.com.sedroApps.util.HttpUtil;
import main.java.com.sedroApps.util.RestResp;
import main.java.com.sedroApps.util.RestUtil;
import main.java.com.sedroApps.util.Sutil;



@Path("/1.0/x/")
@Produces(MediaType.APPLICATION_JSON)
public class RestExample {

    public static HashMap<String, Object> getPhoneInfoGET(String key, String phonenumber) { 
    	HashMap<String, Object> info = null;
		
		String rapidapi_host = "f-sm-jorquera-phone-insights-v1.p.rapidapi.com";
		String url = getUrl(rapidapi_host, "/parse");	
		String reqData = "{\"phone_number\": \"" + phonenumber + "\"}";
		
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("x-rapidapi-key", key);
		headers.put("x-rapidapi-host", rapidapi_host);
		headers.put("Accept", "application/json");
		//System.out.println("PHONE: " + reqData);
		String line = HttpUtil.postDataHttpsJson(url, reqData, null, null, null, headers);
		try {
			//System.out.println("GOT: " + line);
			JSONObject jobj = new JSONObject(line);
			String ccode = RestUtil.getJStr(jobj, "country_code_iso"); // "country_code_iso":"US"
			String location = RestUtil.getJStr(jobj, "location"); // "location":"Washington D.C."
			double latitude = RestUtil.getDouble(jobj, "location_latitude"); //"location_latitude":38.8949549
			double longitude = RestUtil.getDouble(jobj, "location_longitude"); //"location_longitude":-77.0366456
			//String number_of_leading_zeros = RestUtil.getJStr(jobj, "number_of_leading_zeros");
			//String national_number = RestUtil.getJStr(jobj, "national_number");
			String number_type = RestUtil.getJStr(jobj, "number_type"); // "number_type":"FIXED_LINE_OR_MOBILE"
			//String phone_number_e164 = RestUtil.getJStr(jobj, "phone_number_e164"); // "phone_number_e164":"+12025096758"
			//String carrier = RestUtil.getJStr(jobj, "carrier"); // "
			//boolean is_valid_number = RestUtil.getBoolean(jobj, "is_valid_number"); // "number_type":"FIXED_LINE_OR_MOBILE"
			info = new HashMap<>();
			info.put("latitude", latitude);
			info.put("longitude", longitude);
			info.put("country_code", ccode);
			info.put("location", location);
			//info.put("carrier", carrier);
			
		} catch (Throwable tt) {
			tt.printStackTrace();
		}	

		return info;
	}

    public static HashMap<String, Object> getIPInfoGET(String key, String ipaddress) { 
    	HashMap<String, Object> info = null;		
		String rapidapi_host = "ip1.p.rapidapi.com";
		String url = getUrl(rapidapi_host, "/"+ipaddress);
				
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("x-rapidapi-key", key);
		headers.put("x-rapidapi-host", rapidapi_host);
		headers.put("Accept", "application/json");
		String line = HttpUtil.getURLContent(url, headers);
		if (line == null) return null;
		
		//System.out.println("LINE_WORD: " + line);
		try {
			JSONObject jobj = new JSONObject(line);
			JSONObject tm = jobj.getJSONObject("time_zone");
			String tzid = RestUtil.getJStr(tm, "id"); // "id":"America/Chicago"
			String tzabt = RestUtil.getJStr(tm, "abbreviation"); // "abbreviation":"CDT"
			int tzoffset = RestUtil.getInt(tm, "offset"); // "offset":-18000
			String time = RestUtil.getJStr(tm, "time"); // "time":"2019-08-06T11:09:59-05:00"

			String city = RestUtil.getJStr(jobj, "city");
			String region = RestUtil.getJStr(jobj, "region");
			String region_code = RestUtil.getJStr(jobj, "region_code");
			String postal = RestUtil.getJStr(jobj, "postal"); 
			String country = RestUtil.getJStr(jobj, "country"); 
			String country_code = RestUtil.getJStr(jobj, "country_code"); 
			double latitude = RestUtil.getDouble(jobj, "latitude");
			double longitude = RestUtil.getDouble(jobj, "longitude");
			
			info = new HashMap<>();
			info.put("latitude", latitude);
			info.put("longitude", longitude);
			info.put("country_code", country_code);
			info.put("tzoffset", tzoffset);
			info.put("tz", tzid);
			if (city != null) info.put("location", city);
			else if (region != null) info.put("location", region);
			else if (country != null) info.put("location", country);
			//info.put("carrier", carrier);
		} catch (Throwable tt) {}	
		return info;
	}
    
    
    /* WEATHER
     * weatherbit-v1-mashape.p.rapidapi.com
     * var req = unirest("GET", "https://weatherbit-v1-mashape.p.rapidapi.com/current");

  {
	"lang": "en",
	"lon": "<required>",
	"lat": "<required>"
}
{
"data":[1 item
0:{30 items
"wind_cdir":"W"
"state_code":"VA"
"city_name":"Blackstone"
"rh":18
"wind_spd":1.5
"lat":"37"
"wind_cdir_full":"west"
"lon":"-78"
"app_temp":26.75
"dewpt":1.65
"vis":10
"uv":3
"pres":1000.3
"ob_time":"2017-06-03 22:35"
"visibility_val":10000
"sunrise":"09:52:17"
"precip3h":NULL
"timezone":"America/New_York"
"wind_dir":260
"weather":{...}3 items
"datetime":"2017-06-03:22"
"precip":NULL
"station":"KBKT"
"country_code":"US"
"slp":1015.6
"sunset":"00:27:46"
"temp":28.1
"visibility":"10000 Meters"
"clouds":0
"ts":1496527200
}
]
"count":1
}     
     */
/* MOVIE
 * imdb8.p.rapidapi.com
 * var req = unirest("GET", "https://imdb8.p.rapidapi.com/title/find");
{
	"q": "game of thr"
}
{5 items
"@meta":{3 items
"operation":"Search"
"requestId":"2239aac1-3506-4a9c-b506-f2da23faf88f"
"serviceTimeMs":95.528932
}
"@type":"imdb.api.find.response"
"query":"game of thr"
"results":[20 items
0:{...}11 items

16:{6 items
"id":"/title/tt6857128/"
"image":{...}4 items
"title":"Unaired Game of Thrones Prequel Pilot"
"titleType":"tvMovie"
"year":2019
"principals":[...]3 items
}
17:{4 items
"id":"/title/tt10442474/"
"title":"Game of Thorns"
"titleType":"short"
"year":2019
}
18:{7 items
"id":"/title/tt0460780/"
"image":{...}4 items
"runningTimeInMinutes":127
"title":"In the Name of the King: A Dungeon Siege Tale"
"titleType":"movie"
"year":2007
"principals":[...]3 items
}
19:{7 items
"id":"/title/tt2474838/"
"image":{...}4 items
"runningTimeInMinutes":116
"title":"The Name of the Game"
"titleType":"movie"
"year":2016
"principals":[...]3 items
}
]
"types":[2 items
0:"title"
1:"name"
]
}
 */
	private static String getUrl(String host, String ending) {
		return "https://"+ host+ending;
	}
	

	@GET
	@Path("/get")
    public Response getGET(@Context UriInfo info, 
			@Context HttpServletRequest hsr, 
    		@CookieParam("atok") String cookie_access_key) { 
		RestResp rr = new RestResp(info, hsr, null, cookie_access_key, cookie_access_key);
		// drop session
		
		/*
		 * get config from: caller_token and service ?
		 * 
		 */
//		HashMap<String, String> cfg = cs.getConfig();
		String key = "TEST_KEY";

		
		String rapidapi_host = "inteligent-chatbots.p.rapidapi.com";
		String url = getUrl(rapidapi_host, "/tenant/personas");
				
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("x-rapidapi-key", key);
		headers.put("x-rapidapi-host", rapidapi_host);
		headers.put("Accept", "application/json");
		String line = HttpUtil.getURLContent(url, headers);
		if (line == null) return null;
		
		//System.out.println("LINE_WORD: " + line);
		try {
			JSONObject obj = new JSONObject(line);
			JSONArray list = obj.getJSONArray("list");
			if (list != null && list.length() > 0) {
				List<String> pl = new ArrayList<>();
				for (int i=0;i<list.length();i++) {
					pl.add(list.getString(i));
				}
		
			}
		} catch (Throwable tt) {}	
			
		
		// drop cookie
		return rr.ret();
	}

	
	/////////////////////////////////////////////////////////////////////////////////////////////
	// TEST only for form post
	@POST
	@Path("/formget")
	public String fromgetPOST(@Context UriInfo info, 
			@Context HttpServletRequest hsr,
    		@CookieParam("atok") String cookie_access_key, 
    		String body) {

		//System.out.println("TEST_FORM_GET_POST: " + body);		
		JSONObject obj = null;
		
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
						String name = RestUtil.getJStr(eo, "name");
						String type = RestUtil.getJStr(eo, "type");
						String val = RestUtil.getJStr(eo, "val");
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
				String name = RestUtil.getJStr(eo, "name");
				String type = RestUtil.getJStr(eo, "type");
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
		
		
		String key = "TEST_KEY";

		
		String rapidapi_host = "inteligent-chatbots.p.rapidapi.com";
		String url = getUrl(rapidapi_host, "/tenant/personas");
				
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("x-rapidapi-key", key);
		headers.put("x-rapidapi-host", rapidapi_host);
		headers.put("Accept", "application/json");
		String line = HttpUtil.getURLContent(url, headers);
		if (line == null) return null;
		
		//System.out.println("LINE_WORD: " + line);
		try {
			JSONObject jobj = new JSONObject(line);
			JSONArray list = jobj.getJSONArray("list");
			if (list != null && list.length() > 0) {
				List<String> pl = new ArrayList<>();
				for (int i=0;i<list.length();i++) {
					pl.add(list.getString(i));
				}
		
			}
		} catch (Throwable tt) {}	
			

		
		
		
		if (obj != null) {
			//System.out.println(" RESP: "+obj.toString());	
			return obj.toString();
		}
		
		return null;
	}
	
	
	
	/////////////////////////////////////////////////////////////////////////////////////////////
	// TEST only for form post
	@POST
	@Path("/formpost")
	public Response formpostPOST(@Context UriInfo info, 
			@Context HttpServletRequest hsr,
    		@CookieParam("atok") String cookie_access_key, 
    		String body) {
		RestResp rr = new RestResp(info, hsr, null, cookie_access_key, cookie_access_key);

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
				String name = RestUtil.getJStr(eo, "name");
				String type = RestUtil.getJStr(eo, "type");
				String val = RestUtil.getJStr(eo, "val");
				String val2 = RestUtil.getJStr(eo, "val2");
				//if (val2 == null) System.out.println("   ELEM: "+name+"/"+type + " => " + val);		
				//else System.out.println("   ELEM: "+name+"/"+type + " => " + val + " / " + val2);		

			}
		} catch (Throwable t) {
			t.printStackTrace();
		}	
		
		/*
		 * get config from: caller_token and service ?
		 * 
		 */
//		SCServer cs = SCServer.getChatServer();		
//		HashMap<String, String> cfg = cs.getConfig();
		String key = "TEST_KEY";
		
		try {
			JSONObject jobj = new JSONObject(body);
			String username = RestUtil.getJStr(jobj, "username");						
		} catch (Throwable t) {
			t.printStackTrace();
		}	
		
		String rapidapi_host = "inteligent-chatbots.p.rapidapi.com";
		String url = getUrl(rapidapi_host, "/tenant/personas");
				

		String reqData = "{ \"chid\": \"" + "HACL"  + "\", \"event\": \"poll\"}";
		
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("x-rapidapi-key", key);
		headers.put("x-rapidapi-host", rapidapi_host);
		headers.put("Accept", "application/json");

		String line = HttpUtil.postDataHttpsJson(url, reqData, null, null, null, headers);
		try {
			JSONObject obj = new JSONObject(line);
			JSONArray list = obj.getJSONArray("list");
			if (list != null && list.length() > 0) {
				List<String> pl = new ArrayList<>();
				for (int i=0;i<list.length();i++) {
					pl.add(list.getString(i));
				}
		
			}
		} catch (Throwable tt) {}		
		
		return rr.ret();
	}
}

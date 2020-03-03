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

import main.java.com.sedroApps.SCServer;
import main.java.com.sedroApps.util.HttpUtil;
import main.java.com.sedroApps.util.RestResp;
import main.java.com.sedroApps.util.RestUtil;
import main.java.com.sedroApps.util.Sutil;



@Path("/1.0/x/")
@Produces(MediaType.APPLICATION_JSON)
public class RestExample {

    public static HashMap<String, Object> getPhoneInfoGET(String key, String phonenumber) { 
    	if (key == null || phonenumber == null) return null;

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
    	if (key == null || ipaddress == null) return null;

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
    
  //https://rapidapi.com/mvpcapi/api/geo-services-by-mvpc-com?endpoint=apiendpoint_65cc05bc-5f67-40b8-84cf-977da846af11			
    public static HashMap<String, Object> getLocationInfoGET(String key, double lat, double lon) { 
    	if (key == null || lat == 0 || lon == 0) return null;
    	return getWeatherGET(key, lon, lat);
    }
    
    
    public static HashMap<String, Object> getWeatherGET(String key, double lon, double lat) { 
    	if (key == null || lat == 0 || lon == 0) return null;

    	HashMap<String, Object> info = null;
		
		String rapidapi_host = "weatherbit-v1-mashape.p.rapidapi.com";
		
		String url = getUrl(rapidapi_host, "/current?lang=en&lon="+lon+"&lat="+lat+"&units=imperial");	

		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("x-rapidapi-key", key);
		headers.put("x-rapidapi-host", rapidapi_host);
		headers.put("Accept", "application/json");
		
		String line = HttpUtil.getURLContent(url, headers);
		if (line == null) return null;
		
		//String line = HttpUtil.postDataHttpsJson(url, reqData, null, null, null, headers);
		try {
			//System.out.println("GOT: " + line);
			JSONObject jobj = new JSONObject(line);
			JSONArray jl = jobj.getJSONArray("data");
			for (int i=0;i<jl.length();i++) {
				JSONObject we = jl.getJSONObject(i);
				String state_code = RestUtil.getJStr(we, "state_code"); // "state_code":"VA"
				String country_code = RestUtil.getJStr(we, "country_code"); // "country_code":"US"
				String city_name = RestUtil.getJStr(we, "city_name"); // "city_name":"Blackstone"
				String timezone = RestUtil.getJStr(we, "timezone"); // "timezone":"America/New_York"
				//solar_rad 774.1
				//snow: 0
				// "uv":7.59013,
				int rh = RestUtil.getInt(we, "rh"); // "rh":18
				int vis = RestUtil.getInt(we, "vis"); // "vis":10  > kilometer
				
				double wind_spd = RestUtil.getDouble(we, "wind_spd"); // "wind_spd":1.5
				String wind_cdir_full = RestUtil.getJStr(we, "wind_cdir_full"); // "wind_cdir_full":"west"
				double app_temp = RestUtil.getDouble(we, "app_temp"); // "app_temp":26.75
				double temp = RestUtil.getDouble(we, "temp"); // "temp":28.1

				String precip = RestUtil.getJStr(we, "precip"); // "precip":"??"
				//String precip3h = RestUtil.getJStr(jobj, "precip3h"); // "precip3h":"??"
				int clouds = RestUtil.getInt(we, "clouds"); // "clouds":0
				
				String sunrise = RestUtil.getJStr(we, "sunrise"); //"sunrise":"09:52:17"
				String sunset = RestUtil.getJStr(we, "precip"); // "sunset":"00:27:46"
				// weather / description "clear sky"
				JSONObject weather = we.getJSONObject("weather");
				String conditions = null;
				if (weather != null) conditions = RestUtil.getJStr(we, "description");
				
				int aqi = RestUtil.getInt(we, "aqi"); //"aqi":35  -> air quality

				
				info = new HashMap<>();
				info.put("country_code", country_code);
				info.put("province_code", state_code);
				if (city_name != null) info.put("location", city_name);
				info.put("tz", timezone);
				
				info.put("sunrise", sunrise);
				info.put("sunset", sunset);
				
				info.put("precipitation", precip);
				info.put("aqi", aqi);
				info.put("temperature", temp);
				info.put("app_temperature", app_temp);
				info.put("visability", vis);
				info.put("wind", wind_cdir_full + " " + wind_spd);
				info.put("clouds", clouds);
				info.put("conditions", conditions);

				// just the first one
				break;
			}
			
		} catch (Throwable tt) {
			tt.printStackTrace();
		}	

		return info;
	}

    
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
	@Path("/weather")
	public String getWeatherPOST(@Context UriInfo info, 
			@Context HttpServletRequest hsr,
    		@CookieParam("atok") String cookie_access_key, 
    		String body) {

		//System.out.println("TEST_FORM_GET_POST: " + body);		
		JSONObject obj = null, fobj = null;
		JSONArray elem = null, relem = null;
		double lon = 0, lat = 0;
		
		try {
			obj = new JSONObject(body);
			fobj = obj.getJSONObject("form");
			elem = fobj.getJSONArray("elements");			
			try {
			String persona = obj.getString("persona");
			String chid = obj.getString("chid");
			String caller_token = obj.getString("ctok");
			String ctype = obj.getString("type");
			String lang = obj.getString("lang");
			String fname = fobj.getString("name");
			} catch (Throwable t) {}
		} catch (Throwable t) {
			t.printStackTrace();
		}	
		
		JSONObject reqobj = null;
		try {			
		reqobj = obj.getJSONObject("request");
		relem = reqobj.getJSONArray("elements");	
		////////////////////////////////////
		// GET VALUES for params		
		for (int i=0;i<relem.length();i++) {
			JSONObject eo = relem.getJSONObject(i);
			String name = RestUtil.getJStr(eo, "name");
			String val = RestUtil.getJStr(eo, "val");
			//String type = RestUtil.getJStr(eo, "type");
			if (name.equals("latitude")) lat = Sutil.toDouble(val);
			else if (name.equals("longitude")) lon = Sutil.toDouble(val);
		}
		} catch (Throwable t) {}


		// get the KEY
		String key = SCServer.getChatServer().getSedro_access_key();

		////////////////////////////////////
		// get externa info
		HashMap<String, Object> winfo = getWeatherGET(key, lon, lat);		
		
		////////////////////////////////////
		// MAP output params
		try {		
			elem = fobj.getJSONArray("elements");
			for (int i=0;i<elem.length();i++) {
				JSONObject eo = elem.getJSONObject(i);
				try {
				String name = RestUtil.getJStr(eo, "name");
				//String type = RestUtil.getJStr(eo, "type");
				//System.out.println("   ELEM: "+fname+"/"+name+" t:"+type);	
				eo.remove("type");	
				if (winfo == null) continue;
				String val = null;
				String val2 = null;
				if (winfo.get(name) != null) {
					val = (String)winfo.get(name);
					if (winfo.get(name+"2") != null) val2 = (String)winfo.get(name+"2");
				}
				if (val != null) eo.put("val", val);
				if (val2 != null) eo.put("val2", val2);
				} catch (Throwable t) {}
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}	

		////////////////////////////////////
		// CLEAN UP RESPONSE
		try {	
			obj.remove("ctok");
			obj.remove("lang");
			obj.remove("db_id");
			obj.remove("type");
			obj.put("type", "data");
			obj.remove("request");
		} catch (Throwable t) {}	
		
		if (obj != null) {
			//System.out.println(" RESP: "+obj.toString());	
			return obj.toString();
		}
		
		return null;
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

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



@Path("/1.0/x/")
@Produces(MediaType.APPLICATION_JSON)
public class RestExample {
	private static String getUrl(String host, String ending) {
		return "https://"+ host+ending;
	}

	// GET info for PHONE NUMBER
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
			//String number_type = RestUtil.getJStr(jobj, "number_type"); // "number_type":"FIXED_LINE_OR_MOBILE"
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

    // GET INFO for IP ADDRESS
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
			//String tzabt = RestUtil.getJStr(tm, "abbreviation"); // "abbreviation":"CDT"
			int tzoffset = RestUtil.getInt(tm, "offset"); // "offset":-18000
			//String time = RestUtil.getJStr(tm, "time"); // "time":"2019-08-06T11:09:59-05:00"

			String city = RestUtil.getJStr(jobj, "city");
			String region = RestUtil.getJStr(jobj, "region");
			//String region_code = RestUtil.getJStr(jobj, "region_code");
			//String postal = RestUtil.getJStr(jobj, "postal"); 
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
    
    // GET TIMEZONE for LOCATION
  //https://rapidapi.com/mvpcapi/api/geo-services-by-mvpc-com?endpoint=apiendpoint_65cc05bc-5f67-40b8-84cf-977da846af11			
    public static HashMap<String, Object> getLocationInfoGET(String key, double lat, double lon) { 
    	if (key == null || lat == 0 || lon == 0) return null;
    	return getWeatherInfo(key, lon, lat);
    }
      
    // GET WEATHER INFORMATION
    public static HashMap<String, Object> getWeatherInfo(String key, double lon, double lat) { 
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
				info.put("visibility", vis);
				info.put("wind", wind_cdir_full + " " + wind_spd);
				info.put("clouds", ""+clouds);
				info.put("conditions", conditions);

				// just the first one
				break;
			}
			
		} catch (Throwable tt) {
			tt.printStackTrace();
		}	

		return info;
	}
    
    // GET MOVIE LIST 
    // https://rapidapi.com/apidojo/api/imdb8 (500 per month)
    public static List<HashMap<String, String>> getMovieList(String key, String qstr) { 
    	if (key == null || qstr == null || qstr.isEmpty()) return null;
		
		String rapidapi_host = "imdb8.p.rapidapi.com";
		
		//game%20of%20thr
		qstr = RestUtil.encode(qstr);		
		String url = getUrl(rapidapi_host, "/title/find?q=" + qstr);	
		
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("x-rapidapi-key", key);
		headers.put("x-rapidapi-host", rapidapi_host);
		headers.put("Accept", "application/json");
		
		String line = HttpUtil.getURLContent(url, headers);
		if (line == null) return null;
		
		List<HashMap<String, String>> respl = null;
		//System.out.println("GOT: " + line);

		try {		
			JSONObject jobj = new JSONObject(line);
			JSONArray jl = jobj.getJSONArray("results");
			for (int i=0;i<jl.length();i++) {
				JSONObject we = jl.getJSONObject(i);
				String id = RestUtil.getJStr(we, "id"); // "id":"/title/tt6857128/"
				String title = RestUtil.getJStr(we, "title"); // "title":"Unaired Game of Thrones Prequel Pilot"
				System.out.println("  opt: " + title);
				String titleType = RestUtil.getJStr(we, "titleType"); // "titleType":"tvMovie"
				int year = RestUtil.getInt(we, "year"); // "year":2019
				int runningTimeInMinutes = RestUtil.getInt(we, "runningTimeInMinutes"); //"runningTimeInMinutes":127
				String image_url = null, primp = null, role = null;
				int image_height = 0, image_width = 0;
				try {
					JSONObject image = we.getJSONObject("image");
					image_url = RestUtil.getJStr(image, "url");
					image_height = RestUtil.getInt(image, "height");
					image_width = RestUtil.getInt(image, "width");
				} catch (Throwable t) {}
				
				try {
					JSONArray principals = we.getJSONArray("principals");
					for (int p=0;p<principals.length();p++) {
						JSONObject pri = principals.getJSONObject(p);
						primp = RestUtil.getJStr(pri, "name");
						role = RestUtil.getJStr(pri, "category");
						/*"disambiguation":"I",
						"id":"/name/nm0000237/",
						"legacyNameText":"Travolta, John (I)",
						"name":"John Travolta",
						"billing":4,
						"category":"actor",
						"characters":[
							"Vincent Vega"],
							"roles":[{"character":"Vincent Vega",
							"characterId":"/character/ch0515192/"}*/
						break;
						// FIXME more ?
					}
				} catch (Throwable t) {}

				HashMap<String, String> info = new HashMap<>();
				info.put("title", title);
				info.put("title_type", titleType);
				info.put("year", ""+year);				
				if (image_url != null) {
					info.put("image", image_url);				
					info.put("image_width", ""+image_width);				
					info.put("image_height", ""+image_height);				
				}
				if (primp != null) {
					info.put("person_name", primp);				
					info.put("person_role", role);				
				}
				info.put("length", ""+runningTimeInMinutes);
				//System.out.println("  SHOW: " + info.toString());

				// add to list
				if (respl == null) respl = new ArrayList<>();
				respl.add(info);
			}
			
		} catch (Throwable tt) {
			tt.printStackTrace();
		}	

		return respl;
	}

    
    
	
	/////////////////////////////////////////////////////////////////////////////////////////////
	// Get Weather via form
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
		HashMap<String, Object> winfo = getWeatherInfo(key, lon, lat);		
		
		////////////////////////////////////
		// MAP output params FOR SINGLE OBJECT
		// OPTION 1: only what they ask for
		// OPTION 2: everything  we have
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
	// Get Weather via form
	@POST
	@Path("/movies")
	public String getMoviesPOST(@Context UriInfo info, 
			@Context HttpServletRequest hsr,
    		@CookieParam("atok") String cookie_access_key, 
    		String body) {

		//System.out.println("TEST_FORM_GET_POST: " + body);		
		JSONObject obj = null, fobj = null;
		JSONArray elem = null, relem = null;
		String qstr = null;
		
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
			if (name.equals("title")) qstr = val;
		}
		} catch (Throwable t) {}


		// get the KEY
		String key = SCServer.getChatServer().getSedro_access_key();

		////////////////////////////////////
		// get externa info
	   List<HashMap<String, String>> ml = getMovieList(key, qstr);

		////////////////////////////////////
		// MAP output params FOR SINGLE OBJECT
		// OPTION 1: only what they ask for
		// OPTION 2: everything  we have
		try {		
			JSONArray nfl = new JSONArray();

			elem = fobj.getJSONArray("elements");
			
			for (HashMap<String, String> winfo:ml) {
				// for each
				JSONObject nf = new JSONObject();	
				for (int i=0;i<elem.length();i++) {
					JSONObject eo = elem.getJSONObject(i);	
					try {
					String name = RestUtil.getJStr(eo, "name");
					nf.put("name", name);
					//System.out.println("   ELEM: "+fname+"/"+name+" t:"+type);					
					String val = null;
					String val2 = null;
					if (winfo.get(name) != null) {
						val = (String)winfo.get(name);
						if (winfo.get(name+"2") != null) val2 = (String)winfo.get(name+"2");
					}
					if (val != null) nf.put("val", val);
					if (val2 != null) nf.put("val2", val2);
					} catch (Throwable t) {}
				}
				// add to form
				nfl.put(nf);
			}
			// remove old param set, add new list
			obj.remove("form");
			obj.put("form", nfl);
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

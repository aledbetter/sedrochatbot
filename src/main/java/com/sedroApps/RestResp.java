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

import java.util.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "code", "info", "results", "list", "doc" })
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestResp {
	
	private int code;
	@JsonIgnore 
	private transient int time;
	@JsonIgnore
	private transient String version;
	@JsonIgnore
	private transient String timestamp;
	@JsonIgnore
	private transient String call_id;	
	@JsonIgnore
	private transient long startTime; // used to save milliseconds on create of this object
	
	// alternate string for response if needed
	private HashMap<String, String> info; 
	
	// for multiple response sets
	private transient List<HashMap<String, Object>> results = null;
	private transient List<Object> list = null;
	private transient List<HashMap<String, Object>> doc = null;
	
	public RestResp(UriInfo info, 
					HttpServletRequest hsr, 
					MultivaluedMap<String, String> form_params, 
					String atok, 
					String atok_cookie) {
		
		this.time = 0;
		this.timestamp = Gtil.getCurrentTimeStamp();
	
	    //get millis at start
		this.startTime = System.currentTimeMillis();

    	this.setCode(200); // start with no problem always
    	this.setVersion("00_DEV_00"); // FIXME
   	
    	this.call_id = UUID.randomUUID().toString();

    	// get the atok
    	if (atok == null || atok.isEmpty()) {
    		atok = atok_cookie;
    	}    	
	}
	
	// get a cookie
	public String getCookie(HttpServletRequest hsr, String name) {
	    Cookie[] cookies = hsr.getCookies();
	    if (cookies != null) {
	      for (int i = 0; i < cookies.length; i++) {
	        if (cookies[i].getName().equals(name)) {
	          return cookies[i].getValue();
	        }
	      }
	    }
	    return null;
	}
	
	// get time since create
	public int timeMSSoFar() {
		return (int)(System.currentTimeMillis() - this.startTime);
	}
	public void setCode(int code) {
		this.code = code;
	}
	public int getCode() {
		return this.code;
	}

	public Response ret() {
		if (time == 0) done();
		return Response.status(200).entity(this.done()).build(); 
	}
	public Response ret(int code) {
		if (time == 0) done();
		return Response.status(200).entity(this.done(code)).build(); 
	}	
	public Response ret(String param, int code) {
		if (time == 0) done();
		return Response.status(200).entity(this.done(code)).build(); 
	}	
	public Response retNoAuth() {
		if (time == 0) done();
		return Response.status(200).entity(this.done(520)).build(); 
	}
	// Called after API completion to get milliseconds updated for the call time and anything else needed
	public RestResp done(int code) {
		this.setCode(code);
		resultDone();
		return this;
	}
	public RestResp done() {
		resultDone();
		return this;
	}
	public void resultDone() {
		// if there was an error.. clear the results
		if (this.code >= 300 && this.code != 502) {
			this.results = null;
		}
	
		// set the time we took to process
		this.time = (int)(System.currentTimeMillis() - this.startTime);
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}



	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}


	// ResponseData
	@SuppressWarnings("unchecked")
	public void setInfo(HashMap<String, String> data) {
		if (data != null) {
			this.info = (HashMap<String,String>)data.clone();
		} else {
			this.info = null;
		}
	}
	// Add parameter and values one by one into the data
	public void addInfo(String name, String value) {
		if (this.info == null) {
			this.info = new HashMap<String, String>();
		}
		this.info.put(name, value);
	}
	public HashMap<String, String> getInfo() {
		return info;
	}
	
	
	// LIST
	public List<Object> getList() {
		return list;
	}
	public void setList(List<Object> list) {
		if (list != null) {
			this.list = (new ArrayList<Object>(list));
		} else {
			this.list = null;
		}
	}
	
	// Response options
	public List<HashMap<String, Object>> getResults() {
		return results;
	}
	public void setResults(List<HashMap<String, Object>> options) {
		this.results = options;
	}

	// adds a copy
	@SuppressWarnings("unchecked")
	public void addResult(HashMap<String, Object> option) {
		if (option != null) {
			if (this.results == null) {
				this.results = new ArrayList<>();
			}	
			if (option != null) {
				this.results.add((HashMap<String,Object>)option.clone());
			}			
		}
	}
	
	// Doc Response options
	public List<HashMap<String, Object>> getDoc() {
		return doc;
	}
	public void setDoc(List<HashMap<String, Object>> options) {
		this.doc = options;
	}
	// adds actual object, so no mod after or there will be issues
	public void addDoc(HashMap<String, Object> option) {
		if (option != null) {
			if (this.doc == null) {
				this.doc = new ArrayList<HashMap<String, Object>>();
			}	
			if (option != null) {
				this.doc.add(option);
			}			
		}
	}

	public String getCall_id() {
		return call_id;
	}
	
}

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

package main.java.com.sedroApps.util;

import java.util.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "code", "info"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestResp {
	
	private int code;	
	// alternate string for response if needed
	private HashMap<String, Object> info; 
	
	
	public RestResp(UriInfo info, 
					HttpServletRequest hsr, 
					MultivaluedMap<String, String> form_params, 
					String atok, 
					String atok_cookie) {

    	this.setCode(200); // start with no problem always
   	
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
	
	public void setCode(int code) {
		this.code = code;
	}
	public int getCode() {
		return this.code;
	}

	public Response ret() {
		return Response.status(200).entity(this.done()).build(); 
	}
	public Response ret(String cookie) {
		return Response.status(200).header("Set-Cookie", cookie).entity(this.done()).build(); 
	}
	
	public Response ret(int code) {
		return Response.status(200).entity(this.done(code)).build(); 
	}	
	public Response ret(String param, int code) {
		return Response.status(200).entity(this.done(code)).build(); 
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
			
		}
	}


	// ResponseData
	@SuppressWarnings("unchecked")
	public void setInfo(HashMap<String, Object> data) {
		if (data != null) {
			this.info = (HashMap<String,Object>)data.clone();
		} else {
			this.info = null;
		}
	}
	// Add parameter and values one by one into the data
	public void addInfo(String name, Object value) {
		if (this.info == null) {
			this.info = new HashMap<String, Object>();
		}
		this.info.put(name, value);
	}
	public HashMap<String, Object> getInfo() {
		return info;
	}
	



	
}

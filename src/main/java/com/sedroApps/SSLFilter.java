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

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Servlet Filter implementation class SSLFilter
 */
//@WebFilter("/SSLFilter")
public class SSLFilter implements Filter {
	private static boolean isDev = true;
	
    /**
     * Default constructor. 
     */
    public SSLFilter() {

    }

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	
	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 * 
	 * We check in heroku for header 'x-forwarded-proto' == 'https' to see if it is https or http
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest hreq = (HttpServletRequest) request;
        HttpServletResponse hresp = (HttpServletResponse) response;      
    	    	
    	//System.out.println("Filter URL[" + hreq.getRequestURL() + "]["+hreq.getHeader("Host")+"]["+hreq.getHeader("X-Forwarded-For")+"]");
		
       	// API call
    	if (hreq.getRequestURI().startsWith("/api/")) {
        	hresp.addHeader("Cache-Control", "no-cache, must-revalidate"); // no save
			chain.doFilter(request, response);
    		return;
    	}
    	
		// non- of this for those looking for wordpress...
		if (hreq.getRequestURI().endsWith(".php")) {
			hresp.setStatus(HttpServletResponse.SC_FORBIDDEN);
        	return;     			       	
        } else if (hreq.getRequestURI().contains("/?fbclid=")) {
			hresp.setStatus(HttpServletResponse.SC_FORBIDDEN);
        	return;     			       	
        }
		
    	// short form
    	if (
            hreq.getRequestURI().equals("/chat") ||          
            hreq.getRequestURI().equals("/msg") ||
        	hreq.getRequestURI().equals("/api") ||        	
        	hreq.getRequestURI().equals("/terms") ||
        	hreq.getRequestURI().equals("/privacy") ||
        	hreq.getRequestURI().equals("/faq") || 	
        	hreq.getRequestURI().equals("/license") ||
        	hreq.getRequestURI().equals("/status")
        	) {
    		String newPage = null;
    		if (hreq.getRequestURI().equals("/chat")) {
    			// same html for chat and analyzer
    			newPage = "/w/00_DEV_00/"+hreq.getRequestURI().substring(1, hreq.getRequestURI().length()-4) + "analyzer.html";
    		} else {
    			newPage = "/w/00_DEV_00/"+hreq.getRequestURI().substring(1, hreq.getRequestURI().length()) + ".html";
    		}

			hresp.addHeader("Cache-Control", "no-cache, must-revalidate"); // no save   		
    		hreq.getRequestDispatcher(newPage).forward(request, response);
    		return;
    	}

    	if (hreq.getRequestURI().equals("/favicon.ico")) {
        	if (isDev) {
        		hresp.addHeader("Cache-Control", "no-cache, must-revalidate"); // no save
        	} else {
        		hresp.addHeader("Cache-Control", "max-age=99999999, public"); 
        	}
        	chain.doFilter(request, response);
    		return;
    	}
        
    	hresp.addHeader("Cache-Control", "no-cache, must-revalidate"); // no save  
		
		// pass the request along the filter chain
		chain.doFilter(request, response);
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

}

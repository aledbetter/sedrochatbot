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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;


/**
 * Initialize everything we need in the constructor
 */
public class Servlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
		
	static Logger logger = Logger.getLogger(Servlet.class);	
	private static URL proxy_url = null;

	
    /**
     * @see HttpServlet#HttpServlet()
     */
	public Servlet() {
        super();
    	loadService(true);
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}
	
    /**
     * @see HttpServlet#HttpServlet()
     */
	public static void loadService(boolean server) {
		/*
		 * GMT
		 */
		TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
			
 	   	
        ////////////
		// Load and configure the logger
        ////////////
		
        // Set up a simple configuration that logs on the console.
        Logger root = Logger.getRootLogger();
        root.setLevel(Level.ERROR);
        
            	
    	String pu = System.getenv("FIXIE_URL");
    	if (pu != null){
    		try {
				proxy_url = new URL(pu);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	
    	if (proxy_url != null) {
        	System.out.println("Outgoing proxy = " + proxy_url.toString() + " [" + proxy_url.getHost() + "] " + proxy_url.getPort());
    	}
    	    	
        BasicConfigurator.configure();
        logger.setLevel(Level.ERROR); // set the log level to INFO
        System.out.println("LoadService initializing log4j Complete");      
        

    }

	public static URL getProxyUrl() {
		return proxy_url;
	}
	public static String getProxyUser() {
		if (proxy_url == null) return null;
	    String userInfo = proxy_url.getUserInfo();
	    return userInfo.substring(0, userInfo.indexOf(':'));
	}
	public static String getProxyPass() {
		if (proxy_url == null) return null;
	    String userInfo = proxy_url.getUserInfo();
	    return userInfo.substring(userInfo.indexOf(':') + 1);
	}


}

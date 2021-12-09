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

package com.sedroApps;

import java.io.IOException;
import java.util.TimeZone;

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
public class SCServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static SCServer server = null;
	static Logger logger = Logger.getLogger(SCServlet.class);	

	
    /**
     * @see HttpServlet#HttpServlet()
     */
	public SCServlet() {
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
	public static void loadService(boolean svr) {
		TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
			 	   	
        ////////////
		// Load and configure the logger
        ////////////
		
        // Set up a simple configuration that logs on the console.
        Logger root = Logger.getRootLogger();
        root.setLevel(Level.ERROR);       

        BasicConfigurator.configure();
        logger.setLevel(Level.ERROR); // set the log level to INFO
        System.out.println("Sedro Chatbot initializing log4j Complete");   
        
        // create sever instance
        server = new SCServer();

    }

}

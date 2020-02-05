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
	
	private static final int GC_DELAY = (1000)*50;
	private static final int GC_PERIOD = 1000*60*10;

	
	
	static Logger logger = Logger.getLogger(Servlet.class);
	
	private static URL proxy_url = null;
	private static Timer gc_timer = null;
	
	// Logger instance named "LoadUtil".
  //  static Logger logger = Logger.getLogger(LoadUtil.class);
	
    /**
     * @see HttpServlet#HttpServlet()
     */
	public Servlet() {
        super();
    	loadService(true);
    	
    //	logger.info("LoadUtil loading Serices Complete");
//    	Security.addProvider(new BouncyCastleProvider());
    	
    	//////////////////////////////////
    	// garbage collection timer
    	gc_timer = new Timer();
    	gc_timer.scheduleAtFixedRate(new TimerTask() {
	            public void run() {
	                //System.out.println("GC TIMER again");
	    			System.gc(); // memory reduce..
	            }
	        }, GC_DELAY, GC_PERIOD);
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
	/*
    public static void main(String[] args) throws Exception{
        Server server = new Server(Integer.valueOf(System.getenv("PORT")));
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);
        context.addServlet(new ServletHolder(new HelloWorld()),"/*");
        server.start();
        server.join();   
    }
    */
	
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
        //root.addAppender(new ConsoleAppender(
        //	    new PatternLayout(PatternLayout.TTCC_CONVERSION_PATTERN)));
        root.setLevel(Level.ERROR);
        
            	
    	String pu = System.getenv("FIXIE_URL");
    	// TEST
    	if (true) {
    		//pu = "http://fixie:Fa7kH8kVnESrTVZ@velodrome.usefixie.com:80";
    		//http://fixie:ZIL6lqA7GLvu6eZ@velodrome.usefixie.com:80
    	}
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
    	
    	
    	//curl http://welcome.usefixie.com --proxy http://fixie:Fa7kH8kVnESrTVZ@velodrome.usefixie.com:80
    	
        BasicConfigurator.configure();
        logger.setLevel(Level.ERROR); // set the log level to INFO
        System.out.println("LoadService initializing log4j Complete");      
        

    }
//HACK	
//	public static AppCore getCore() {
//		return core;
//	}
//HACK
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

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


import java.io.InputStream;
import java.sql.*;
import java.util.HashMap;

import org.apache.commons.lang3.SerializationUtils;


/*
 * DButil
 * Data Base access and configuration utility for use in all of the RESET calls
 * 
 */
public class DButil {	
	// File
	static private String def_tree_path = "ont/";
	static private boolean useDB = true;
	
    static String prod_user = null;
    static String prod_pass = null;
    static String prod_DB = "jdbc:postgresql://aaip4f08ft38a.cg07na9ichnw.us-east-2.rds.amazonaws.com:5432/postgres";
    static String prod_db = "public";
	static final String JDBC_DRIVER = "org.postgresql.Driver";  //org.postgresql.Driver
	static boolean dbinit = false;
	
	/*
	 *  GET URL: heroku run echo \$JDBC_DATABASE_URL
	 */
    static {
    	System.out.println("DBUtil: start initialize");
        try {      
 		   if (!useDB) {
			   dbinit = true;
			   prod_DB = null;
			   System.out.println("DBUtil: Initialized: local file");
		   } else {  
	        	setupJDBC(); // RDB
		   }		
        } catch (Throwable ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }
    
    public static void setupAmazonSQL() {
    	String RDS_HOSTNAME = System.getenv("RDS_HOSTNAME"); // The hostname of the DB instance.
    	String RDS_PORT = System.getenv("RDS_PORT"); // The port on which the DB instance accepts connections. The default value varies among DB engines.
    	String prod_db = System.getenv("RDS_DB_NAME"); // The database name, ebdb.
    	if (RDS_HOSTNAME != null && RDS_PORT != null) {
	    	prod_DB = "jdbc:postgresql://" + RDS_HOSTNAME + ":"+RDS_PORT+"/"+prod_db;    	
	    	System.out.println("SETUP AmazonDB: " + prod_DB);
    	}
    	// user/pass
    	prod_user = System.getenv("RDS_USERNAME"); // The user name that you configured for your database..
    	prod_pass = System.getenv("RDS_PASSWORD"); // The password that you configured for your database.
    }

    
    private static void setupJDBC() {
	   try{
		   Class.forName(JDBC_DRIVER);
		   setupAmazonSQL();
	   } catch(Exception e){
	      e.printStackTrace();
	   }
	   if (prod_user != null && prod_DB != null) {
		   System.out.println("DBUtil: JDBC Initializing user: " + prod_user);
		   dbinit = true;
		  // dbinit = DBPersistance.createDirTable();
		   if (dbinit) System.out.println("DBUtil: JDBC Initialized: " + prod_DB);
	   }
    }
    public static boolean haveDB() {
    	return dbinit;
    }
    public static String getFilePath() {
    	return def_tree_path;
    }
    public static String getRDBPath() {
    	return prod_DB;
    }    
    
    public static Connection getConnection() {
    	Connection conn = null;
    	if (!dbinit) return null;
    	try {
    		//?allowMultiQueries=true
    		conn = DriverManager.getConnection(prod_DB, prod_user, prod_pass);
    	} catch(Exception e) {
    		
    	}
    	return conn;
    }  
    public static void closeConnection(Connection conn) {
        try {
            if (conn != null) conn.close();
         } catch(SQLException se){
            se.printStackTrace();
         }
    }  
    //      String sql = "CREATE DATABASE STUDENTS";

//    String sql = "CREATE TABLE REGISTRATION (id INTEGER not NULL, first VARCHAR(255), last VARCHAR(255), age INTEGER, PRIMARY KEY ( id ))"; 
    public static boolean createTable(String sql) {
    	Connection conn = getConnection();
    	if (conn == null) return false;
    	
    	Statement stmt = null;
    	try {
    		stmt = conn.createStatement();
    		stmt.executeUpdate(sql);
    	} catch (Exception e) {
    		return false;
    	} finally {
    		try {
    			if (stmt != null) conn.close();
    		} catch(SQLException se) { }// do nothing
    		closeConnection(conn);
    	}
    	return true;
    }
    
    
    public static void save(String key, HashMap<String, Object> data) {
    	if (data == null || key == null) return;
    	byte[] dataFlat = SerializationUtils.serialize(data);

    	// FIXME 
    	
    }
    public static HashMap<String, Object> load(String key) {
    	if (key == null) return null;

    
    	InputStream data = null;
    	// FIXME get daa
    	if (data == null) return null;

		@SuppressWarnings({ "unchecked", "unused" })
		HashMap<String, Object> obj = (HashMap<String, Object>)SerializationUtils.deserialize(data);
    	return obj;
    }
    
}


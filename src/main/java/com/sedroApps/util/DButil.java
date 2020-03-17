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


import java.io.IOException;
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
	public static final String DEFAULT_CYR_KEY = "Sedro Know's All";
	public static final String SINGLE_KEY = "chatserver";
	// save all to here
	static final String TABLE_NAME = "sedrochatbot";
	static final String SESS_TABLE_NAME = "sedrochatbotSess";
	
    static String prod_user = null;
    static String prod_pass = null;
    static String prod_DB = "jdbc:postgresql://aafbtx2xad9u71.cg07na9ichnw.us-east-2.rds.amazonaws.com:5432/ebdb";
    
    static String prod_db = "public";
	static final String JDBC_DRIVER = "org.postgresql.Driver";  //org.postgresql.Driver
	static boolean dbinit = false;
	static String encrypte_key = null;
	
	/*
	 *  GET URL: heroku run echo \$JDBC_DATABASE_URL
	 */
    static {
    	System.out.println("DBUtil: start initialize");
        try {      
	        setupJDBC(); // RDB	
	   //     dropDataTables();
	        
	        // verify tables
	        if (!haveDBTable(SINGLE_KEY)) {
	        	createSessionTable();
				createDataTable();
	        }
        } catch (Throwable ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }
    
    public static void setupDB() {
    	String RDS_DB_URL = System.getenv("RDS_DB_URL"); // The hostname of the DB instance.
    	if (RDS_DB_URL == null) {
	    	String RDS_HOSTNAME = System.getenv("RDS_HOSTNAME"); // The hostname of the DB instance.
	    	String RDS_PORT = System.getenv("RDS_PORT"); // The port on which the DB instance accepts connections. The default value varies among DB engines.
	    	String prod_db = System.getenv("RDS_DB_NAME"); // The database name, ebdb.
	    	if (RDS_HOSTNAME != null && RDS_PORT != null) {
		    	prod_DB = "jdbc:postgresql://" + RDS_HOSTNAME + ":"+RDS_PORT+"/"+prod_db;    	
		    	System.out.println("SETUP DB: " + prod_DB);
	    	}
    	} else {
	    	prod_DB = RDS_DB_URL;    	
	    	System.out.println("SETUP DBurl: " + prod_DB);
   		
    	}
    	
    	// get encrypte key
    	encrypte_key = System.getenv("ENC_KEY");
    	if (encrypte_key == null) encrypte_key = DEFAULT_CYR_KEY;
    	
    	// user/pass
    	prod_user = System.getenv("RDS_USERNAME"); // The user name that you configured for your database..
    	prod_pass = System.getenv("RDS_PASSWORD"); // The password that you configured for your database.
    }

    
    private static void setupJDBC() {
	   try{
		   Class.forName(JDBC_DRIVER);
		   setupDB();
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

	public static boolean createSessionTable() {
		String sql = "CREATE TABLE IF NOT EXISTS "+SESS_TABLE_NAME+" (key VARCHAR(128) PRIMARY KEY, username VARCHAR(128), expire TIMESTAMP);";
  		Connection conn = DButil.getConnection();
		if (conn == null) {
  			System.out.println("ERROR createDirTable["+SESS_TABLE_NAME+"] connect fail");
  			return false;
  		}
  		try {
  			Statement stmt = conn.createStatement();
		    stmt.executeUpdate(sql);
		} catch (Exception e) {
		//	e.printStackTrace();
			return false;
		}
		//System.out.println("createDirTable["+DIR_NAME+"] Complete");
		return true;
	}
	
	public static Timestamp getSessionKey(String key) {
		if (key == null) return null;
		
 		Connection conn = DButil.getConnection();
  		if (conn == null) return null;
  		String sql = "SELECT * FROM "+SESS_TABLE_NAME + " WHERE key = '" + key+"'";
  		Timestamp ts = null;
  		try {
  			Statement stmt = conn.createStatement();
		    ResultSet rs = stmt.executeQuery(sql);
		    if (rs.next()) {
		    	try {
				    String atok = rs.getString("key");
				    String username = rs.getString("username");
				    ts = rs.getTimestamp("expire");
		    	} catch (Throwable t) {}
			    	rs.close();	
		    }
	  		DButil.closeConnection(conn); 		
		} catch (SQLException e) { 
	  		DButil.closeConnection(conn); 		
			e.printStackTrace();
		}
		return ts;
	}
	public static int saveSessionKey(String atok, String username, Timestamp expire) {
		if (atok == null || username == null) return 0;
		int cnt = 0;
	//	System.out.println("SAVEING[" + key + "] data: " + data.length);

		String sql = "INSERT INTO " + SESS_TABLE_NAME + " (key, username, expire) VALUES(?, ?, ?) "
		+ " ON CONFLICT (key) DO UPDATE SET expire = ?";
		
  		Connection conn = DButil.getConnection();
  		if (conn == null) {
  			System.out.println("ERROR: saveTreeDescDynDB() no db connected");
  			return 0;
  		}
  		try {
  			PreparedStatement pstmt = conn.prepareStatement(sql);
  			conn.setAutoCommit(false);
  			pstmt.setString(1, atok);
  			pstmt.setString(2, username);
  			pstmt.setTimestamp(3, expire);
		
  			/// OR
  			pstmt.setTimestamp(4, expire);								
  			pstmt.addBatch();		

  			pstmt.executeBatch();
  			conn.commit(); 			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		DButil.closeConnection(conn);		
		return cnt;
	} 	
	public static void deleteSessionKey(String atok) {
		if (atok == null) return;
		String sql = "DELETE FROM " + SESS_TABLE_NAME + " WHERE key = '"+atok+"'";
		
  		Connection conn = DButil.getConnection();
  		if (conn == null) {
  			System.out.println("ERROR: saveTreeDescDynDB() no db connected");
  			return;
  		}
  		try {
  			PreparedStatement pstmt = conn.prepareStatement(sql);
  			conn.setAutoCommit(false);
  			pstmt.executeBatch();
  			conn.commit(); 			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		DButil.closeConnection(conn);		
	} 

	
	
	
	public static boolean createDataTable() {
		String sql = "CREATE TABLE IF NOT EXISTS "+TABLE_NAME+" (key VARCHAR(64) PRIMARY KEY, data bytea, sdata bytea);";
  		Connection conn = DButil.getConnection();
		if (conn == null) {
  			System.out.println("ERROR createDirTable["+TABLE_NAME+"] connect fail");
  			return false;
  		}
  		try {
  			Statement stmt = conn.createStatement();
		    stmt.executeUpdate(sql);
		} catch (Exception e) {
			//e.printStackTrace();
			return false;
		}
		//System.out.println("createDirTable["+DIR_NAME+"] Complete");
		return true;
	}
	
	public static boolean dropDataTables() {
		String sql = "DROP TABLE "+TABLE_NAME+", "+SESS_TABLE_NAME+";";
  		Connection conn = DButil.getConnection();
		if (conn == null) {
  			System.out.println("ERROR dropDataTables["+TABLE_NAME+"/"+SESS_TABLE_NAME+"] connect fail");
  			return false;
  		}
  		try {
  			Statement stmt = conn.createStatement();
		    stmt.executeUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		//System.out.println("createDirTable["+DIR_NAME+"] Complete");
		return true;
	}
	private static boolean haveDBTable(String key) {
  		Connection conn = DButil.getConnection();
  		if (conn == null) return false;
  		String sql = "SELECT * FROM "+TABLE_NAME + " WHERE key = '" + key+"'";
  		String label = null;
  		try {
  			Statement stmt = conn.createStatement();
		    ResultSet rs = stmt.executeQuery(sql);
		    if (rs.next()) {
		    	try {
			    label = rs.getString("key");
		    	} catch (Throwable t) {}
			    rs.close();	
		    }
	  		DButil.closeConnection(conn); 	
	  		if (label != null) return true;
	  		return false;
		} catch (SQLException e) { 
	  		DButil.closeConnection(conn); 		
			//e.printStackTrace();
		}
		return false;
	}

    public static void save(String key, HashMap<String, Object> data, HashMap<String, Object> dataState) {
    	if (data == null || key == null) return;
    	if (!haveDB()) return;

    	// flat and encrypted
    	byte[] dataFlat = null;
    	if (dataFlat == null) {
    		dataFlat = SerializationUtils.serialize(data);
    		dataFlat = encrypteData(dataFlat);
    	}
    	
    	byte[] dataStateFlat = null;
    	if (dataState != null) {
    		dataStateFlat = SerializationUtils.serialize(dataState);
    		dataStateFlat = encrypteData(dataStateFlat);
    	}
    	
	//	System.out.println("SAVEING[" + key + "] data: " + data.length);

		String sql = "INSERT INTO " + TABLE_NAME + " (key, data) VALUES(?, ?) "
		+ " ON CONFLICT (key) DO UPDATE SET data = ?";
		
  		Connection conn = DButil.getConnection();
  		if (conn == null) {
  			System.out.println("ERROR: saveTreeDescDynDB() no db connected");
  			return;
  		}
  		try {
  			PreparedStatement pstmt = conn.prepareStatement(sql);
  			conn.setAutoCommit(false);
  			pstmt.setString(1, key);
  			pstmt.setBytes(2, dataFlat);
		
  			/// OR
  			pstmt.setBytes(3, dataFlat);								
  			pstmt.addBatch();		

  			pstmt.executeBatch();
  			conn.commit(); 			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		DButil.closeConnection(conn);		
		return;
    }
    public static HashMap<String, Object> load(String key) {
    	if (key == null) return null;
    	if (!haveDB()) return null;

  		Connection conn = DButil.getConnection();
  		if (conn == null) return null;
  		String sql = "SELECT * FROM "+TABLE_NAME + " WHERE key = '" + key+"'";
  		InputStream data = null, datastate = null;
  		try {
  			Statement stmt = conn.createStatement();
		    ResultSet rs = stmt.executeQuery(sql);
		    if (rs.next()) {
		    	try {
			    String label = rs.getString("key");
				data = rs.getBinaryStream("data");
				datastate = rs.getBinaryStream("data2");
		    	} catch (Throwable t) {}
			    rs.close();	
		    }
	  		DButil.closeConnection(conn); 		
		} catch (SQLException e) { 
	  		DButil.closeConnection(conn); 		
			e.printStackTrace();
		}
  		
    	if (data == null && datastate == null) return null;
    	
    	// use bytes
		try {
			byte[] bdata = new byte[data.available()];
	    	data.read(bdata);
	    	bdata = decrypteData(bdata);
			HashMap<String, Object> obj = (HashMap<String, Object>)SerializationUtils.deserialize(bdata);
			
			if (datastate != null) {
				byte[] bsdata = new byte[datastate.available()];
				bsdata = decrypteData(bsdata);
	// FIXME add to obj
				HashMap<String, Object> objState = (HashMap<String, Object>)SerializationUtils.deserialize(bdata);
				if (objState != null) {
				//	List<HashMap<String, Object>> sl = obj.get("user");
				//	obj.put(objState.get(key));
				}
			}
			
			return obj;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
    }
    
    
    /////////////////////////////////////////
    // ENCRYPT DATA
    private static byte[] encrypteData(byte[] bdata) {
    	if (encrypte_key == null) return bdata;
        String en = EncryptUtil.encryptBytes(encrypte_key, bdata);
        bdata = en.getBytes(); 	
    	return bdata;
    }
    private static byte[]  decrypteData(byte[] bdata) {
    	if (encrypte_key == null) return bdata;
    	String data = new String(bdata);
    	bdata = EncryptUtil.decryptBytes(encrypte_key, data); 	
    	return bdata;
    }
}


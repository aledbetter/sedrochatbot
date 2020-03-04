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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.mindrot.jbcrypt.BCrypt;

import main.java.com.sedroApps.adapter.ChatAdapter;
import main.java.com.sedroApps.msgcb.CbExample;
import main.java.com.sedroApps.msgcb.CbMessage;
import main.java.com.sedroApps.util.DButil;

public class SCServer {
	private static final int DEFAULT_INTERVAL = (1000*60)*1;
	private static final int DEFAULT_DELAY = (1000*10);

	private String username;
	private String password;	
	private int poll_interval = DEFAULT_INTERVAL;
	private boolean init = false;
	private String sedro_access_key;

	List<SCUser> uaList;	// list of users
	
	private String sedro_host = "inteligent-chatbots.p.rapidapi.com";
//	private String sedro_host = "localhost:8080/api/1.0";

	private HashMap<String, CbMessage> msgcbMap = null;
	
	private static SCServer cs = null;
	private static Timer proc_timer = null;

	
	public SCServer() {
		msgcbMap = new HashMap<>();
		
		////////////////////////////////////////////////////////
		// add all the Call backs here (yes its a bit hackish)
		////////////////////////////////////////////////////////
		addCbMsg(new CbExample("example"));
		// ADD ALL
		
		
	}
	
	// so a single static instance (could instaciate in the servlet... if it is always there)
	static {
		cs = new SCServer();		
	}
	
	public static SCServer getChatServer() {
		return cs;
	}
	public String getSedro_access_key() {
		return sedro_access_key;
	}
	public void setSedro_access_key(String sedro_access_key) {
		this.sedro_access_key = sedro_access_key;
	}
	public String getSedro_host() {
		return sedro_host;
	}
	public void setSedro_host(String sedro_host) {
		this.sedro_host = sedro_host;
	}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public int getPoll_interval() {
		return poll_interval;
	}
	public void setPoll_interval(int millis) {
		if (millis < 1000 || this.poll_interval == millis) return;	
		this.poll_interval = millis;
		setTimer();
	}
	
	public CbMessage getCbMsg(String name) {
		return msgcbMap.get(name);
	}
	public void addCbMsg(CbMessage cb) {
		msgcbMap.put(cb.getName(), cb);
	}
	public Set<String> getCbMsgNames() {
		return msgcbMap.keySet();
	}
	
	private void setTimer() {
		if (proc_timer != null) proc_timer.cancel();
		proc_timer = new Timer();
		proc_timer.scheduleAtFixedRate(new TimerTask() {
	            public void run() {
	            	getChatServer().processInterval();
	            }
	        }, DEFAULT_DELAY, poll_interval);
	}
	public void init() {
		if (init) return;
		username = "admin";
		setPassword("admin");
		
		// load if persistance
		load();
		setPassword("admin");

		// setup processing timers to run
		setTimer();	
		init = true;
	}


	public static String hashPassword(String password_plaintext) {
		String salt = BCrypt.gensalt(12);
		String hashed_password = BCrypt.hashpw(password_plaintext, salt);

		return(hashed_password);
	}
	public static boolean checkPassword(String password_plaintext, String stored_hash) {
		boolean password_verified = false;
	//	if(null == stored_hash || !stored_hash.startsWith("$2a$"))
	//		throw new java.lang.IllegalArgumentException("Invalid hash provided for comparison");
		password_verified = BCrypt.checkpw(password_plaintext, stored_hash);

		return(password_verified);
	}
	public void setPassword(String password) {
		synchronized (username) {
			this.password = hashPassword(password);
			//System.out.println("Passxxxx["+password+"]: " + this.password);
		}
	}
	
	public boolean login(String username, String password) {
		if (username == null || password == null) return false;
		synchronized (username) {
			if (!username.equals(this.username)) return false;
			if (checkPassword(password, this.password)) return true;
		}
		return false;
	}
	
	public void processInterval() {
		if (uaList == null) return;
		// process users	
		synchronized (uaList) {
			if (uaList.size() > 0) {
				for (SCUser ua:uaList) {
					ua.process();
				}
			}
		}
	}
	
	public List<SCUser> getUsers() {
		return uaList;
	}
	public SCUser getUser(String username) {
		if (uaList == null) return null;
		synchronized (uaList) {
			if (uaList.size() < 1) return null;
			for (SCUser ua: uaList) {
				if (ua.getCBUsername().equals(username)) return ua;
			}
		}
		return null;
	}
	public SCUser addUser(String username, boolean save) {
		if (uaList == null) uaList = new ArrayList<>();
		synchronized (uaList) {
			SCUser ua = new SCUser(username);
			uaList.add(ua);
			if (save) save();
			return ua;
		}
	}
	public boolean delUser(String username) {
		if (uaList == null) return false;
		synchronized (uaList) {
			if (uaList.size() < 1) return false;
			for (SCUser ua: uaList) {
				if (ua.getCBUsername().equals(username)) {
					uaList.remove(ua);
					save();
					return true;
				}
			}
		}
		return false;
	}
	
	public ChatAdapter findChatService(String id) {
		if (uaList == null) return null;
		synchronized (uaList) {
			if (uaList.size() < 1) return null;
			for (SCUser ua: uaList) {
				ChatAdapter ca = ua.findChatService(id);
				if (ca != null) return ca;
			}
		}
		return null;
	}
	
	
	
	public void save() {

		// Save the data
		HashMap<String, Object> sm = getMap();
		sm.put("password", this.password);
		if (uaList != null) {
			synchronized (uaList) {
				if (uaList.size() > 0) {
					List<HashMap<String, Object>> sl = new ArrayList<>();
					for (SCUser ua:uaList) {
						HashMap<String, Object> um = ua.getMap();
						sl.add(um);
					}
					sm.put("users", sl);
				}
			}
		}
		DButil.save(DButil.SINGLE_KEY, sm, null);
	}
	
	public void load() {
		// Load the data
		HashMap<String, Object> sm = DButil.load(DButil.SINGLE_KEY);
		if (sm == null) return;
		//  load from the data map
		synchronized (username) {
			this.password = (String)sm.get("password");
			this.username = (String)sm.get("username");
			this.sedro_access_key = (String)sm.get("sedro_access_key");
			String host = (String)sm.get("sedro_host");
			if (host != null) this.sedro_host = host;
			setPoll_interval((Integer)sm.get("poll_interval"));
		}
		List<HashMap<String, Object>> uml = (List<HashMap<String, Object>>)sm.get("users");
		if (uml == null || uml.size() < 1) return;
		
		if (uaList == null) uaList = new ArrayList<>();
		synchronized (uaList) {
				for (HashMap<String, Object> um:uml) {
				// load this user
				String un = (String)um.get("username");
				SCUser ua = addUser(un, false);
				ua.load(um);
			}
		}
	}
	
	// get al lthe user info as a map..
	public HashMap<String, Object> getMap() {
		HashMap<String, Object> m = new HashMap<>();
		synchronized (username) {
			m.put("username", this.username);
			m.put("poll_interval", this.poll_interval);
			m.put("sedro_access_key", this.sedro_access_key);
			m.put("sedro_host", this.sedro_host);
			m.put("database", DButil.haveDB());
			m.put("database_path", DButil.getRDBPath());
		}
		return m;
	}
}

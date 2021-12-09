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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import com.sedroApps.adapter.ChatAdapter;
import com.sedroApps.msgcb.CbExample;
import com.sedroApps.msgcb.CbMessage;
import com.sedroApps.util.DButil;

public class SCTenant {
	private static final int DEFAULT_INTERVAL = (1000*60)*1;
	private static final int DEFAULT_DELAY = (1000*10);

	private String username;
	private String password;	
	private int poll_interval = DEFAULT_INTERVAL;
	private boolean init = false;
	private String sedro_access_key;
	private String id;

	private List<SCUser> uaList;	// list of users
	
	private String sedro_host = "https://inteligent-chatbots.p.rapidapi.com";
	private String sedro_hostname = "inteligent-chatbots.p.rapidapi.com";


	private HashMap<String, CbMessage> msgcbMap = null;
	
	private Timer proc_timer = null;

	
	public SCTenant() {
		msgcbMap = new HashMap<>();
		
		////////////////////////////////////////////////////////
		// add all the Call backs here (yes its a bit hackish)
		////////////////////////////////////////////////////////
		addCbMsg(new CbExample("example"));
		// ADD ALL
		
		
	}

	public void init() {
		if (init) return;
		// setup processing timers to run
		this.setTimer();	
		this.init = true;
	}
	public void setDefaults() {
		if (init) return;
		this.setPassword("password");
	}
	
	// connect to sedro and validate info.../ get ID/user
	public boolean setup(String sedro_access_key) {
		HashMap<String, String> hm = SCSedroCall.getTenantId(this, sedro_access_key);
		if (hm == null) {
			System.out.println("SETUP FAIL: "+ sedro_access_key);
			return false;
		}
		this.id = hm.get("id");
		this.username = hm.get("username");
		this.sedro_access_key = sedro_access_key;
		return true;
	}	
	
	
	public String getId() {
		return id;
	}	
	void setId(String id) {
		this.id = id;
	}
	public String getSedro_access_key() {
		return sedro_access_key;
	}
	public String getSedro_host() {
		return sedro_host;
	}
	public void setSedro_host(String sedro_host) {
		this.sedro_host = sedro_host;
		if (sedro_host != null) {
			if (sedro_host.startsWith("https://")) {
				sedro_hostname = sedro_host.substring(8);
			} else if (sedro_host.startsWith("http://")) {
				sedro_hostname = sedro_host.substring(7);
			} else {
				sedro_hostname = sedro_host;
			}
		}
	}
	public String getSedro_hostname() {
		return sedro_hostname;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
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
	
	private SCTenant getSelf() {
		return this;
	}
	private void setTimer() {
		if (proc_timer != null) proc_timer.cancel();
		proc_timer = new Timer();
		proc_timer.scheduleAtFixedRate(new TimerTask() {
	            public void run() {
	            	getSelf().processInterval();           	
	            }
	        }, DEFAULT_DELAY, poll_interval);
	}

	public void setPassword(String password) {
		synchronized (this) {
			this.password = SCServer.hashPassword(password);
			//System.out.println("Passxxxx["+password+"]: " + this.password);
		}
	}

	
	public void processInterval() {
		// process users	
		synchronized (this) {
			if (uaList == null) return;
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
		synchronized (this) {
			if (uaList == null) return null;
			if (uaList.size() < 1) return null;
			for (SCUser ua: uaList) {
				if (ua.getCBUsername().equals(username)) return ua;
			}
		}
		return null;
	}
	public SCUser addUser(String username, boolean save) {
		synchronized (this) {
			if (uaList == null) uaList = new ArrayList<>();
			SCUser ua = new SCUser(this, username);
			uaList.add(ua);
			if (save) save();
			return ua;
		}
	}
	public boolean delUser(String username) {
		synchronized (this) {
			if (uaList == null) return false;
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
		synchronized (this) {
			if (uaList == null) return null;
			if (uaList.size() < 1) return null;
			for (SCUser ua: uaList) {
				ChatAdapter ca = ua.findChatService(id);
				if (ca != null) return ca;
			}
		}
		return null;
	}
	
	public SCCall findCallByID(String id) {
		synchronized (this) {
			if (uaList == null) return null;
			if (uaList.size() < 1) return null;
			for (SCUser ua: uaList) {
				SCCall call = ua.findCallByID(id);
				if (call != null) return call;
			}
		}
		return null;
	}
	
	public void save() {
		synchronized (this) {
			if (this.id == null) return;
			// Save the data
			HashMap<String, Object> sm = getMap();
			sm.put("password", this.password);
			if (uaList != null && uaList.size() > 0) {
				List<HashMap<String, Object>> sl = new ArrayList<>();
				for (SCUser ua:uaList) {
					HashMap<String, Object> um = ua.getMap();
					sl.add(um);
				}
				sm.put("users", sl);
			}
			DButil.save(this.id, sm, null);
		}
	}
	
	public void load(HashMap<String, Object> sm) {
		// Load the data
		if (sm == null) sm = DButil.load(this.id);
		if (sm == null) return;
		//  load from the data map
		synchronized (this) {
				this.password = (String)sm.get("password");
				this.username = (String)sm.get("username");
				this.id = (String)sm.get("id");
				this.sedro_access_key = (String)sm.get("sedro_access_key");
				String host = (String)sm.get("sedro_host");
				if (host != null) this.setSedro_host(host);
				setPoll_interval((Integer)sm.get("poll_interval"));
			
			List<HashMap<String, Object>> uml = (List<HashMap<String, Object>>)sm.get("users");
			if (uml == null || uml.size() < 1) return;
			
			if (uaList == null) uaList = new ArrayList<>();
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
		synchronized (this) {
			m.put("username", this.username);
			m.put("id", this.id);
			m.put("poll_interval", this.poll_interval);
			m.put("sedro_access_key", this.sedro_access_key);
			m.put("sedro_host", this.sedro_host);
			m.put("database", DButil.haveDB());
			m.put("database_path", DButil.getRDBPath());
		}
		return m;
	}
}

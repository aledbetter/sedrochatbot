package main.java.com.sedroApps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import main.java.com.sedroApps.util.DButil;

public class ChatServer {
	private static final int DEFAULT_INTERVAL = (1000*60)*3;

	private String username;
	private String password;	
	private int poll_interval = DEFAULT_INTERVAL;
	private boolean init = false;
	private String sedro_access_key;

	List<UserAccount> uaList;	// list of users
	
	private static ChatServer cs = null;
	private static Timer proc_timer = null;

	
	
	// so a single static instance (could instaciate in the servlet... if it is always there)
	static {
		cs = new ChatServer();
	}
	
	public static ChatServer getChatServer() {
		return cs;
	}
	public String getSedro_access_key() {
		return sedro_access_key;
	}
	public void setSedro_access_key(String sedro_access_key) {
		this.sedro_access_key = sedro_access_key;
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
	public void setPoll_interval(int poll_interval) {
		this.poll_interval = poll_interval;
	}
	public void init() {
		if (init) return;
		username = "admin";
		setPassword("admin");
		
		// load if persistance
		load();
		
		// setup processing timers to run
		proc_timer = new Timer();
		proc_timer.scheduleAtFixedRate(new TimerTask() {
	            public void run() {
	            	getChatServer().processInterval();
	            }
	        }, DEFAULT_INTERVAL, DEFAULT_INTERVAL);
		init = true;
	}
	private String hashPassword(String password) {
		return ""+password.hashCode();
	}
	public void setPassword(String password) {
		password = hashPassword(password);
	}
	
	public boolean login(String username, String password) {
		if (username == null || password == null) return false;
		if (!username.equals(this.username)) return false;
		String hp = hashPassword(password);
		if (hp.equals(this.password)) return true;
		return false;
	}
	
	public void logout() {
		// sure..

	}
	
	public void processInterval() {
		// process users	
		if (uaList != null && uaList.size() > 0) {
			for (UserAccount ua:uaList) {
				ua.process();
			}
		}
	}
	
	public List<UserAccount> getUsers() {
		return uaList;
	}
	public UserAccount getUser(String username) {
		if (uaList == null || uaList.size() < 1) return null;
		for (UserAccount ua: uaList) {
			if (ua.getCBUsername().equals(username)) return ua;
		}
		return null;
	}
	public UserAccount addUser(String username) {
		if (uaList == null) uaList = new ArrayList<>();
		UserAccount ua = new UserAccount(username);
		uaList.add(ua);
		return ua;
	}
	public boolean delUser(String username) {
		if (uaList == null || uaList.size() < 1) return false;
		for (UserAccount ua: uaList) {
			if (ua.getCBUsername().equals(username)) {
				uaList.remove(ua);
				return true;
			}
		}
		return false;
	}
	
	public void save() {
		// Save the data
		HashMap<String, Object> sm = getMap();
		sm.put("password", this.password);
		if (uaList != null && uaList.size() > 0) {
			List<HashMap<String, Object>> sl = new ArrayList<>();
			for (UserAccount ua:uaList) sl.add(ua.getMap());
			sm.put("users", sl);
		}
	
		DButil.save("chatserver", sm);
	}
	public void load() {
		// Load the data
		HashMap<String, Object> sm = DButil.load("chatserver");
		if (sm == null) return;
		//  load from the data map
		this.password = (String)sm.get("password");
		this.username = (String)sm.get("username");
		this.sedro_access_key = (String)sm.get("sedro_access_key");
		this.poll_interval = (Integer)sm.get("poll_interval");
		List<HashMap<String, Object>> uml = (List<HashMap<String, Object>>)sm.get("users");
		if (uml == null || uml.size() < 1) return;
		for (HashMap<String, Object> um:uml) {
			// load this user
			String un = (String)um.get("username");
			UserAccount ua = addUser(un);
			ua.load(um);
		}
	}
	
	// get al lthe user info as a map..
	public HashMap<String, Object> getMap() {
		HashMap<String, Object> m = new HashMap<>();
		m.put("username", this.username);
		m.put("poll_interval", this.poll_interval);
		m.put("sedro_access_key", this.sedro_access_key);

		return m;
	}
}

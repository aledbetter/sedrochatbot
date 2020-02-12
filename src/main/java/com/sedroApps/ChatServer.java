package main.java.com.sedroApps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatServer {

	String username;
	String password;	
	int poll_interval = 360;
	boolean init = false;
	String sedro_access_key;

	List<UserAccount> uaList;
	private static ChatServer cs = null;
	
	// so a single static instance (could instaciate in the servlet... if it is always there)
	static {
		cs = new ChatServer();
	}
	
	public static ChatServer getChatServer() {
		return cs;
	}
	public void init() {
		username = "admin";
		password = "admin";
		load();
	}
	
	public boolean login(String username, String password) {
		if (username == null || password == null) return false;
		if (username.equals(this.username) && password.equals(this.password)) return true;
		return false;
	}
	
	public void logout() {
		// sure..

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
// FIXME checks and sets		
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
	}
	public void load() {
		// Load the data
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

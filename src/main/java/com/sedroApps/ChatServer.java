package main.java.com.sedroApps;

import java.util.ArrayList;
import java.util.List;

public class ChatServer {

	String username;
	String password;	
	int poll_interval = 360;

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
	
	public String login(String username, String password) {
		
		return "ok";
	}
	
	public void logout() {
		

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
	
}

package main.java.com.sedroApps;

import java.util.List;

public class ChatServer {

	String username;
	String password;	
	int poll_interval = 360;
	
	List<UserAccount> uaList;
	SedroInstance sedro;
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
	
}
